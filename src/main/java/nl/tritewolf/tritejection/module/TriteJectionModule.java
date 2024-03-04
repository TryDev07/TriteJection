package nl.tritewolf.tritejection.module;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.binder.TriteBinderBuilder;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

import java.util.Collections;
import java.util.List;

public abstract class TriteJectionModule {

    public abstract void bindings();

    public List<TriteJectionMultiBinder> registerMultiBindings() {
        return Collections.emptyList();
    }

    protected <K> TriteBinderBuilder<K> bind(Class<K> clazz) {
        return new TriteBinderBuilder<>(clazz, this, TriteJection.getInstance().getTriteBinderContainer(), TriteJection.getInstance().getTriteMultiBinderContainer());
    }
}
