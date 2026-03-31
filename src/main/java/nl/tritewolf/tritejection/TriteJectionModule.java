package nl.tritewolf.tritejection;

import nl.tritewolf.tritejection.binder.TriteBinderBuilder;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

import java.util.Collections;
import java.util.List;

public abstract class TriteJectionModule {

    private TriteJection instance;

    void init(TriteJection instance) {
        this.instance = instance;
    }

    public abstract void bindings();

    public List<TriteJectionMultiBinder> registerMultiBindings() {
        return Collections.emptyList();
    }

    protected <K> TriteBinderBuilder<K> bind(Class<K> clazz) {
        return new TriteBinderBuilder<>(this.instance, this, clazz);
    }
}
