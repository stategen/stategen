package org.stategen.framework.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * *导出字段名称为常量到前端，编码手写硬编码,
 * *@Id 字段可以不用设置也可以导出到前端
 * *以前为全量导出，考虑到前端使用场量不多，反而占用一定的编译空间，故改为指定FieldNameConst才导出
 * 
 * @author niaoge
 * @version $Id: FieldNameConst.java, v 0.1 2020年7月29日 上午3:53:08 XiaZhengsheng Exp $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, FIELD, ANNOTATION_TYPE })
@Inherited
public @interface FieldNameConst {
    
}
