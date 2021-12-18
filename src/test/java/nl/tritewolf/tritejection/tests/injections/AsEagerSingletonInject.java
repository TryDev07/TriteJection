package nl.tritewolf.tritejection.tests.injections;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.tests.objects.TestObject;

import java.util.concurrent.ThreadLocalRandom;

public class AsEagerSingletonInject {

    @TriteJect
    public TestObject testObject;

    public int setTestObjectToRandomInt() {
        int i = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        testObject.setI(i);
        return i;
    }
}
