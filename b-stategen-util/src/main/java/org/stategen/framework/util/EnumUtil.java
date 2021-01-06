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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ReflectionUtils;
import org.stategen.framework.lite.ValuedEnum;

/**
 * The Class EnumUtil.
 * 该类是将配后ibatis将int等转换为java枚举的工具类
 *
 * @author Xia Zhengsheng
 * @param <T> the generic type
 */
public class EnumUtil {

    private static Map<Class<?>, Map<String, ValuedEnum<?>>> enumCache = new ConcurrentHashMap<Class<?>, Map<String, ValuedEnum<?>>>();

    final static Map<Class<?>, Map<String, Method>> ENUM_GETMETHOD_MAP = new ConcurrentHashMap<Class<?>, Map<String, Method>>();

    /**
     * The Class EnumValuesMap.
     *
     * @param <T> the generic type
     */
    public static class EnumValuesMap<T extends Enum<T>> extends HashMap<String, Object> {
        /**  */
        private static final long serialVersionUID = 1L;
        T enumObj;
        final static String CODE = "code";
        final static String DESC = "desc";

        public EnumValuesMap(T enumObj) {
            @SuppressWarnings("unchecked")
            Map<String, Method> enumGetMethods = getEnumGetMethods(enumObj.getClass());
            if (CollectionUtil.isNotEmpty(enumGetMethods)) {
                for (Entry<String, Method> entry : enumGetMethods.entrySet()) {
                    Object value;
                    try {
                        value = entry.getValue().invoke(enumObj);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        continue;
                        //skip
                    }
                    this.put(entry.getKey(), value);
                }
            } else {
                this.put(CODE, enumObj.name());
            }
            this.enumObj = enumObj;
        }

        public T getEnumObj() {
            return enumObj;
        }

        public void setEnumObj(T enumObj) {
            this.enumObj = enumObj;
        }

        public void setDesc(String desc) {
            this.put(DESC, desc);
        }

        public String getDesc() {
            return (String) this.get(DESC);
        }
    }

    private static <T extends Enum<T>> Map<String, Method> getEnumGetMethods(Class<T> enumClz) {
        Map<String, Method> getMethodMap = ENUM_GETMETHOD_MAP.get(enumClz);
        if (getMethodMap == null) {
            getMethodMap = new HashMap<String, Method>();
            Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(enumClz);
            if (CollectionUtil.isNotEmpty(allDeclaredMethods)) {
                for (Method method : allDeclaredMethods) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers)) {
                        continue;
                    }

                    if (method.getParameterCount() > 0) {
                        continue;
                    }

                    String methodName = method.getName();
                    if ("getDeclaringClass".equals(methodName) || "getClass".equals(methodName)) {
                        continue;
                    }

                    if (!methodName.startsWith("get") || methodName.length() <= 3) {
                        continue;
                    }

                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                    if (getMethodMap.containsKey(fieldName)) {
                        continue;
                    }
                    getMethodMap.put(fieldName, method);
                }
            }
            ENUM_GETMETHOD_MAP.put(enumClz, getMethodMap);
        }
        return getMethodMap;
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class<? extends Enum<?>>, Map<? extends Enum, String>> descEnumCache = new ConcurrentHashMap<Class<? extends Enum<?>>, Map<? extends Enum, String>>();

    public static <T extends Enum<T>> Map<T, EnumValuesMap<T>> getEnumMapByEnumType(Class<T> enumType) {
        Map<T, String> enumMap = getEnumValueDescList(enumType);
        if (CollectionUtil.isEmpty(enumMap)) {
            return null;
        }

        Map<T, EnumValuesMap<T>> result = new HashMap<T, EnumValuesMap<T>>(enumMap.size());
        for (T t : enumMap.keySet()) {
            EnumValuesMap<T> enumEntry = new EnumValuesMap<T>(t);
            result.put(t, enumEntry);
        }

        return result;
    }

    public static <T extends Enum<T>> List<EnumValuesMap<T>> getEnumsByEnumType(Class<T> enumType) {
        Map<T, EnumValuesMap<T>> enumMapByEnumType = getEnumMapByEnumType(enumType);
        if (CollectionUtil.isNotEmpty(enumMapByEnumType)) {
            return new ArrayList<EnumValuesMap<T>>(enumMapByEnumType.values());
        }
        return CollectionUtil.newEmptyList();
    }

    /***与旧的系统兼容，将1,2,3等信息，如果是ValuedEnum,则转化为枚举，其它走Enum.valueOf*/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T valueOf(Class<T> enumType, String source) {
        if (source == null) {
            return null;
        }

        String soruceValue = source.trim();
        if (soruceValue.isEmpty()) {
            return null;
        }

        if (ValuedEnum.class.isAssignableFrom(enumType)) {
            Map<String, ValuedEnum<?>> cache = enumCache.get(enumType);
            if (cache != null) {
                ValuedEnum<?> valuedEnum = cache.get(soruceValue);
                if (valuedEnum != null) {
                    return (T) valuedEnum;
                }
            }
        }

        try {
            Class<? extends Enum> enumClz = (Class<? extends Enum>) enumType;
            return (T) Enum.valueOf(enumClz, soruceValue);
        } catch (Exception e) {
            //skip ,没有日志配置，用 printStackTrace
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends Enum<T>> Map<T, String> getEnumValueDescList(Class<T> enumType) {
        GetOrCreateWrap<Map<? extends Enum, String>> orCreateMap = CollectionUtil.getOrCreateMap(enumType, descEnumCache, HashMap.class);
        Map<T, String> enumMaps = (Map<T, String>) orCreateMap.getValue();
        if (orCreateMap.isNew()) {
            T[] enumConstants = enumType.getEnumConstants();
            for (T em : enumConstants) {
                String desc = (em instanceof ValuedEnum<?>) ? ((ValuedEnum<?>) em).getDesc() : em.name();
                enumMaps.put(em, desc);
            }
        }
        return enumMaps;
    }

    @SuppressWarnings("rawtypes")
    public static void registValuedEnum(Class<? extends ValuedEnum> veClass) {
        Object[] enumConstants = veClass.getEnumConstants();
        if (CollectionUtil.isNotEmpty(enumConstants)) {
            for (Object object : enumConstants) {
                ValuedEnum<?> valuedEnum = (ValuedEnum<?>) object;
                EnumUtil.putToCache(valuedEnum);
            }
        }
    }

    /***
     * 将枚举放入缓存，以便快速查询
     * 
     * @param valuedObject
     */
    protected static void putToCache(ValuedEnum<?> valuedObject) {
        if (valuedObject == null) {
            throw new IllegalArgumentException("valuedObject could not be null!");
        }

        if (!(valuedObject instanceof Enum)) {
            throw new IllegalArgumentException("valuedObject must  be Enum!");
        }

        try {
            Map<String, ValuedEnum<?>> cache = enumCache.get(valuedObject.getClass());
            if (cache == null) {
                cache = new ConcurrentHashMap<String, ValuedEnum<?>>();
                enumCache.put(valuedObject.getClass(), cache);
            }

            Object value = valuedObject.getValue();

            if (value == null) {
                //skip ,没有日志配置，用 printStackTrace
                System.out.println(valuedObject + "<===========> value:" + value);
                return;
            }

            cache.put(value.toString(), valuedObject);
        } catch (Exception e) {
            //skip ,没有日志配置，用 printStackTrace
            System.out.println(valuedObject + "注册失败1<===========>:\n" + e);
        }
    }

}
