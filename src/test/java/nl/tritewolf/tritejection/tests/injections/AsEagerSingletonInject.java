package nl.tritewolf.tritejection.tests.injections;

import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.tests.objects.FakeObject;

import java.util.concurrent.ThreadLocalRandom;

public class AsEagerSingletonInject {

    @TriteJect
    public FakeObject fakeObject;

    public int setTestObjectToRandomInt() {
        int i = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        fakeObject.setI(i);
        return i;
    }

    public int getTest(){
        return 10;
    }
}
