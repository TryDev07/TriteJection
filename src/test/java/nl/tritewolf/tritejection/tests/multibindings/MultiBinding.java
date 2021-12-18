package nl.tritewolf.tritejection.tests.multibindings;

import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

public class MultiBinding implements TriteJectionMultiBinder {

    @Override
    public Class<?> getMultiBindingClass() {
        return Cache.class;
    }

    @Override
    public void handleMultiBinding(Object triteJectionBindingInstance) {
        Cache.getBindings().put(triteJectionBindingInstance.getClass(), 99);
    }
}
