package nl.tritewolf.tritejection.binder.handle;

import lombok.AllArgsConstructor;
import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.binder.TriteBinderBuilder;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.binder.TriteBinding;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.exceptions.TriteMultipleConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HandleBindings {

    private final TriteBinderContainer triteBinderContainer;

    public void initBindings() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, TriteMultipleConstructorException, NoTriteBindingException {
        List<TriteBinding> bindings = this.triteBinderContainer.getBindings();
        List<TriteBinding> bindingBuilders = this.triteBinderContainer.getMethodBindings();

        while (!bindingBuilders.isEmpty()) {
            TriteBinding bindingBuilder = bindingBuilders.iterator().next();
            Class<?> bindingBuilderClass = bindingBuilder.getClassType();

            List<Constructor<?>> constructors = Arrays.stream(bindingBuilderClass.getDeclaredConstructors()).filter(cost -> cost.isAnnotationPresent(TriteJect.class)).collect(Collectors.toList());
            if (constructors.size() > 1) {
                throw new TriteMultipleConstructorException();
            }

            Class<?>[] parameterTypes = constructors.get(0).getParameterTypes();
            List<Object> availableBindings = new ArrayList<>();

            for (Class<?> parameterType : parameterTypes) {
                TriteBinding binding = triteBinderContainer.getBinding(parameterType);
                if (binding != null) {
                    availableBindings.add(binding.getBinding());
                    continue;
                }
                availableBindings.add(null);
            }

            if (availableBindings.stream().anyMatch(Objects::isNull) && !bindingBuilders.iterator().hasNext()) {
                bindingBuilders.remove(bindingBuilder);
                throw new NoTriteBindingException("There is an missing binding for constructor in class " + constructors.get(0).getClass().getSimpleName());
            }

            Object binding = bindingBuilderClass.getDeclaredConstructor(parameterTypes).newInstance(availableBindings.toArray(new Object[0]));
            bindings.add(new TriteBinding(bindingBuilder.getClassType(), binding, bindingBuilder.getNamed()));
            bindingBuilders.remove(bindingBuilder);
        }
    }
}
