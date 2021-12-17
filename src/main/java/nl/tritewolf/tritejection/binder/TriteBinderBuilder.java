package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.annotations.TriteJect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TriteBinderBuilder<K> {

    private Class<? extends K> clazz;
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
        if (!isConstructorAnnotationPresent(triteBinding)) {
            try {
                TriteBinding build = this.triteBinding.binding(clazz.getDeclaredConstructor().newInstance()).build();
                this.triteBinderContainer.addBinding(build);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        this.triteBinderContainer.addBinderBuilder(this.triteBinding.build());

    }

    private boolean isConstructorAnnotationPresent(TriteBinding triteBinding) {
        List<Constructor<?>> collect = Arrays.stream(triteBinding.getClassType().getDeclaredConstructors()).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return false;
        }
        return collect.stream().anyMatch(constructor -> constructor.isAnnotationPresent(TriteJect.class));
    }

    public TriteBinderBuilder<K> annotatedWith(String name) {
        this.triteBinding.named(name);
        return this;
    }

    public TriteBinderBuilder<K> to(Class<? extends K> clazz) {
        this.clazz = clazz;
//        this.triteBinding.classType(clazz);
        return this;
    }


    public TriteBinderBuilder<K> toInstance(K object) {
        this.triteBinding.classType(clazz).binding(object);
        return this;
    }
}
