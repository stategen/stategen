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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * The Class GenericTypeResolver.
 */
public class GenericTypeResolver {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericTypeResolver.class);
    
    public static Class<?> getClass(Type genericType, int i) { 
        Class<?> result =null;
        if (genericType instanceof ParameterizedType) { // 处理泛型类型    
            ParameterizedType parameterizedType =(ParameterizedType) genericType;
            genericType = parameterizedType.getActualTypeArguments()[i]; 
            result=  getInternalGenericClass(genericType, i);  
        } else {
            result = getInternalGenericClass(genericType, i);
        } 
        return result;
    }   
    

    private static Class<?> getInternalGenericClass(Type genericType, int i) {    
        if (genericType instanceof ParameterizedType) { // 处理多级泛型   
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            genericType= parameterizedType.getRawType();     
        } else if (genericType instanceof GenericArrayType) { // 处理数组泛型     
            GenericArrayType genericArrayType = (GenericArrayType) genericType;
            genericType= genericArrayType.getGenericComponentType();     
        } else if (genericType instanceof TypeVariable) { // 处理泛型擦拭对象   
            TypeVariable<?> typeVariable = (TypeVariable<?>) genericType;
            //再递归一次
            genericType= getClass((typeVariable).getBounds()[0], 0);     
        } else if (genericType instanceof WildcardType){
            WildcardType wildcardType =((WildcardType)genericType);
            Type[] bounds = wildcardType.getLowerBounds(); // '? extends Number'
            if (bounds.length == 0) { 
                bounds = wildcardType.getUpperBounds(); // '? super Integer'
            }
            genericType = bounds[i];
        }
        
        return getClazz(genericType);
    }
    
    public static String getGenericName(Type genericType, int i) { 
        String genericName=null;
        if (ParameterizedType.class.isInstance(genericType)) { // 处理泛型类型    
            ParameterizedType parameterizedType =(ParameterizedType) genericType;
            genericType = parameterizedType.getActualTypeArguments()[i];  
            genericName= getInternalGenericName(genericType, i);   
        } else  {     
            genericName= getInternalGenericName(genericType, i);  
        } 
        return genericName;
    }     
    
    
    private static String getInternalGenericName(Type genericType, int i) {    
        String genericName=null;
        if (genericType instanceof ParameterizedType) { // 处理多级泛型   
            ParameterizedType subParameterizedType= (ParameterizedType) genericType;
            genericName=subParameterizedType.getTypeName();
        } else if (genericType instanceof GenericArrayType) { // 处理数组泛型  
            GenericArrayType genericArrayType= (GenericArrayType) genericType;
            genericType = genericArrayType.getGenericComponentType();
            //再递归一次
            genericName =getInternalGenericName(genericType,i); 
        } else if (genericType instanceof TypeVariable) { // 处理泛型擦拭对象     
            TypeVariable<?> typeVariable =(TypeVariable<?>) genericType;
            genericName =typeVariable.getName();
        } else if (genericType instanceof WildcardType){
            WildcardType wildcardType =((WildcardType)genericType);
            genericName=wildcardType.getTypeName();
        } 
        
        return genericName;
    }

    private static Class<?> getClazz(Type destType) {
        Class<?> cls = (Class<?>)destType;
        return cls.isArray() ? cls.getComponentType() : cls;
    }   

}
