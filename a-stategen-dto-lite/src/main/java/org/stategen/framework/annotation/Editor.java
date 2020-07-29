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

/***
 * *前端编辑状态下，用何种编辑器编辑对应Field的值
 * 
 * @author niaoge
 * @version $Id: Editor.java, v 0.1 2020年7月29日 上午3:14:59 XiaZhengsheng Exp $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, ElementType.PARAMETER, FIELD, ANNOTATION_TYPE })
@Inherited
public @interface Editor {
    
    /*** 前端编辑器，继承和依赖倒置的设计原则，由前端具体实现该编辑器，用javaClass避免硬编码 */
    Class<? extends EditorType> value() default EditorType.Input.class;
    
    /*** 该编辑器一些属性 */
    String props() default "";
    
    /****/
    String nullTitle() default "请选择";
    
    /****/
    String falseTitle() default "";
    
    /****/
    String trueTitle() default "";
}
