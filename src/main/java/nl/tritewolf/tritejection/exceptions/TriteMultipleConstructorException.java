package nl.tritewolf.tritejection.exceptions;

public class TriteMultipleConstructorException extends RuntimeException {

    public TriteMultipleConstructorException() {
        super("There is more then 1 constructor with annotation @TriteJect");
    }
}
