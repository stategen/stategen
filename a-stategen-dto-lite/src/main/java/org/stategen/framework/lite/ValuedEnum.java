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
package org.stategen.framework.lite;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Interface ValuedEnum.
 *  该接口用来被其它枚举实现, 
 *  由于历史原因，数据库中很多状态用int表示的，对此ibatis需要写大理的typehandleCallback进行转换，用来该接口后，可以不用写转换器
 *  实现该接口的枚举可以被ibatis正确从数据库转换java枚举，也可以反过来保存至数据库或作为参数向数据库查询
 *  该接口需要配合 ValuedEnumScanner 使用
 * @author Xia Zhengsheng
 * @version $Id: ValuedEnum.java, v 0.1 2016-12-26 13:21:50 Xia zhengsheng Exp $
 * @param <T> the generic type
 */
public interface ValuedEnum<T>  {

    /**
     * 缺省getValue方法,请在枚举中加入field value,枚举可以不实现在getValue,该方法可默认代替
     *
     * @return the value
     */
    @SuppressWarnings("unchecked")
    default T getValue() {
        Field valueField = getValueField(this.getClass());
        if (valueField == null) {
            return (T) this.toString();
        }
        
        Object value = getFieldValue(valueField, this);
        if (value == null) {
            value = this.toString();
        }
        return (T) value;
    }
    
    /**
     * 缺省getDesc方法,请在枚举中加入field desc,枚举可以不实现在getDesc,该方法可默认代替
     *
     * @return the desc
     */
    default String getCode() {
        return this.toString();
    }    

    /**
     * 缺省getDesc方法,请在枚举中加入field desc,枚举可以不实现在getDesc,该方法可默认代替
     *
     * @return the desc
     */
    default String getDesc() {
        Field descField = getDescField(this.getClass());
        if (descField == null) {
            return this.toString();
        }
        String desc = (String) getFieldValue(descField, this);
        if (desc == null) {
            desc = this.toString();
        }
        return desc;
    }

    @SuppressWarnings("unchecked")
    static <T> T getFieldValue(Field valueField, ValuedEnum<T> target) {
        if (valueField != null) {
            try {
                return (T) valueField.get(target);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //skip
            }
        }
        return null;
    }

    static Map<Class<?>, FieldWrapper> fieldsCache = new ConcurrentHashMap<Class<?>, ValuedEnum.FieldWrapper>();

    /**
     * The Class FieldWrapper.
     *
     * @author Xia Zhengsheng
     * @version $Id: ValuedEnum.java, v 0.1 2016-12-26 13:21:50 Xia zhengsheng Exp $
     */
    class FieldWrapper {
        private Field valueField;
        private Field descField;

        public Field getValueField() {
            return valueField;
        }

        public void setValueField(Field valueField) {
            this.valueField = valueField;
        }

        public Field getDescField() {
            return descField;
        }

        public void setDescField(Field descField) {
            this.descField = descField;
        }
    }

    static Field getValueField(Class<?> targetClass) {
        FieldWrapper fieldWrapper = getFieldWrapper(targetClass);
        return fieldWrapper.getValueField();
    }

    static FieldWrapper getFieldWrapper(Class<?> targetClass) {
        FieldWrapper fieldWrapper = fieldsCache.get(targetClass);
        if (fieldWrapper == null) {
            fieldWrapper = new FieldWrapper();
            Field valueField = findField(targetClass, "value");
            Field descField = findField(targetClass, "desc");
            fieldWrapper.setValueField(valueField);
            fieldWrapper.setDescField(descField);
            fieldsCache.put(targetClass, fieldWrapper);
        }
        return fieldWrapper;
    }

    static Field getDescField(Class<?> targetClass) {
        FieldWrapper fieldWrapper = getFieldWrapper(targetClass);
        return fieldWrapper.getDescField();
    }

    static Field findField(Class<?> targetClass, String fieldName) {
        Field theField = null;
        try {
            theField = targetClass.getDeclaredField(fieldName);
            return setAccess(theField);
        } catch (NoSuchFieldException e) {
        }
        Class<?> superclass = targetClass.getSuperclass();
        if (superclass != null) {
            return findField(superclass, fieldName);
        } else {
            return theField;
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Member> T setAccess(final Field theField) {
        if (theField != null) {
            if (!theField.isAccessible())
                theField.setAccessible(true);
        }
        return (T) theField;
    }
    


}