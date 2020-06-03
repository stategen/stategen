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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

/**
 * The Class ReflectionUtil.
 * java反射的工具类,复制自 dynamic-spring(niaoge)
 * @author niaoge
 */
public class ReflectionUtil {

    /**
     *  <pre>   The Constant logger.  </pre>
     */
    final static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * Gets the class.
     *
     * @param target the target
     * @return the class
     */
    public static Class<?> getClass(Object target) {
        return (target instanceof Class ? (Class<?>) target : target.getClass());
    }

    /**
     * Sets the access.
     *
     * @param <T> the generic type
     * @param theMember the the member
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public static <T extends Member> T setAccess(final Member theMember) {
        if (theMember instanceof Method) {
            Method real = (Method) theMember;
            if (! real.isAccessible()) {
                real.setAccessible(true);
            }
        } else if (theMember instanceof Constructor) {
            @SuppressWarnings("rawtypes")
            Constructor real = (Constructor) theMember;
            if (!real.isAccessible()) {
                real.setAccessible(true);
            }
        } else if (theMember instanceof Field) {
            Field real = (Field) theMember;
            if (!real.isAccessible()) {
                real.setAccessible(true);
            }
        }
        return (T) theMember;
    }

    /**
     * Find field.
     *
     * @param target the target
     * @param fieldName the field name
     * @return the field
     */
    public static Field findField(Object target, String fieldName) {
        if (target == null) {
            return null;
        }
        Class<?> targetClass = getClass(target);
        Field    theField;
        try {
            theField = targetClass.getDeclaredField(fieldName);
            return setAccess(theField);
        } catch (NoSuchFieldException e) {
        }
        return findField(targetClass.getSuperclass(), fieldName);
    }

