package nl.tritewolf.tritejection.tests.modules;

import nl.tritewolf.tritejection.module.TriteJectionModule;
import nl.tritewolf.tritejection.multibinder.TriteJectionMultiBinder;
import nl.tritewolf.tritejection.tests.injections.*;
import nl.tritewolf.tritejection.tests.interfaces.Test;
import nl.tritewolf.tritejection.tests.interfaces.TestHandling;
import nl.tritewolf.tritejection.tests.multibindings.Cache;
import nl.tritewolf.tritejection.tests.multibindings.MultiBinding;
import nl.tritewolf.tritejection.tests.multibindings.injections.MultiBinderInject;
import nl.tritewolf.tritejection.tests.objects.FakeObject;

import java.util.Collections;
import java.util.List;

public class Module extends TriteJectionModule {

    @Override
    public void bindings() {
        bind(FakeObject.class).asEagerSingleton();

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

        //Multibinding injection
        bind(MultiBinderInject.class).toMultiBinder(Cache.class).asEagerSingleton();

        bind(ConstructorAndFieldInjection.class).asEagerSingleton();

        bind(SubModule.class).asSubModule();
    }

    @Override
    public List<TriteJectionMultiBinder> registerMultiBindings() {
        return Collections.singletonList(new MultiBinding());
    }
}
