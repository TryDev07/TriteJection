package nl.tritewolf.tritejection.tests.injections;

import lombok.Getter;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.tests.objects.FakeObject;

@Getter
public class ConstructorAndFieldInjection {

    @TriteJect
    public FakeObject fakeObject;

    @TriteJect
    public ConstructorAndFieldInjection(FakeObject fakeObject) {
        this.fakeObject = fakeObject;
    }
}
