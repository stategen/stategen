package org.stategen.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * <pre>
 * 除了枚举标注，
 * 其它，如何数据是此用其它字段，需要加上该标准
 * 推导api,如getCityOptions
 * 当api不为空时，直接获取 api
 * 如果api为空
 *   当 bean=Province.class,将获取 getProinceOptions
 *   当 bean 为空时，该field或名parameter 的名称 name，去掉结尾 Id ,Ids,ID,IDs 
 *   get+name(首字母大写)+Options 
 *   如cityId,cityIds =>getCityOptions
 * </pre>
 *  *  */
@Target({ ElementType.TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReferConfig {
    
    Class<?> bean() default Void.class;
    String api() default "";
    String none() default "";
    String referField() default "";
}
