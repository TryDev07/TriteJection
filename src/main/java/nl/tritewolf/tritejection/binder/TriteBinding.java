package nl.tritewolf.tritejection.binder;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TriteBinding {

    private final Class<?> classType;
    private final Object binding;
    private final String named;

    @Builder
    public TriteBinding(Class<?> classType, Object binding, String named) {
        this.classType = classType;
        this.binding = binding;
        this.named = named;
    }
}
