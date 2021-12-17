package nl.tritewolf.tritejection.binder;

import lombok.Getter;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TriteBinderContainer {

    private final List<TriteBinding> methodBindings = new ArrayList<>();
    private final List<TriteBinding> bindings = new ArrayList<>();

    public TriteBinding getBinding(Class<?> classType) {
        return this.bindings.stream().filter(triteBinding -> triteBinding.getClassType().equals(classType))
                .findFirst()
                .orElse(null);
    }

    public void addBinding(TriteBinding triteBinding) {
        this.bindings.add(triteBinding);
    }

    public void addBinderBuilder(TriteBinding binderBuilder) {
        this.methodBindings.add(binderBuilder);
    }

}
