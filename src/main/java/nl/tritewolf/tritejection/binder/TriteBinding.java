package nl.tritewolf.tritejection.binder;

import lombok.Builder;
import lombok.Getter;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;

import java.util.Collection;
import java.util.HashSet;

@Getter
public class TriteBinding {

    private final Class<?> classType;
    private final Class<?> bindingClassType;
    private final Object binding;
    private final String named;
    private final Collection<TriteJectionMultiBinder> multiBinders;
    private final boolean isSubModule;

    @Builder
    public TriteBinding(Class<?> classType, Class<?> bindingClassType, Object binding, String named, Collection<TriteJectionMultiBinder> multiBinders, boolean isSubModule) {
        this.classType = classType;
        this.bindingClassType = bindingClassType;
        this.binding = binding;
        this.named = named;
        this.multiBinders = multiBinders;
        this.isSubModule = isSubModule;
    }

    // Append following methods to builder class
    public static class TriteBindingBuilder {

        public TriteBindingBuilder classType(Class<?> classType) {
            this.classType = classType;
            this.bindingClassType = classType;
            return this;
        }

        public TriteBindingBuilder multiBinder(TriteJectionMultiBinder multiBinder) {
            if (multiBinder == null) return this;

            if (this.multiBinders == null) {
                this.multiBinders = new HashSet<>();
            }

            this.multiBinders.add(multiBinder);
            return this;
        }
    }
}
