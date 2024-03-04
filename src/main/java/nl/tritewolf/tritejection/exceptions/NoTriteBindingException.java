package nl.tritewolf.tritejection.exceptions;

public class NoTriteBindingException extends RuntimeException {

    public NoTriteBindingException(String prefix, String classTypeName) {
        super(prefix + "There is no such binding for class type " + classTypeName);
    }
}
