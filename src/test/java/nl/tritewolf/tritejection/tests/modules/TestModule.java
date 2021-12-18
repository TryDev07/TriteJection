package nl.tritewolf.tritejection.tests.modules;

import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.tests.injections.*;
import nl.tritewolf.tritejection.tests.interfaces.Test;
import nl.tritewolf.tritejection.tests.interfaces.TestHandling;
import nl.tritewolf.tritejection.tests.objects.TestObject;

public class TestModule extends TriteJectionModule {

    @Override
    public void bindings() {
        bind(TestObject.class).asEagerSingleton();

        //Field injection.
        bind(AsEagerSingletonInject.class).asEagerSingleton();

        //Constructor injection
        bind(AsEagerSingletonConstructorInject.class).asEagerSingleton();

        //Custom instance injection
        bind(CustomInstanceInject.class).toInstance(new CustomInstanceInject(69)).asEagerSingleton();

        //Named field injection
        bind(Test.class).annotatedWith("TestHandling").to(TestHandling.class).asEagerSingleton();
        bind(AsEagerSingletonNamedInjection.class).asEagerSingleton();
        bind(AsEagerSingletonNamedConstructorInjection.class).asEagerSingleton();
    }
}
