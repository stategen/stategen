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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import io.swagger.annotations.Api;

/**
 * The Interface ApiConfig.
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Controller
@GenRoute
@GenModel
@Api
@Menu
public @interface ApiConfig {
    /***对应{@link  io.swagger.annotations.Api#value()} */
    @AliasFor(annotation = Api.class, attribute = "value")
    String name() default "";

    /***该controller是否生成菜单项 ，默认 true */
    @AliasFor(annotation = Menu.class,attribute="value")
    boolean menu() default true;
    
    /***对应{@link io.swagger.annotations.Api#hidden()} ,该controller是否生成前端api*/
    @AliasFor(annotation = Api.class)
    boolean hidden() default false;
    
    /***是否生成前端对应的状态监听model*/
    @AliasFor(annotation = GenModel.class,attribute="value")
    boolean genModel() default true;
    
    /***是否生成前端路由路径*/
    @AliasFor(annotation = GenRoute.class,attribute="value")
    boolean genRoute() default true;

}