    public static List<Field> getAllDeclaredFields(Class<?> targetClass) {
        List<Field> result = new ArrayList<Field>(32);
        while (targetClass != null && targetClass != Object.class) {
            Field[] declaredFields = targetClass.getDeclaredFields();
            if (CollectionUtil.isNotEmpty(declaredFields)) {
                for (Field field : declaredFields) {
                    result.add(field);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return result;
    }

    /**
     * Find method.
     *
     * @param target the target
     * @param methodName the method name
     * @return the method
     */
    public static Method findMethod(Object target, String methodName) {
        if (target == null) {
            return null;
        }

        Class<?> targetClass = getClass(target);
        Method   theMethod;
        try {
            theMethod = targetClass.getDeclaredMethod(methodName);
            return setAccess(theMethod);
        } catch (NoSuchMethodException e) {
            return findMethod(targetClass.getSuperclass(), methodName);
        }
    }

    /**
     * Find fields.
     *
     * @param target the target
     * @param fieldNames the field names
     * @return the sets the
     */
    public static Set<Field> findFields(Object target, String... fieldNames) {
        if (target == null) {
            return null;
        }

        if (CollectionUtil.isEmpty(fieldNames)) {
            return null;
        }

        Set<Field> result = new HashSet<Field>();
        for (String fieldName : fieldNames) {
            Field theField = findField(target, fieldName);
            if (theField != null) {
                result.add(theField);
            }
        }
        return result;
    }

    /**
     * Find fields.
     *
     * @param target the target
     * @param fieldNames the field names
     * @return the sets the
     */
    public static Set<Field> findFields(Object target, Set<String> fieldNames) {
        if (target == null) {
            return null;
        }

        if (CollectionUtil.isEmpty(fieldNames)) {
            return null;
        }

        Set<Field> result = null;
        result = new HashSet<Field>();
        for (String fieldName : fieldNames) {
            Field theField = findField(target, fieldName);
            if (theField != null) {
                result.add(theField);
            }
        }
        return result;
    }

    /**
     * Find fields map.
     *
     * @param target the target
     * @param fieldNames the field names
     * @return the map
     */
    public static Map<String, Field> findFieldsMap(Object target, String... fieldNames) {
        if (target == null) {
            return null;
        }

        if (CollectionUtil.isEmpty(fieldNames)) {
            return null;
        }

        Map<String, Field> result = null;
        result = new HashMap<String, Field>(fieldNames.length);
        for (String fieldName : fieldNames) {
            Field theField = findField(target, fieldName);
            if (theField != null) {
                result.put(fieldName, theField);
            }
        }
        return result;
    }

    /**
     * Find field.
     *
     * @param target the target
     * @param ann the ann
     * @return the field
     */
    public static Field findField(Object target, Class<? extends Annotation> ann) {
        if (target == null) {
            return null;
        }
        Class<?> targetClass = getClass(target);
        Field[]  fields      = targetClass.getDeclaredFields();

        for (Field field : fields) {
            Annotation tmpAnn = field.getAnnotation(ann);
            if (tmpAnn != null) {
                return setAccess(field);
            }
        }
        return findField(targetClass.getSuperclass(), ann);
    }

    /**
     * Checks if is field type.
     *
     * @param field the field
     * @param clz the clz
     * @return true, if is field type
     */
    public static boolean isFieldType(Field field, Class<?> clz) {
        if (field != null) {
            Class<?> type = field.getType();
            if (type.isAssignableFrom(clz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find field.
     *
     * @param obj the obj
     * @param fieldName the field name
     * @param type the type
     * @return the field
     */
    public static Field findField(Object obj, String fieldName, Class<?> type) {
        if (obj == null) {
            return null;
        }
        Field theField = findField(obj, fieldName);
        if (isFieldType(theField, type)) {
            return theField;
        }
        return null;
    }

    /**
     * Gets the first upper.
     *
     * @param name the name
     * @return the first upper
     */
    public static String getFirstUpper(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        StringBuffer sb = new StringBuffer(name.length());
        sb.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1));
        return sb.toString();
    }

    /**
     * Gets the setter or setter method name.
     *
     * @param fieldName the name
     * @param isSet the is set
     * @return the setter or setter method name
     */
    public static String getSetterOrSetterMethodName(String fieldName, boolean isSet) {
        if (StringUtil.isEmpty(fieldName)) {
            return fieldName;
        }
        StringBuffer sb = new StringBuffer(fieldName.length() + 3);
        if (isSet) {
            sb.append("set");
        } else {
            sb.append("get");
        }
        sb.append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1));
        return sb.toString();

    }

    /**
     * Gets the field value.
     *
     * @param <T> the generic type
     * @param target the target
     * @param fieldName the field name
     * @return the field value
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        Field theField = findField(target, fieldName);
        return getFieldValue(target, theField);
    }

    /*
     * public static <T> T getMethodAnnotation(Class<?> clz,Class<T> ann){
     * clz.get }
     */

    public static void setFieldValue(Object target, Field theField, Object value) {
        if (theField != null) {
            setAccess(theField);
            try {
                Object newValue = ConvertUtils.convert(value, theField.getType());
                theField.set(target, newValue);
            } catch (Exception e) {
                logger.error("在调用时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:\n" + e.getMessage() + " \n", e);
            }
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        Field theField = findField(target, fieldName);
        if (theField != null) {
            setFieldValue(target, theField, value);
        }
    }

    /**
     * Gets the field value.
     *
     * @param <T> the generic type
     * @param target the target
     * @param theField the the field
     * @return the field value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Field theField) {
        if (theField != null) {
            setAccess(theField);
            try {
                return (T) theField.get(target);
            } catch (IllegalArgumentException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }
        return null;
    }

    /***
     * TODO,还需要加上Parameter
     * @param member
     * @param annoType
     * @return
     */
    public static <A extends Annotation> A getAnntationUntilParent(Member member, Class<A> annoType) {
        A anno = null;

        if (Method.class.isInstance(member)) {
            Method method = ((Method) member);
            anno = method.getAnnotation(annoType);
        }

        if (anno == null) {
            return member.getDeclaringClass().getAnnotation(annoType);
        }

        return null;
    }

    public static boolean methodIsInherited(String methodName, Class<?> returnType, Class<?>[] parameterTypes, Method targetMtd) {
        if (!methodName.equals(targetMtd.getName())) {
            return false;
        }

        if (!returnType.equals(targetMtd.getReturnType())) {
            return false;
        }

        Class<?>[] subParameterTypes = targetMtd.getParameterTypes();

        if (CollectionUtil.isEmpty(parameterTypes) && CollectionUtil.isNotEmpty(subParameterTypes)) {
            return false;
        }

        if (CollectionUtil.isNotEmpty(parameterTypes) && CollectionUtil.isEmpty(subParameterTypes)) {
            return false;
        }

        if (CollectionUtil.isEmpty(parameterTypes) && CollectionUtil.isEmpty(subParameterTypes)) {
            return true;
        }

        if (parameterTypes.length != subParameterTypes.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].equals(subParameterTypes[i])) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNormalMethod(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers);
    }

    final static String GET ="get";
    final static char A ='A';
    final static char Z ='Z';
    
    public static String isAndGetGetterMethodFeildName(Method method) {
        if (ReflectionUtil.isNormalMethod(method) && (method.getReturnType() != Void.class)
            && CollectionUtil.isEmpty(method.getGenericParameterTypes())) {
            String methodName = method.getName();
            int    length     = methodName.length();
            if (length > GET.length() && methodName.startsWith(GET)) {
                char first = methodName.charAt(3);
                if (first >= A && first <= Z) {
                    first = Character.toLowerCase(first);
                    return new StringBuffer(length - 3).append(first).append(methodName, 4, length).toString();
                }
            }
        }
        return null;
    }

    /***不获取到Object中的method*/
    public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && superclass != Object.class) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    public static Map<String, Method> getGetterNameMethods(Class<?> currentType) {
        Map<String, Method> getterNameMethodMap = new LinkedHashMap<String, Method>(64);
        doWithMethods(currentType, new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                String getterMethodFeildName = ReflectionUtil.isAndGetGetterMethodFeildName(method);
                getterNameMethodMap.put(getterMethodFeildName, method);

            }
        }, new MethodFilter() {
            @Override
            public boolean matches(Method method) {
                String getterMethodFeildName = ReflectionUtil.isAndGetGetterMethodFeildName(method);
                return StringUtil.isNotEmpty(getterMethodFeildName) && !getterNameMethodMap.containsKey(getterMethodFeildName);
            }
        });

        return getterNameMethodMap;
    }

