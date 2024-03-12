package nl.tritewolf.tritejection.tests.interfaces.constructor;

import nl.tritewolf.tritejection.annotations.TriteJect;

public class TestHandlingWithConstructor implements ITestHandlingWithConstructor {

    @TriteJect
    TestHandlingWithConstructor(TestHandlingWithConstructorParameter parameter) {
    }

    @Override
    public String handle() {
        return "this is working";
    }
}
