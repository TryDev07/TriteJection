package nl.tritewolf.tritejection.binder;

import lombok.Getter;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TriteBinderContainer {

    private final List<TriteBinding> bindings = new ArrayList<>();

    public TriteBinding getBinding(Class<?> classType) {
        return this.bindings.stream().filter(triteBinding -> triteBinding.getClassType().equals(classType))
                .findFirst()
                .orElseThrow(() -> new NoTriteBindingException(classType.getSimpleName()));
    }

    public void addBinding(TriteBinding triteBinding) {
        this.bindings.add(triteBinding);
    }

}