    public static Map<String, Field> getFieldNameFieldMap(Class<?> currentType) {
        Map<String, Field> fieldNameFieldMap = new LinkedHashMap<String, Field>(64);
        ReflectionUtils.doWithFields(currentType, new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                fieldNameFieldMap.put(field.getName(), field);
            }
        }, new FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return !fieldNameFieldMap.containsKey(field.getName());
            }
        });

        return fieldNameFieldMap;
    }

    public static String getJavaConsoleLink(AccessibleObject accessibleObject) {
        String        className  = null;
        StringBuilder sb         = new StringBuilder("\n★★★★★ at ");
        String        fieldName  = null;
        String        methodName = null;
        String        simpleName = null;

        if (accessibleObject instanceof Executable) {
            Executable executable = (Executable) accessibleObject;
            className = executable.getDeclaringClass().getPackage().getName() + '.' + executable.getDeclaringClass().getSimpleName();
            if (!(executable instanceof Constructor)) {
                methodName = executable.getName();
            } else {
                methodName = executable.getDeclaringClass().getSimpleName();
            }

            simpleName = executable.getDeclaringClass().getSimpleName();

            //append(".java:0)");
        } else {
            Field field = (Field) accessibleObject;
            fieldName  = field.getName();
            simpleName = field.getDeclaringClass().getSimpleName();
            className  = field.getDeclaringClass().getPackage().getName() + '.' + simpleName;

        }
        sb.append(className);

        int xlineNumber = 0;
        if (fieldName != null) {
            sb.append(".").append(fieldName);
        } else if (methodName != null) {
            sb.append(".").append(methodName);
        }

        sb.append("(").append(simpleName);
        sb.append(".java:");

        sb.append(xlineNumber).append(")");

        String errMessage = sb.toString();
        return errMessage;
    }
}
