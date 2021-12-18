package nl.tritewolf.tritejection.tests.injections;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.tests.objects.FakeObject;

import java.util.concurrent.ThreadLocalRandom;

public class AsEagerSingletonConstructorInject {

    public FakeObject fakeObject;

    @TriteJect
    public AsEagerSingletonConstructorInject(FakeObject fakeObject) {
        this.fakeObject = fakeObject;
    }

    public int setTestObjectToRandomInt() {
        int i = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        fakeObject.setI(i);
        return i;
    }
}
