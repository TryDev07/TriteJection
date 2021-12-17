package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.annotations.TriteJect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class TriteBinderBuilder<K> {

    private final Class<? extends K> clazz;
    private final TriteBinderContainer triteBinderContainer;

    private final TriteBinding.TriteBindingBuilder triteBinding;

    public TriteBinderBuilder(Class<? extends K> clazz, TriteBinderContainer triteBinderContainer) {
        this.clazz = clazz;
        this.triteBinderContainer = triteBinderContainer;

        this.triteBinding = initBuilder();
    }

    private TriteBinding.TriteBindingBuilder initBuilder() {
        return TriteBinding.builder().classType(clazz);
    }

    public void asEagerSingleton() {
        TriteBinding triteBinding = this.triteBinding.build();

        if (triteBinding.getBinding() != null) {
            this.triteBinderContainer.addBinding(triteBinding);
            return;
        }

        if (isConstructorAnnotationPresent(triteBinding)) {
            try {
                this.triteBinderContainer.addBinding(this.triteBinding.binding(this.triteBinding.getClass().getDeclaredConstructor().newInstance()).build());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return;
        }

        this.triteBinderContainer.addBinderBuilder(this.triteBinding);
    }

    private boolean isConstructorAnnotationPresent(TriteBinding triteBinding) {
        return Arrays.stream(triteBinding.getClassType().getDeclaredConstructors()).anyMatch(constructor -> constructor.isAnnotationPresent(TriteJect.class));
    }

    public TriteBinderBuilder<K> annotatedWith(String name) {
        this.triteBinding.named(name);
        return this;
    }

    public TriteBinderBuilder<K> to(K object) {
        this.triteBinding.classType(object.getClass());
        return this;
    }


    public TriteBinderBuilder<K> toInstance(K object) {
        this.triteBinding.classType(clazz).binding(object);
        return this;
    }
}
