package nl.tritewolf.tritejection.binder;

import lombok.Getter;
import lombok.Setter;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Getter
@Setter
public class TriteBinderContainer {

    private ConcurrentLinkedDeque<TriteBinding> methodBindings = new ConcurrentLinkedDeque<>();
    private List<TriteBinding> bindings = new ArrayList<>();

    public TriteBinding getBinding(Class<?> classType) {
        return this.bindings.stream().filter(triteBinding -> triteBinding.getClassType().equals(classType))
                .findFirst()
                .orElseThrow(() -> new NoTriteBindingException(classType.getSimpleName()));
    }

    public void addBinding(TriteBinding triteBinding) {
        if (this.bindings.stream().anyMatch(binding -> binding.getClassType().equals(triteBinding.getClassType()))) {
            return;
        }

        this.bindings.add(triteBinding);
    }

    public void addBinderBuilder(TriteBinding binderBuilder) {
        this.methodBindings.add(binderBuilder);
    }

}
