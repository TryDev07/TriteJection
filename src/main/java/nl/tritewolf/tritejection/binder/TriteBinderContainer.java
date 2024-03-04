package nl.tritewolf.tritejection.binder;

import lombok.Getter;
import lombok.Setter;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
public class TriteBinderContainer {

    private ConcurrentLinkedDeque<TriteBinding> methodBindings = new ConcurrentLinkedDeque<>();
    private List<TriteBinding> bindings = new ArrayList<>();

    public TriteBinding getBinding(Class<?> classType) {
        return this.bindings.stream().filter(triteBinding -> triteBinding.getClassType().equals(classType))
                .findFirst()
                .orElseThrow(() -> new NoTriteBindingException("GET BINDING ERROR ", classType.getSimpleName()));
    }

    public void addBinding(TriteBinding triteBinding) {
        if (exists(triteBinding)) return;

        this.bindings.add(triteBinding);
    }

    public boolean exists(TriteBinding triteBinding) {
        return this.bindings.stream().anyMatch(binding -> binding.getClassType().equals(triteBinding.getClassType()));
    }

    public void addBinderBuilder(TriteBinding binderBuilder) {
        this.methodBindings.add(binderBuilder);
    }

}
