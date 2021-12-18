package nl.tritewolf.tritejection.tests.injections;

import lombok.Getter;
import nl.tritewolf.tritejection.annotations.TriteNamed;
import nl.tritewolf.tritejection.tests.interfaces.Test;

@Getter
public class AsEagerSingletonNamedInjection {

    @TriteNamed("TestHandling")
    private Test testHandling;
}
