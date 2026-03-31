package nl.tritewolf.tritejection;

import lombok.Getter;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.binder.TriteBinderProcessor;
import nl.tritewolf.tritejection.bindings.FieldBinding;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinderContainer;
import nl.tritewolf.tritejection.utils.AnnotationDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class TriteJection {

    private final TriteBinderContainer triteBinderContainer;
    private final TriteBinderProcessor triteBinderProcessor;
    private final TriteJectionMultiBinderContainer triteMultiBinderContainer;

    private final List<TriteJectionModule> modules = new ArrayList<>();

    private TriteJection(TriteJectionModule... triteJectionModule) {
        this.triteBinderContainer = new TriteBinderContainer();
        this.triteBinderProcessor = new TriteBinderProcessor(this);
        this.triteMultiBinderContainer = new TriteJectionMultiBinderContainer();

        addModule(triteJectionModule);
    }

    @SuppressWarnings("unchecked")
    public <K> K getTriteJection(Class<K> clazz) {
        try {
            return (K) triteBinderProcessor.getInstanceByClass(clazz).getBinding();
        } catch (NullPointerException nullPointerException) {
            throw new NoTriteBindingException("Error in getTriteJection ", clazz.getName());
        }
    }

    public <K> K getTriteJectionOr(Class<K> clazz, K def) {
        try {
            return this.getTriteJection(clazz);
        } catch (NoTriteBindingException noTriteBindingException) {
            return def;
        }
    }

    public void addModule(TriteJectionModule... triteJectionModule) {
        Arrays.stream(triteJectionModule).forEach(module -> {
            module.init(this);

            module.registerMultiBindings().forEach(triteMultiBinderContainer::addTriteJectionMultiBinder);
            module.bindings();
            this.modules.add(module);

            this.triteBinderProcessor.handleBindings(module);
        });
    }

    public void process() {
        ClassLoader classLoader = modules.get(0).getClass().getClassLoader();
        this.process(classLoader, Package.getPackages());
    }

    public void process(ClassLoader classLoader, Package[] packages) {
        try {
            AnnotationDetector annotationDetector = new AnnotationDetector(new FieldBinding(this.triteBinderProcessor));

            String[] objects = Arrays.stream(packages).map(Package::getName).toArray(String[]::new);

            annotationDetector.detect(classLoader, objects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TriteJection createTriteJection(TriteJectionModule... triteJectionModule) {
        return new TriteJection(triteJectionModule);
    }

}

