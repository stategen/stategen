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
package org.stategen.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * The Class AnnotationUtil.
 *  该类用来代替spring的 AnnotationUtils ,相比于 AnnotationUtils,该类可以获得继承方法上的annotation
 *  
 * @author Xia Zhengsheng
 */
public class AnnotationUtil {

    /**
     * The Class AnnotationWrapper. 该类用来生成annotation的缓存，用来快速获得查询结果
     *
     * @author Xia Zhengsheng
     * @version $Id: AnnotationUtil.java, v 0.1 2016-12-26 13:39:15 Xia zhengsheng Exp $
     */
    static class AnnotationWrapper {
        private Annotation annotation = null;

        public AnnotationWrapper(Annotation annotation) {
            this.annotation = annotation;
        }

        public Annotation getAnnotation() {
            return annotation;
        }
    }

    private static Map<AnnotatedElement, Map<Class<?>, AnnotationWrapper>> annotatedElementAnnosCache = new ConcurrentHashMap<AnnotatedElement, Map<Class<?>, AnnotationWrapper>>();

    /**
     * 该方法不同于spring,它可以获得继承的标注.
     *
     * @param <A> the generic type
     * @param annotatedElement the annotated element
     * @param annotationType the annotation type
     * @return the annotation
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        Map<Class<?>, AnnotationWrapper> wrappersMap = annotatedElementAnnosCache.get(annotatedElement);
        if (wrappersMap != null) {
            AnnotationWrapper annotationWrapper = wrappersMap.get(annotationType);
            if (annotationWrapper != null) {
                return (A) annotationWrapper.getAnnotation();
            }
        }

        if (wrappersMap == null) {
            wrappersMap = new ConcurrentHashMap<Class<?>, AnnotationUtil.AnnotationWrapper>();
            annotatedElementAnnosCache.put(annotatedElement, wrappersMap);
        }

        A internalAnnoation = internalGetAnnoation(annotatedElement, annotationType);
        AnnotationWrapper wrapper = new AnnotationWrapper(internalAnnoation);
        wrappersMap.put(annotationType, wrapper);
        return internalAnnoation;
    }

    /**
     * Internal get annoation.
     *
     * @param <A> the generic type
     * @param annotatedElement the annotated element
     * @param annotationType the annotation type
     * @return the a
     */
    private static <A extends Annotation> A internalGetAnnoation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        //用spring中的方法找annotation
        A annotation = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, annotationType);
        if (annotation != null) {
            return annotation;
        }

        if (annotatedElement instanceof Method) {
            Method method = (Method) annotatedElement;
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            Class<?>[] parameterTypes = method.getParameterTypes();

            Class<?> superclass = method.getDeclaringClass().getSuperclass();
            if (superclass == null || superclass.isAssignableFrom(Object.class)) {
                return null;
            }
            //从父方法中找annotation
            Method[] methods = superclass.getMethods();
            for (Method mtd : methods) {
                if (!ReflectionUtil.methodIsInherited(methodName, returnType, parameterTypes, mtd)) {
                    continue;
                }
                return internalGetAnnoation(mtd, annotationType);
            }
        }

        return annotation;
    }

    /**
     * Gets the annotation until supper.
     *
     * @param <A> the generic type
     * @param annotatedElement the annotated element
     * @param annotationType the annotation type
     * @return the annotation until supper
     */
    public static <A extends Annotation> A getAnnotationUntilSupper(AnnotatedElement annotatedElement, Class<A> annotationType) {
        A annotation = AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
        if (annotation == null) {
            if (annotatedElement instanceof Class) {
                Class<?> clz = (Class<?>) annotatedElement;
                if (clz.getSuperclass() != null) {
                    return getAnnotationUntilSupper(clz.getSuperclass(), annotationType);
                }
            }
        }
        return annotation;
    }

    /**
     * Gets the parent annotation.
     *
     * @param <A> the generic type
     * @param method the annotated element
     * @param annotationType the annotation type
     * @return the parent annotation
     */
    public static <A extends Annotation> A getMethodOwnerAnnotation(Method method, Class<A> annotationType) {
        Class<?> declaringClass = method.getDeclaringClass();
        return getAnnotation(declaringClass, annotationType);
    }

    /**
     * Gets the annotation include parent.
     *
     * @param <A> the generic type
     * @param method the annotated element
     * @param annotationType the annotation type
     * @return the annotation include parent
     */
    public static <A extends Annotation> A getMethodOrOwnerAnnotation(Method method, Class<A> annotationType) {
        A annotation = getAnnotation(method, annotationType);
        if (annotation == null) {
            annotation = getMethodOwnerAnnotation(method, annotationType);
        }
        return annotation;
    }

    public static <V, A extends Annotation> V getAnnotationValueFormMembers(Class<A> annotationType, Function<? super A, V> valueMethod,
                                                                            AnnotatedElement... members) {
        if (members != null && annotationType != null && valueMethod != null) {
            A mergedAnnotation = getAnnotationFormMembers(annotationType, members);
            if (mergedAnnotation != null) {
                return valueMethod.apply(mergedAnnotation);
            }
        }
        return null;
    }

    public static <V, A extends Annotation> V getAnnotationValueFormMembers(Class<A> annotationType, V defautValue,Function<? super A, V> valueMethod,
                                                                            AnnotatedElement... members) {
        V value = getAnnotationValueFormMembers(annotationType, valueMethod, members);
        if (value == null) {
            value = defautValue;
        }
        return value;
    }

    public static <A extends Annotation> A getAnnotationFormMembers(Class<A> annotationType, AnnotatedElement... members) {
        if (CollectionUtil.isNotEmpty(members)) {
            for (AnnotatedElement annotatedElement : members) {
                if (annotatedElement != null) {
                    A mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, annotationType);
                    if (mergedAnnotation != null) {
                        return mergedAnnotation;
                    }
                }
            }
        }
        return null;
    }
}
