package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinderContainer;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TriteBinderBuilder<K> {

    private final Class<K> clazz;
    private Class<? extends K> bindingClass;
    private final TriteJectionModule module;
    private final TriteBinderContainer triteBinderContainer;
    private final TriteJectionMultiBinderContainer triteMultiBinderContainer;

    private final TriteBinding.TriteBindingBuilder triteBinding;

    public TriteBinderBuilder(Class<K> clazz, TriteJectionModule module, TriteBinderContainer triteBinderContainer, TriteJectionMultiBinderContainer triteMultiBinderContainer) {
        this.clazz = clazz;
        this.bindingClass = clazz;
        this.module = module;
        this.triteBinderContainer = triteBinderContainer;
        this.triteMultiBinderContainer = triteMultiBinderContainer;

        this.triteBinding = initBuilder();
    }

    private TriteBinding.TriteBindingBuilder initBuilder() {
        return TriteBinding.builder().classType(this.clazz);
    }

    public void asEagerSingleton() {
        TriteBinding triteBinding = this.triteBinding.build();

        if (this.triteBinderContainer.getBindings().stream().anyMatch(binding -> binding.getClassType().equals(triteBinding.getClassType()))) {
            return;
        }

        if (triteBinding.getBinding() != null) {
            this.triteBinderContainer.addBinding(triteBinding);
            return;
        }

        if (!this.isConstructorAnnotationPresent()) {
            try {
                Constructor<? extends K> declaredConstructor = this.bindingClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);

                K binding = declaredConstructor.newInstance();

                Collection<TriteJectionMultiBinder> multiBinders = triteBinding.getMultiBinders();
                if (multiBinders != null) {
                    for (TriteJectionMultiBinder multiBinder : multiBinders) {
                        multiBinder.handleMultiBinding(binding);
                    }
                }

                TriteBinding build = this.triteBinding.binding(binding).build();
                this.triteBinderContainer.addBinding(build);

                declaredConstructor.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        this.triteBinderContainer.addBinderBuilder(this.module, this.triteBinding.build());

    }

    public void asSubModule() {
        this.triteBinding.isSubModule(true);
        TriteBinding triteBinding = this.triteBinding.build();

        if (this.triteBinderContainer.getBindings().stream().anyMatch(binding -> binding.getClassType().equals(triteBinding.getClassType()))) {
            return;
        }

        if (triteBinding.getBinding() != null) {
            this.triteBinderContainer.addBinding(triteBinding);
            return;
        }

        if (!this.isConstructorAnnotationPresent()) {
            try {
                Constructor<? extends K> declaredConstructor = this.bindingClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);

                K binding = declaredConstructor.newInstance();
                if (!(binding instanceof TriteJectionModule)) {
                    throw new RuntimeException("Cannot bind " + binding.getClass().getSimpleName() + " because class isn't and module");
                }
                TriteJection.getInstance().addModule((TriteJectionModule) binding);

                TriteBinding build = this.triteBinding.binding(binding).build();
                this.triteBinderContainer.addBinding(build);

                declaredConstructor.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        this.triteBinderContainer.addBinderBuilder(this.module, this.triteBinding.build());
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
        this.bindingClass = clazz;
        this.triteBinding.bindingClassType(clazz);
        return this;
    }

    public TriteBinderBuilder<K> toInstance(K object) {
        this.triteBinding.binding(object);
        return this;
    }

    private boolean isConstructorAnnotationPresent() {
        List<Constructor<?>> collect = Arrays.stream(this.bindingClass.getDeclaredConstructors()).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return false;
        }
        return collect.stream().anyMatch(constructor -> constructor.isAnnotationPresent(TriteJect.class));
    }
}