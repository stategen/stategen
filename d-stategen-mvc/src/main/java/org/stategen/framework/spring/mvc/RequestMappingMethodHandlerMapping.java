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
package org.stategen.framework.spring.mvc;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.stategen.framework.annotation.ApiRequestMappingAutoWithMethodName;
import org.stategen.framework.spring.util.RequestMappingResolveResult;
import org.stategen.framework.spring.util.RequestMappingResolver;
import org.stategen.framework.util.CollectionUtil;

/**
 * The Class RequestMappingMethodHandlerMapping.
 */
public class RequestMappingMethodHandlerMapping extends RequestMappingHandlerMapping {
    private boolean useSuffixPatternMatch = true;

    private boolean useTrailingSlashMatch = true;

    private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

    private final List<String> fileExtensions = new ArrayList<String>();

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {

        boolean isClass = element instanceof Class<?>;

        RequestMapping requestMappingAnno = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        if (requestMappingAnno == null) {
            return null;
        }

        RequestCondition<?> condition = (isClass ? getCustomTypeCondition((Class<?>) element)
            : getCustomMethodCondition((Method) element));

        ApiRequestMappingAutoWithMethodName methodNameAnno = AnnotatedElementUtils.findMergedAnnotation(element,
            ApiRequestMappingAutoWithMethodName.class);

        if (methodNameAnno == null) {
            return createRequestMappingInfo(requestMappingAnno, condition);
        }

        return createRequestMappingInfoByApiMethodAnno(requestMappingAnno, condition, (Method) element);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    protected RequestMappingInfo createRequestMappingInfoByApiMethodAnno(RequestMapping requestMapping,
                                                                         RequestCondition<?> customCondition,
                                                                         Method method) {
        String[] patterns = resolveEmbeddedValuesInPatterns(requestMapping.value());
        if (!method.isAnnotationPresent(RequestMapping.class) || CollectionUtil.isEmpty(patterns)) {
            RequestMappingResolveResult methodParsered = RequestMappingResolver.resolveOwnPath(method);
            String                      path           = methodParsered.getPath();
            patterns = new String[] { path };
        }

        return new RequestMappingInfo(
            new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), this.useSuffixPatternMatch,
                this.useTrailingSlashMatch, this.fileExtensions),
            new RequestMethodsRequestCondition(requestMapping.method()),
            new ParamsRequestCondition(requestMapping.params()), new HeadersRequestCondition(requestMapping.headers()),
            new ConsumesRequestCondition(requestMapping.consumes(), requestMapping.headers()),
            new ProducesRequestCondition(requestMapping.produces(), requestMapping.headers(),
                this.contentNegotiationManager),
            customCondition);
    }

}
