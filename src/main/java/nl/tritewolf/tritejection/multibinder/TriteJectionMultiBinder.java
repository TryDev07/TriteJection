package nl.tritewolf.tritejection.multibinder;

public interface TriteJectionMultiBinder {

    Class<?> getMultiBindingClass();

    void handleMultiBinding(Object triteJectionBindingInstance);
}
