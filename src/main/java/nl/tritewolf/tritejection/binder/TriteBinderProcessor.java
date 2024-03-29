package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.binder.handle.HandleBindings;
import nl.tritewolf.tritejection.exceptions.NoTriteAnnotationBindingException;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.module.TriteJectionModule;

import java.lang.reflect.InvocationTargetException;

public class TriteBinderProcessor {

    private final TriteBinderContainer triteBinderContainer;

    public TriteBinderProcessor(TriteBinderContainer triteBinderContainer) {
        this.triteBinderContainer = triteBinderContainer;
    }

    public void handleBindings(TriteJectionModule module) {
        try {
            new HandleBindings(this.triteBinderContainer, this).initBindings(module);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public TriteBinding getInstanceByClass(Class<?> clazz) {
        return triteBinderContainer.getBinding(clazz);
    }

    public TriteBinding getInstanceByAnnotation(String annotation) {
        return triteBinderContainer.getBindings().stream().filter(triteBinding -> triteBinding.getNamed() != null && triteBinding.getNamed().equals(annotation)).findFirst().orElseThrow(() -> new NoTriteAnnotationBindingException(annotation));
    }

}
