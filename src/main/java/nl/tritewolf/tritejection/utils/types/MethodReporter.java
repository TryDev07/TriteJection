package nl.tritewolf.tritejection.utils.types;

import java.lang.annotation.Annotation;

public interface MethodReporter extends Reporter {

    void reportMethodAnnotation(Class<? extends Annotation> annotation, String className,
                                String methodName);
}