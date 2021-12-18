package nl.tritewolf.tritejection;

import lombok.Getter;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.binder.TriteBinderProcessor;
import nl.tritewolf.tritejection.binder.TriteBinding;
import nl.tritewolf.tritejection.bindings.FieldBinding;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinderContainer;
import nl.tritewolf.tritejection.utils.AnnotationDetector;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class TriteJection {

    @Getter private static TriteJection instance;
    private final TriteBinderContainer triteBinderContainer;
    private final TriteBinderProcessor triteBinderProcessor;

    private TriteJection(TriteJectionModule... triteJectionModule) {
        instance = this;
        this.triteBinderContainer = new TriteBinderContainer();
        this.triteBinderProcessor = new TriteBinderProcessor(this.triteBinderContainer);

        try {
            Arrays.stream(triteJectionModule).forEach(TriteJectionModule::bindings);
            AnnotationDetector annotationDetector = new AnnotationDetector(new FieldBinding(this.triteBinderProcessor));

            ClassLoader classLoader = triteJectionModule.getClass().getClassLoader();
            String[] objects = Arrays.stream(classLoader.getDefinedPackages()).map(Package::getName).toArray(String[]::new);

            annotationDetector.detect(classLoader, objects);

            this.triteBinderProcessor.handleBindings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <K> K getTriteJection(Class<K> clazz) {
        try {
            return (K) triteBinderProcessor.getInstanceByClass(clazz).getBinding();
        } catch (NullPointerException nullPointerException) {
            throw new NoTriteBindingException(clazz.getSimpleName());
        }
    }

    public static TriteJection createTriteJection(TriteJectionModule... triteJectionModule) {
        return new TriteJection(triteJectionModule);
    }
}

