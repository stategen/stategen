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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.stategen.framework.annotation.AreaExtraProp.List;


/**
 * The Interface AreaExtraProp.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Inherited
public @interface AreaExtraProp {
    public final static String EDITOR_VISIBLE_FALSE = "doEdit: false";
    public final static String EDITOR_VISIBLE_TRUE = "doEdit: true";
    
    public final static String QUERY_VISIBLE_FALSE = "doQuery: false";
    public final static String QUERY_VISIBLE_TRUE = "doQuery: true";
    
    /***指成功后，生成的状态*/
    String value() ;
    
    
    /**
     * The Interface List.
     */
    @Target({ METHOD,  ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Inherited
    @Documented
    @interface List {

        AreaExtraProp[] value();
    }
}
