package nl.tritewolf.tritejection.binder;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.TriteJectionModule;
import nl.tritewolf.tritejection.binder.handle.HandleBindings;
import nl.tritewolf.tritejection.exceptions.NoTriteAnnotationBindingException;

import java.lang.reflect.InvocationTargetException;

public class TriteBinderProcessor {

    private final TriteJection instance;

    private final TriteBinderContainer triteBinderContainer;

    public TriteBinderProcessor(TriteJection instance) {
        this.instance = instance;

        this.triteBinderContainer = new TriteBinderContainer();
    }

    public void handleBindings(TriteJectionModule module) {
        try {
            new HandleBindings(this.instance, this.triteBinderContainer, this).initBindings(module);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
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