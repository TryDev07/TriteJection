package nl.tritewolf.tritejection.tests.modules;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.tests.injections.AsEagerSingletonInject;
import nl.tritewolf.tritejection.tests.interfaces.TestSubHandling;
import org.junit.jupiter.api.Assertions;

public class SubModule extends TriteJectionModule {

    @TriteJect
    public SubModule(AsEagerSingletonInject asEagerSingletonInject) {
        Assertions.assertEquals(asEagerSingletonInject.getTest(), 10);
    }

    @Override
    public void bindings() {
        bind(TestSubHandling.class).asEagerSingleton();
    }
}
