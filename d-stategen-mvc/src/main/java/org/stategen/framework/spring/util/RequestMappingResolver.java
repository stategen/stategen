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
package org.stategen.framework.spring.util;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.stategen.framework.annotation.ApiRequestMappingAutoWithMethodName;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

/**
 * The Class RequestMappingResolver.
 */
public class RequestMappingResolver {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RequestMappingResolver.class);

    public static RequestMappingResolveResult resolveOwnPath(Method method) {
        RequestMethod requestMethod =null;
        String path =null;
        
        ApiRequestMappingAutoWithMethodName apiRequestMappingAutoWithMethodName = AnnotationUtil.getAnnotation(method,  ApiRequestMappingAutoWithMethodName.class);
        
        RequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        String requestMappingValue = getRequestMappingValue(requestMapping);
        
        //判断requestMapping是否在method上
        boolean requestMappingOnMethod =method.isAnnotationPresent(RequestMapping.class);
        if (method.getName().equals("getUserById")){
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuffer("输出info信息: requestMappingValue:").append(requestMappingValue).toString());
            }
        }
        
        if (!requestMappingOnMethod && apiRequestMappingAutoWithMethodName != null && StringUtil.isBlank(requestMappingValue)) {
            String methodName = method.getName();
            path =StringUtil.startWithSlash(methodName);
            requestMethod = apiRequestMappingAutoWithMethodName.method();
        } else if (StringUtil.isNotBlank(requestMappingValue)) {
            RequestMethod[] requestMethods = requestMapping.method();
            requestMethod =  CollectionUtil.getFirst(requestMethods);
            path =requestMappingValue;
        }
        
        if (requestMethod == null && requestMapping!=null) {
            RequestMethod[] netMethods = requestMapping.method();
            requestMethod =  CollectionUtil.getFirst(netMethods);
        }
        
        return new RequestMappingResolveResult(path, requestMethod);
    }

    public static String getRequestMappingValue(RequestMapping requestMappingAnno) {
        if (requestMappingAnno != null) {
            String[] values = requestMappingAnno.value();
            String value =CollectionUtil.getFirst(values);
            if (StringUtil.isNotEmpty(value) && !value.startsWith("/")) {
                value = "/" + value;
            }
            return value;
        }
        return null;
    }
}