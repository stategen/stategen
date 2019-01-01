package org.stategen.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.stategen.framework.lite.enums.EditorType;

/***
 * <pre>
 * 除了枚举，
 * 其它如不指定 referField ，根据参数或field名称推导：cityId=>city ,cityIds => citys，但是否后如果不含有Id、Ids,将根据api推导
 * 如不指定 api,
 *   1,如不配置optionClass 根据参数或field名称推导： cityId,cityIds=> api=getCityOptions
 *   2,如配置optionClass=City.class,无论参数或field，都推导 =>getCityOptions
 * 
 * </pre>
 *  *  */
@Target({ ElementType.TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Editor(EditorType.Select.class)
public @interface ReferConfig {
    
    Class<?> optionClass() default Void.class;
    String api() default "";
    String referField() default "";
}
