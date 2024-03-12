package nl.tritewolf.tritejection.binder;

import lombok.Getter;
import lombok.Setter;
import nl.tritewolf.tritejection.exceptions.NoTriteBindingException;
import nl.tritewolf.tritejection.module.TriteJectionModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
public class TriteBinderContainer {

    private Map<TriteJectionModule, ConcurrentLinkedDeque<TriteBinding>> methodBindings = new ConcurrentHashMap<>();
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

    public void addBinderBuilder(TriteJectionModule module, TriteBinding binderBuilder) {
        this.methodBindings.computeIfAbsent(module, triteJectionModule -> new ConcurrentLinkedDeque<>()).add(binderBuilder);
    }
}