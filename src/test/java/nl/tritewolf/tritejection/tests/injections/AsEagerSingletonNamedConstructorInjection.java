package nl.tritewolf.tritejection.tests.injections;

import lombok.Getter;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.annotations.TriteNamed;
import nl.tritewolf.tritejection.tests.interfaces.Test;

@Getter
public class AsEagerSingletonNamedConstructorInjection {

    private final Test testHandling;

    @TriteJect
    public AsEagerSingletonNamedConstructorInjection(@TriteNamed("TestHandling") Test testHandling) {
        this.testHandling = testHandling;
    }
}
