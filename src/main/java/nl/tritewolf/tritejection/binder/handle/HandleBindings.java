package nl.tritewolf.tritejection.binder.handle;

import lombok.AllArgsConstructor;
import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.annotations.TriteNamed;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.binder.TriteBinderProcessor;
import nl.tritewolf.tritejection.binder.TriteBinding;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.exceptions.TriteMultipleConstructorException;
import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HandleBindings {

    private final TriteBinderContainer triteBinderContainer;
    private final TriteBinderProcessor triteBinderProcessor;

    public void initBindings() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, TriteMultipleConstructorException, NoTriteBindingException {
        ConcurrentLinkedDeque<TriteBinding> bindingBuilders = this.triteBinderContainer.getMethodBindings();

        Iterator<TriteBinding> iterator = bindingBuilders.iterator();

        while (iterator.hasNext()) {
            TriteBinding bindingBuilder = iterator.next();
            Class<?> bindingBuilderClass = bindingBuilder.getClassType();

            if (this.triteBinderContainer.exists(bindingBuilder)) {
                return;
            }

            List<Constructor<?>> constructors = Arrays.stream(bindingBuilderClass.getDeclaredConstructors()).filter(cost -> cost.isAnnotationPresent(TriteJect.class)).collect(Collectors.toList());
            if (constructors.size() > 1) {
                throw new TriteMultipleConstructorException();
            }

            Class<?>[] parameterTypes = constructors.get(0).getParameterTypes();
            List<Object> availableBindings = new ArrayList<>();

            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.isAnnotationPresent(TriteNamed.class)) {
                    TriteBinding binding = triteBinderProcessor.getInstanceByAnnotation(parameterType.getAnnotation(TriteNamed.class).value());

                    if (binding != null) {
                        availableBindings.add(binding.getBinding());
                    }
                    continue;
                }

                try {
                    TriteBinding binding = triteBinderContainer.getBinding(parameterType);

                    if (binding != null) {
                        availableBindings.add(binding.getBinding());
                        continue;
                    }
                    availableBindings.add(null);
                }catch (NoTriteBindingException exception){
                    throw new NoTriteBindingException("ERROR IN Handle binding A" , constructors.get(0).getClass().getSimpleName());
                }
            }

            if (!iterator.hasNext() && availableBindings.stream().anyMatch(Objects::isNull)) {
                iterator.remove();
                throw new NoTriteBindingException("ERROR IN Handle binding B" ,"There is an missing binding for constructor in class " + constructors.get(0).getClass().getSimpleName());
            }

            if (availableBindings.stream().anyMatch(Objects::isNull)) {
                continue;
            }

            Constructor<?> declaredConstructor = bindingBuilderClass.getDeclaredConstructor(parameterTypes);
            declaredConstructor.setAccessible(true);

            Object binding = declaredConstructor.newInstance(availableBindings.toArray(new Object[0]));

            Collection<TriteJectionMultiBinder> multiBinders = bindingBuilder.getMultiBinders();
            if (multiBinders != null) {
                for (TriteJectionMultiBinder multiBinder : multiBinders) {
                    multiBinder.handleMultiBinding(binding);
                }
            }

            if (bindingBuilder.isSubModule()) {
                iterator.remove();

                if (!(binding instanceof TriteJectionModule)) {
                    throw new RuntimeException("Cannot bind " + binding.getClass().getSimpleName() + " because class isn't and module");
                }

                TriteJection.getInstance().addModule((TriteJectionModule) binding);
                declaredConstructor.setAccessible(false);
                continue;
            }

            this.triteBinderContainer.addBinding(new TriteBinding(bindingBuilder.getClassType(), binding, bindingBuilder.getNamed(), multiBinders, false));
            iterator.remove();

            declaredConstructor.setAccessible(false);
        }
    }

}
