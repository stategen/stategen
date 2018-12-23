package org.stategen.framework.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.stategen.framework.lite.enums.EditorType;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD,ElementType.PARAMETER, FIELD, ANNOTATION_TYPE })
@Inherited
public @interface Editor {
    Class<? extends EditorType> value() default EditorType.Input.class;
    String props() default "";

}
