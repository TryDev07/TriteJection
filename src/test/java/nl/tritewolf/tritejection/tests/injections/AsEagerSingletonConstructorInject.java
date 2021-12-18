package nl.tritewolf.tritejection.tests.injections;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.tests.objects.TestObject;

import java.util.concurrent.ThreadLocalRandom;

public class AsEagerSingletonConstructorInject {

    public TestObject testObject;

    @TriteJect
    public AsEagerSingletonConstructorInject(TestObject testObject) {
        this.testObject = testObject;
    }

    public int setTestObjectToRandomInt() {
        int i = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        testObject.setI(i);
        return i;
    }
}
