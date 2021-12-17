package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.exceptions.NoTriteAnnotationBindingException;

public class TriteBinderProcessor {

    private final TriteBinderContainer triteBinderContainer;

    public TriteBinderProcessor(TriteBinderContainer triteBinderContainer) {
        this.triteBinderContainer = triteBinderContainer;
    }

    public TriteBinding getInstanceByClass(Class<?> clazz) {
        return triteBinderContainer.getBinding(clazz);
    }

    public TriteBinding getInstanceByAnnotation(String annotation) {
        return triteBinderContainer.getBindings().stream().filter(triteBinding -> triteBinding.getNamed().equals(annotation)).findFirst().orElseThrow(() -> new NoTriteAnnotationBindingException(annotation));
    }
}
