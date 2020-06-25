
package org.stategen.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * 该类copy自spring AssertUtil,避免 util引用spring-core jar包.
 *
 * @version $Id: AssertUtil.java, v 0.1 2016年12月26日 下午1:45:40 niaoge Exp $
 */
public class AssertUtil {

    
    public static void throwException(String elseMessage){
        throw new IllegalArgumentException(elseMessage);
    }
    
    public static void throwException(String elseMessage,Throwable cause){
        throw new IllegalArgumentException(elseMessage,cause);
    }
    

    public static void mustTrue(boolean expression, String elseMessage) {
        if (!expression) {
            throwException(elseMessage);
        }
    }


    public static void mustTrue(boolean expression) {
        mustTrue(expression, "[Assertion failed] - this expression must be true");
    }
    
    public static void mustFalse(boolean expression, String elseMessage) {
        if (expression) {
            throwException(elseMessage);
        }
    }
    
    public static <K> void mustNotContains(Set<K> set,K key, String elseMessage){
        if (set.contains(key)){
            throwException(elseMessage);
        }
    }
    
    public static <K> void mustNotContainsAndAdd(Set<K> set,K key, String elseMessage){
        mustNotContains(set, key, elseMessage);
        set.add(key);
    }
    
    
    public static <K,V> void mustNotContains(Map<K,V> map,K key,V value, String elseMessage){
        if (map.containsKey(key)){
            throwException(elseMessage);
        }
    }
    
    public static <K,V> void mustNotContainsAndPut(Map<K,V> map,K key,V value, String elseMessage){
        mustNotContains(map, key, value, elseMessage);
        map.put(key, value);
    }
    
    
    public static void mustFalse(boolean expression) {
        mustFalse(expression, "[Assertion failed] - this expression must be true");
    }

    public static void mustNull(Object object, String elseMessage) {
        if (object != null) {
            throwException(elseMessage);
        }
    }


    public static void mustNull(Object object) {
        mustNull(object, "[Assertion failed] - the object argument must be null");
    }


    public static void mustNotNull(Object object, String elseMessage) {
        if (object == null) {
            throwException(elseMessage);
        }
    }

    public static void mustNotNull(Object object) {
        mustNotNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }


    public static void mustNotBlank(String text, String elseMessage) {
        if (StringUtil.isBlank(text)) {
            throwException(elseMessage);
        }
    }


    public static void mustNotBlank(String text) {
        mustNotBlank(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }
    
    public static void mustNotEmpty(String dest, String elseMessage) {
        if (StringUtil.isEmpty(dest)) {
            throwException(elseMessage);
        }
    }


    public static void mustNotEmpty(String dest) {
        mustNotEmpty(dest, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }  

    public static void mustNotEmpty(Object[] array, String elseMessage) {
        if (CollectionUtil.isEmpty(array)) {
            throwException(elseMessage);
        }
    }

    public static void mustNotEmpty(Object[] array) {
        mustNotEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }
    public static void mustNotEmpty(Collection<?> collection, String elseMessage) {
        if (CollectionUtil.isEmpty(collection)) {
            throwException(elseMessage);
        }
    }

    public static void mustNotEmpty(Collection<?> collection) {
        mustNotEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void mustNotEmpty(Map<?, ?> map, String elseMessage) {
        if (CollectionUtil.isEmpty(map)) {
            throwException(elseMessage);
        }
    }

    public static void mustNotEmpty(Map<?, ?> map) {
        mustNotEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }


    public static void mustInstanceOf(Class<?> clazz, Object obj) {
        mustInstanceOf(clazz, obj, "");
    }


    public static void mustInstanceOf(Class<?> type, Object obj, String elseMessage) {
        mustNotNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throwException((StringUtil.isNotBlank(elseMessage) ? elseMessage + " " : "") + "Object of class ["
                                               + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }

    public static void mustAssignable(Class<?> superType, Class<?> subType) {
        mustAssignable(superType, subType, "");
    }


    public static void mustAssignable(Class<?> superType, Class<?> subType, String elseMessage) {
        mustNotNull(superType, "Type to check against must not be null");
        if (!superType.isAssignableFrom(subType)) {
            throwException(
                (StringUtil.isNotBlank(elseMessage) ? elseMessage + " " : "") + subType + " is not assignable to " + superType);
        }
    }

}
