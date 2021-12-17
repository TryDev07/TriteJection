package nl.tritewolf.tritejection.exceptions;

public class NoTriteAnnotationBindingException extends RuntimeException {

    public NoTriteAnnotationBindingException(String classTypeName) {
        super("There is no such binding for annotation named " + classTypeName);
    }
}
