package nl.tritewolf.tritejection.binder;

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
        try {
            return TriteBinding.builder().classType(clazz).binding(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return TriteBinding.builder();
        }
    }

    public void asEagerSingleton() {
        this.triteBinderContainer.addBinding(this.triteBinding.build());
    }

    public TriteBinderBuilder<K> annotatedWith(String name) {
        this.triteBinding.named(name);
        return this;
    }

    public TriteBinderBuilder<K> to(K object) {
        try {
            this.triteBinding.classType(object.getClass()).binding(object.getClass().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }


    public TriteBinderBuilder<K> toInstance(K object) {
        this.triteBinding.classType(clazz).binding(object);
        return this;
    }
}
