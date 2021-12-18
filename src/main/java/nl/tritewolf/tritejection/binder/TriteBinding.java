package nl.tritewolf.tritejection.binder;

import lombok.Builder;
import lombok.Getter;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

@Getter
public class TriteBinding {

    private final Class<?> classType;
    private final Object binding;
    private final String named;
    private final TriteJectionMultiBinder multiBinder;

    @Builder
    public TriteBinding(Class<?> classType, Object binding, String named, TriteJectionMultiBinder multiBinder) {
        this.classType = classType;
        this.binding = binding;
        this.named = named;
        this.multiBinder = multiBinder;
    }
}
