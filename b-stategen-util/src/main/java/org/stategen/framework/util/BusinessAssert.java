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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.org.stategen.framework.lite.BusinessException;

/**
 * The Class BusinessAssert.
 */
public class BusinessAssert {

    public static void throwException(String elseMessage){
        throw new BusinessException(elseMessage);
    }
    
    public static boolean mustTrue(boolean expression, String elseMessage) {
        if (!expression) {
            BusinessAssert.throwException(elseMessage);
        }
        return expression;
    }
    

    
    public static boolean mustFalse(boolean expression, String elseMessage) {
        if (expression) {
            BusinessAssert.throwException(elseMessage);
        }
        return expression;
    }
    


    public static <T> T mustNotNull(T object, String elseMessage) {
        if(object == null) {
            BusinessAssert.throwException(elseMessage);
        }
        return object;
    }
    


    public static String mustNotEmpty(String str, String elseMessage) {
        if(org.stategen.framework.util.StringUtil.isEmpty(str)) {
            BusinessAssert.throwException(elseMessage);
        }
        return str;
    }
    
    public static <K,V,M extends Map<K, V>> M  mustNotEmpty(M map, String elseMessage) {
        if(org.stategen.framework.util.CollectionUtil.isEmpty(map)) {
            BusinessAssert.throwException(elseMessage);
        }
        return map;
    }
    
    public static <K,V,M extends Map<K, V>> M  mustNotEmpty(M map) {
        return mustNotEmpty(map,"The map must not be empty");
    }
    
    public static <T,L extends Collection<T>> L mustNotEmpty(L items,String elseMessage) {
        if(org.stategen.framework.util.CollectionUtil.isEmpty(items)) {
            BusinessAssert.throwException(elseMessage);
        }
        return items;
    }
    

    public static String mustNotBlank(String str, String elseMessage) {
        if(org.stategen.framework.util.StringUtil.isBlank(str)) {
            BusinessAssert.throwException(elseMessage);
        }
        return str;
    }
    


    public static <T> void mustEqual(Class<T> type ,T objectA, T objectB, String elseMessage) {
        if (type!=null){
            if (Enum.class.isAssignableFrom(type)){
                if(objectA != objectB) {
                    BusinessAssert.throwException(elseMessage);
                }
                return;
            }
        }
        
        if (objectA==null || objectB==null){
            BusinessAssert.throwException(elseMessage);
        }
        
        if (!objectA.equals(objectB)){
            BusinessAssert.throwException(elseMessage);
        }
    }
    
    public static <T> void mustEqual(Class<T> type ,T objectA, T objectB) {
        mustEqual(type, objectA, objectB,"The object A must not be null and must equal to the object B");
    }

    public static <K> void mustNotContainsAndAdd(Set<K> set,K key, String elseMessage){
        if (set.contains(key)){
            throwException(elseMessage);
        }
        set.add(key);
    }

}
