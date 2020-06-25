/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.stategen.framework.enums.DataOpt;

/**
 * The Interface State.
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@GenForm()
@GenEffect()
@GenReducer()
@GenRefresh()
@Inherited
public @interface State {

    /***如果api返回值不能强调返回的类型，需要用area指定，
     * 比如返回前端并不是User而是String userId,让前端根据userId删除UserList的user,则需要指定User.class*/
    Class<?> area() default Object.class;

    /***APPEND_OR_UPDATE 返回值与前端状态中已有的数据(area中,比如userArea)的关系，分别为 增加或更新、替换、删除 */
    DataOpt dataOpt() default DataOpt.APPEND_OR_UPDATE;

    /***false,是否在当前路由下，立即返回，对于dva的model,则生成到setup中,*/
    boolean init() default false;

    /***true 检查路径和是否打开过 */
    boolean initCheck() default true;
    
    /***true 生成react的reducer函数*/
    @AliasFor(annotation = GenReducer.class,attribute="value")
    boolean genReducer() default true;
    
    /***true 生成react的effect函数*/
    @AliasFor(annotation = GenEffect.class,attribute="value")
    boolean genEffect() default true;
    
    /***false,生成form表单必要的元数，这些元素是组装好的，前端拿到后，就能组装界面，可以适当地美工*/
    @AliasFor(annotation = GenForm.class,attribute="value")
    boolean genForm() default false;
    
    /***false, 是否生成前端的刷新函数*/
    @AliasFor(annotation = GenRefresh.class,attribute="value")
    boolean genRefresh() default false;
    
    

}
