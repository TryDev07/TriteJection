package nl.tritewolf.tritejection.tests;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.tests.injections.*;
import nl.tritewolf.tritejection.tests.modules.TestModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class TriteJectionTests {

    private static TestModule testModule;
    private static TriteJection triteJection;

    @BeforeAll
    static void setup() {
        testModule = new TestModule();
        triteJection = TriteJection.createTriteJection(testModule);
        Logger.getAnonymousLogger().info("Starting testing:");
    }

    @DisplayName("Create TriteInjection test")
    @Test
    public void testInjectionCreation() {
        Assertions.assertNotNull(triteJection);
        Assertions.assertEquals(triteJection.getClass().getSimpleName(), TriteJection.class.getSimpleName());
    }

    @DisplayName("Cache TriteBindings test")
    @Test
    public void testBindingCaching() {
        TriteBinderContainer triteBinderContainer = triteJection.getTriteBinderContainer();

        Assertions.assertNotNull(triteBinderContainer.getBindings());

        triteBinderContainer.getBindings().forEach(triteBinding -> {
            Assertions.assertNotNull(triteBinding.getBinding());
            Assertions.assertNotNull(triteBinding.getClassType());
        });
    }

    @DisplayName("AsEagerSingleton injection test")
    @Test
    public void testAsEagerSingletonInjection() {
        AsEagerSingletonInject triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonInject.class);

        Assertions.assertNotNull(triteJection.testObject);
        Assertions.assertEquals(0, triteJection.testObject.getI());

        int newI = triteJection.setTestObjectToRandomInt();
        Assertions.assertEquals(newI, triteJection.testObject.getI());
    }

    @DisplayName("AsEagerSingleton injection for constructor test")
    @Test
    public void testAsEagerSingletonConstructorInjection() {
        AsEagerSingletonConstructorInject triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonConstructorInject.class);

        Assertions.assertNotNull(triteJection.testObject);

        int newI = triteJection.setTestObjectToRandomInt();
        Assertions.assertEquals(newI, triteJection.testObject.getI());
    }

    @Test
    public void testCustomInstanceInjection() {
        CustomInstanceInject triteJection = TriteJectionTests.triteJection.getTriteJection(CustomInstanceInject.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertEquals(69, triteJection.getI());
    }

    @Test
    public void testAsEagerSingletonNamedInjection() {
        AsEagerSingletonNamedInjection triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonNamedInjection.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getTestHandling());

        String handle = triteJection.getTestHandling().handle();
        Assertions.assertEquals("this is working", handle);
    }

    @Test
    public void testAsEagerSingletonNamedConstructorInjection() {
        AsEagerSingletonNamedConstructorInjection triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonNamedConstructorInjection.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getTestHandling());

        String handle = triteJection.getTestHandling().handle();
        Assertions.assertEquals("this is working", handle);
    }

}
