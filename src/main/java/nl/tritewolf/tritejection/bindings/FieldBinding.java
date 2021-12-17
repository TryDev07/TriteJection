package nl.tritewolf.tritejection.bindings;

import lombok.AllArgsConstructor;
import nl.tritewolf.tritejection.annotations.TriteJect;
import nl.tritewolf.tritejection.annotations.TriteNamed;
import nl.tritewolf.tritejection.binder.TriteBinderProcessor;
import nl.tritewolf.tritejection.utils.types.FieldReporter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@AllArgsConstructor
public class FieldBinding implements FieldReporter {

    private final TriteBinderProcessor triteBinderProcessor;

    @Override
    public void reportFieldAnnotation(Class<? extends Annotation> annotation, String className, String fieldName) {
        System.out.println(className);
        try {
            Class<?> clazz = Class.forName(className);
            Field declaredField = clazz.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            if (annotation.equals(TriteJect.class)) {
                declaredField.set(triteBinderProcessor.getInstanceByClass(clazz).getBinding(),
                        triteBinderProcessor.getInstanceByClass(declaredField.getType()).getBinding());
                return;
            }

            declaredField.set(triteBinderProcessor.getInstanceByClass(clazz).getBinding(),
                    triteBinderProcessor.getInstanceByAnnotation(declaredField.getAnnotation(TriteNamed.class).value()).getBinding());
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Annotation>[] annotations() {
        return new Class[]{TriteJect.class};
    }
}
