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
@Route
@Api
@Menu
public @interface ApiConfig {

    @AliasFor(annotation = Api.class, attribute = "value")
    String name() default "";

    @AliasFor(annotation = Menu.class,attribute="value")
    boolean menu() default true;
    
    /***面包屑*/
    @AliasFor(annotation = Menu.class)
    Class<?> breadParent() default Object.class;
    
    @AliasFor(annotation = Api.class)
    boolean hidden() default false;
    
    @AliasFor(annotation = Route.class,attribute="value")
    boolean route() default true;

}
