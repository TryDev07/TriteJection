package nl.tritewolf.tritejection.binder.handle;

import lombok.AllArgsConstructor;
import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.TriteJectionModule;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.annotations.TriteNamed;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.binder.TriteBinderProcessor;
import nl.tritewolf.tritejection.binder.TriteBinding;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.exceptions.TriteMultipleConstructorException;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HandleBindings {

    private final TriteJection instance;

    private final TriteBinderContainer triteBinderContainer;
    private final TriteBinderProcessor triteBinderProcessor;

    public void initBindings(TriteJectionModule module) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, TriteMultipleConstructorException, NoTriteBindingException {
        ConcurrentLinkedDeque<TriteBinding> methodBindings = this.triteBinderContainer.getMethodBindings().remove(module);
        if (methodBindings == null) return;

        Iterator<TriteBinding> iterator = methodBindings.iterator();
        while (iterator.hasNext()) {
            TriteBinding bindingBuilder = iterator.next();
            Class<?> bindingBuilderClass = bindingBuilder.getBindingClassType();

            if (this.triteBinderContainer.exists(bindingBuilder)) {
                return;
            }

            List<Constructor<?>> constructors = Arrays.stream(bindingBuilderClass.getDeclaredConstructors()).filter(cost -> cost.isAnnotationPresent(TriteJect.class)).collect(Collectors.toList());
            if (constructors.size() > 1) {
                throw new TriteMultipleConstructorException();
            }

            Constructor<?> constructor = constructors.get(0);
            Parameter[] parameters = constructor.getParameters();
            List<Object> availableBindings = new ArrayList<>();

            for (Parameter parameter : parameters) {
                Class<?> parameterType = parameter.getType();

                if (parameter.isAnnotationPresent(TriteNamed.class)) {
                    String name = parameter.getAnnotation(TriteNamed.class).value();
                    TriteBinding binding = this.triteBinderProcessor.getInstanceByAnnotation(name);
                    availableBindings.add(binding.getBinding());
                    continue;
                }

                try {
                    TriteBinding binding = this.triteBinderContainer.getBinding(parameterType);
                    availableBindings.add(binding.getBinding());
                } catch (NoTriteBindingException exception) {
                    throw new NoTriteBindingException("ERROR IN Handle binding A ", "in class " + bindingBuilderClass.getSimpleName() + " for parameter " + parameterType.getSimpleName());
                }
            }

            if (availableBindings.stream().anyMatch(Objects::isNull)) {
                continue;
            }

            constructor.setAccessible(true);
            Object instance = constructor.newInstance(availableBindings.toArray());

            Collection<TriteJectionMultiBinder> multiBinders = bindingBuilder.getMultiBinders();
            if (multiBinders != null) {
                for (TriteJectionMultiBinder multiBinder : multiBinders) {
                    multiBinder.handleMultiBinding(instance);
                }
            }

            if (bindingBuilder.isSubModule()) {
                iterator.remove();

                if (!(instance instanceof TriteJectionModule)) {
                    throw new RuntimeException("Cannot bind " + instance.getClass().getSimpleName() + " because class isn't a module");
                }

                this.instance.addModule((TriteJectionModule) instance);
                constructor.setAccessible(false);
                continue;
            }

            this.triteBinderContainer.addBinding(new TriteBinding(bindingBuilder.getClassType(), bindingBuilder.getBindingClassType(), instance, bindingBuilder.getNamed(), multiBinders, false));
            iterator.remove();

            constructor.setAccessible(false);
        }
    }
}
