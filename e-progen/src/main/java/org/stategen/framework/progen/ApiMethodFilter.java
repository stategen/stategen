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
package org.stategen.framework.progen;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.stategen.framework.util.AnnotationUtil;

/**
 * The Class ApiMethodFilter.
 */
public class ApiMethodFilter implements MethodFilter {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ApiMethodFilter.class);

    public static MethodFilter filter = new ApiMethodFilter();

    @Override
    public boolean matches(Method method) {
        if (!Modifier.isPublic(method.getModifiers())){
            return false;
        }
        
        if (Modifier.isStatic(method.getModifiers())){
            return false;
        }
        
        if (AnnotationUtil.getMethodOrOwnerAnnotation(method, ResponseBody.class)==null){
            return false;
        }
        
        if (AnnotationUtil.getAnnotation(method, RequestMapping.class)==null) {
            return false;
        }
        return true;
    }

}
