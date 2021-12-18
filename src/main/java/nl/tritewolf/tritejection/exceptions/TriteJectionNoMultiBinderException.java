package nl.tritewolf.tritejection.exceptions;

public class TriteJectionNoMultiBinderException extends RuntimeException {

    public TriteJectionNoMultiBinderException(String classTypeName) {
        super("There is no such multibinding binding for class " + classTypeName);
    }
}
