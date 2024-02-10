package nl.tritewolf.tritejection.tests;

import nl.tritewolf.tritejection.TriteJection;
import nl.tritewolf.tritejection.binder.TriteBinderContainer;
import nl.tritewolf.tritejection.tests.injections.*;
import nl.tritewolf.tritejection.tests.interfaces.TestSubHandling;
import nl.tritewolf.tritejection.tests.modules.Module;
import nl.tritewolf.tritejection.tests.multibindings.Cache;
import nl.tritewolf.tritejection.tests.multibindings.injections.MultiBinderInject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class TriteJectionTests {

    private static Module module;
    private static TriteJection triteJection;

    @BeforeAll
    static void setup() {
        module = new Module();
        triteJection = TriteJection.createTriteJection(module);
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

        Assertions.assertNotNull(triteJection.fakeObject);
        Assertions.assertEquals(0, triteJection.fakeObject.getI());

        int newI = triteJection.setTestObjectToRandomInt();
        Assertions.assertEquals(newI, triteJection.fakeObject.getI());
    }

    @DisplayName("AsEagerSingleton injection for constructor test")
    @Test
    public void testAsEagerSingletonConstructorInjection() {
        AsEagerSingletonConstructorInject triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonConstructorInject.class);

        Assertions.assertNotNull(triteJection.fakeObject);

        int newI = triteJection.setTestObjectToRandomInt();
        Assertions.assertEquals(newI, triteJection.fakeObject.getI());
    }

    @DisplayName("Custom injection test")
    @Test
    public void testCustomInstanceInjection() {
        CustomInstanceInject triteJection = TriteJectionTests.triteJection.getTriteJection(CustomInstanceInject.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertEquals(69, triteJection.getI());
    }

    @DisplayName("Named annotated field injection test")
    @Test
    public void testAsEagerSingletonNamedInjection() {
        AsEagerSingletonNamedInjection triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonNamedInjection.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getTestHandling());

        String handle = triteJection.getTestHandling().handle();
        Assertions.assertEquals("this is working", handle);
    }

    @DisplayName("Named annotated constructor field injection test")
    @Test
    public void testAsEagerSingletonNamedConstructorInjection() {
        AsEagerSingletonNamedConstructorInjection triteJection = TriteJectionTests.triteJection.getTriteJection(AsEagerSingletonNamedConstructorInjection.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getTestHandling());

        String handle = triteJection.getTestHandling().handle();
        Assertions.assertEquals("this is working", handle);
    }

    @DisplayName("Constructor and field injection test")
    @Test
    public void testConstructorAndFieldInjection() {
        ConstructorAndFieldInjection triteJection = TriteJectionTests.triteJection.getTriteJection(ConstructorAndFieldInjection.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getFakeObject());

    }

    @DisplayName("Sub module injections test")
    @Test
    public void testSubModuleInjections() {
        TestSubHandling triteJection = TriteJectionTests.triteJection.getTriteJection(TestSubHandling.class);

        Assertions.assertNotNull(triteJection);
        Assertions.assertNotNull(triteJection.getString());
        Assertions.assertEquals("test" , triteJection.getString());

    }


    @DisplayName("Multibinding initialization test.")
    @Test
    public void testMultibindingInitialization() {
        int multiBinderSize = triteJection.getTriteMultiBinderContainer().getTriteJectionMultiBinders().size();
        int multiBinderToRegisterSize = module.registerMultiBindings().size();

        Assertions.assertNotNull(triteJection.getTriteMultiBinderContainer().getTriteJectionMultiBinders());
        Assertions.assertNotNull(module.registerMultiBindings());

        Assertions.assertEquals(multiBinderToRegisterSize, multiBinderSize);
    }

    @DisplayName("Multibinding handling test.")
    @Test
    public void testMultibindingHandling() {
        MultiBinderInject triteJection = TriteJectionTests.triteJection.getTriteJection(MultiBinderInject.class);

        Assertions.assertNotNull(Cache.getBindings());
        Assertions.assertNotNull(Cache.getBindings().get(triteJection.getClass()));
        Assertions.assertEquals(99, Cache.getBindings().get(triteJection.getClass()));
        Assertions.assertNotNull(triteJection.fakeObject);
    }

}
