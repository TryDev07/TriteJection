package nl.tritewolf.tritejection.utils.types;

import java.lang.annotation.Annotation;

public interface Reporter {

    Class<? extends Annotation>[] annotations();
}