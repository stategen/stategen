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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.stategen.framework.annotation.CookieCheck.List;
import org.stategen.framework.lite.ICookieType;

/**
 * *
 * framework根据该标识向cookie检测某个值是否存在.
 *
 * @author XiaZhengsheng
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = List.class)
public @interface CookieCheck {
    
    /***cookieGroupClzs与cookieNames长度相等*/
    Class<? extends ICookieType> cookieTypeClz();
    
    /***由cookieTypeClz找到枚举中的failResponse
     ** @Deprecated Class<? extends IResponseStatus> responseStatusClzOfCheckFail();
     */
    String cookieName();
    
    /**
     * The Interface List.
     */
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Check
    @interface List {
        
        CookieCheck[] value();
    }
    
}
