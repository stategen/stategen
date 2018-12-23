package org.stategen.framework.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.stategen.framework.annotation.ExcludeBeanRule.List;

/***
 * 因为api不能区分当前方法是否是查询还是操作bean数据，当为查询时，如果把bean中field的相关规则全部带过来，则查询肯定很麻烦
 * 该标准排除bean上哪些限制标志，如NotNull,Min
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD,ElementType.PARAMETER, FIELD, ANNOTATION_TYPE })
@Inherited
@Repeatable(List.class)
public @interface ExcludeBeanRule {
  Class<? extends Annotation>[] value(); 
  
  /**
   * The Interface List.
   */
  @Target({METHOD,ElementType.PARAMETER, FIELD, ANNOTATION_TYPE })
  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @Documented
  @interface List {
      ExcludeBeanRule[] value();
  }
}
