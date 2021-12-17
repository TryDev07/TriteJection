package nl.tritewolf.tritejection.exceptions;

public class NoTriteBindingException extends RuntimeException {

    public NoTriteBindingException(String classTypeName) {
        super("There is no such binding for class type " + classTypeName);
    }
}
