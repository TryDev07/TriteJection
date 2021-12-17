package nl.tritewolf.tritejection.module;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.binder.TriteBinderBuilder;
import nl.tritewolf.tritejection.binder.TriteBinding;

import java.util.stream.Collectors;

public abstract class TriteJectionModule {

    public abstract void bindings();

    protected <K> TriteBinderBuilder<K> bind(Class<K> clazz) {
        return new TriteBinderBuilder<>(clazz, TriteJection.getInstance().getTriteBinderContainer());
    }
}
