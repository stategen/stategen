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

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.stategen.framework.annotation.Check;
import org.stategen.framework.checker.AbstractMethodChecker;
import org.stategen.framework.lite.CheckExcludeController;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.response.ResponseStatusTypeHandler;
import org.stategen.framework.response.ResponseUtil;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.GetOrCreateWrap;
import org.stategen.framework.util.TagArrayList;

/**
 * The Class AuthCheckerHandlerInterceptor.
 */
public class AuthCheckerHandlerInterceptor extends ResponseStatusTypeHandler implements HandlerInterceptor {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthCheckerHandlerInterceptor.class);

    public static Map<Method, TagArrayList<Annotation>> METHOD_CHECKERS_MAP = new ConcurrentHashMap<Method, TagArrayList<Annotation>>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //不是 RequestMapping的方法不额外处理
        RequestMapping requestMappingAnn = AnnotationUtil.getAnnotation(method, RequestMapping.class);

        if (requestMappingAnn == null) {
            //没有requestMapping,不拦截
            return true;
        }

        TagArrayList<Annotation> checkAnnos = getOrCreateCheckAnnoCache(method);
        if (CollectionUtil.isNotEmpty(checkAnnos)) {

            ResponseBody responseBodyAnno = AnnotationUtil.getMethodOrOwnerAnnotation(method, ResponseBody.class);

            for (Annotation checkAnno : checkAnnos) {
                Class<? extends Annotation> checkAnnoClz = checkAnno.annotationType();
                AbstractMethodChecker<Annotation> abstractMethodChecker = AbstractMethodChecker.getChecker(checkAnnoClz);
                AssertUtil.mustNotNull(abstractMethodChecker, new StringBuilder("标注:").append(checkAnnoClz).append(" 没有找到相应的校验器").toString());
                Class<? extends IResponseStatus> responseStatusClzOfCheckFailDefault = this.getResponseStatusClz();
                IResponseStatus errorResponseStatus = abstractMethodChecker.doCheck(method, checkAnno, responseStatusClzOfCheckFailDefault);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                if (errorResponseStatus != null) {
                    if (responseBodyAnno != null) {
                        ResponseUtil.writhResponse(true,null, errorResponseStatus);
                        if (logger.isInfoEnabled()) {
                            logger.info(new StringBuilder("校验").append(checkAnnoClz).append(" 没有通过，方法执行被拦截!").toString());
                        }
                    } else {
                        ResponseUtil.writeResponsePage(errorResponseStatus);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    protected TagArrayList<Annotation> getOrCreateCheckAnnoCache(Method method) {
        GetOrCreateWrap<TagArrayList<Annotation>> annoWrap = CollectionUtil.getOrCreateList(method, METHOD_CHECKERS_MAP, TagArrayList.class);
        TagArrayList<Annotation> checkAnnos = annoWrap.getValue();
        if (annoWrap.isNew()) {
            synchronized (checkAnnos) {
                if (!checkAnnos.isTagged()) {
                    scanCheckAnnos(method, checkAnnos);
                    Class<?> declaringClass = method.getDeclaringClass();
                    if (AnnotationUtils.getAnnotation(method, CheckExcludeController.class) == null) {
                        scanCheckAnnos(declaringClass, checkAnnos);
                    }
                    checkAnnos.setTagged(true);
                }
            }
        }
        return checkAnnos;
    }

    protected void scanCheckAnnos(AnnotatedElement annotatedElement, TagArrayList<Annotation> checkAnnos) {
        Annotation[] annotations = AnnotationUtils.getAnnotations(annotatedElement);
        if (CollectionUtil.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                //在anno上查找，是否有anno标注为Check
                Check check = AnnotationUtils.getAnnotation(annotation, Check.class);
                if (check != null) {
                    Repeatable repeatable = AnnotationUtils.getAnnotation(annotation, Repeatable.class);
                    if (repeatable != null) {
                        Class<? extends Annotation> realCheckAnnoClazz = repeatable.value();
                        Set<? extends Annotation> realCheckAnnos = AnnotatedElementUtils.getMergedRepeatableAnnotations(annotatedElement,
                            realCheckAnnoClazz);
                        checkAnnos.addAll(realCheckAnnos);
                    } else {
                        checkAnnos.add(annotation);
                    }
                }
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    public void setResponseStatusClzOfCheckFailDefault(Class<IResponseStatus> responseStatusClzOfCheckFailDefault) {
        this.setResponseStatusClz(responseStatusClzOfCheckFailDefault);
    }

}
