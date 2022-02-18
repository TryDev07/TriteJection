package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinderContainer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TriteBinderBuilder<K> {

    private Class<? extends K> clazz;
    private final TriteBinderContainer triteBinderContainer;
    private final TriteJectionMultiBinderContainer triteMultiBinderContainer;

    private final TriteBinding.TriteBindingBuilder triteBinding;

    public TriteBinderBuilder(Class<? extends K> clazz, TriteBinderContainer triteBinderContainer, TriteJectionMultiBinderContainer triteMultiBinderContainer) {
        this.clazz = clazz;
        this.triteBinderContainer = triteBinderContainer;
        this.triteMultiBinderContainer = triteMultiBinderContainer;

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
                K binding = clazz.getDeclaredConstructor().newInstance();

                TriteJectionMultiBinder multiBinder = triteBinding.getMultiBinder();
                if (multiBinder != null) {
                    multiBinder.handleMultiBinding(binding);
                }

                TriteBinding build = this.triteBinding.binding(binding).build();
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

    public TriteBinderBuilder<K> toMultiBinder(Class<?> clazz) {
        TriteJectionMultiBinder triteJectionMultiBinder = this.triteMultiBinderContainer.getTriteJectionMultiBinder(clazz);
        this.triteBinding.multiBinder(triteJectionMultiBinder);
        return this;
    }

    public TriteBinderBuilder<K> to(Class<? extends K> clazz) {
        this.clazz = clazz;
        return this;
    }


    public TriteBinderBuilder<K> toInstance(K object) {
        this.triteBinding.classType(clazz).binding(object);
        return this;
    }
}
