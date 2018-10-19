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
package org.stategen.framework.ibatis.typehandler;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.stategen.framework.lite.ValuedEnum;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * **
 * 该类由于技术改进，暂时不用到.
 *
 * @author XiaZhengsheng
 */
public abstract class AbstractValuedObjectCallback implements TypeHandlerCallback {
    
    public abstract <T> Class<T>  getValuedClass();
    
    static Map<Class<? extends ValuedEnum>,ValueAndNameHolder > VALUED_MAPPING = new HashMap<Class<? extends ValuedEnum>, ValueAndNameHolder>();
    
    public static boolean isEmpty(Map<?, ?> map){
        return map==null || map.isEmpty();
    }    

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        if (null == parameter) {
            setter.setNull(Types.VARCHAR);
        } else {
            Object enumName =( (ValuedEnum)parameter).getValue();
            setter.setObject(enumName);
        }
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        Object result = null;
        String value = getter.getString();
        result=valueOf(getValuedClass(),value);
        return result;
    }

    public Object valueOf(String name) {
        Object result = null;
        result = nameOf(getValuedClass(), name);
        return result;
    }
    
    /**
     * The Class ValueAndNameHolder.
     */
    static class ValueAndNameHolder{
        Map<String, ValuedEnum> valueStringMapping =new HashMap<String, ValuedEnum>();
        Map<String, ValuedEnum> nameStringMapping =new HashMap<String, ValuedEnum>();
        
        ValuedEnum valueOf(String value) {
            if (!isEmpty(valueStringMapping)) {
                return valueStringMapping.get(value);
            }
            return null;
        }
        
        ValuedEnum nameOf( String name) {
            if (!isEmpty(nameStringMapping)) {
                return nameStringMapping.get(name);
            }
            return null;
        }       
        
        void registValues(ValuedEnum[] values){
            for (ValuedEnum en : values) {
                Object value = en.getValue();
                valueStringMapping.put(value == null ? null : value.toString(), en);
                nameStringMapping.put(en.toString(), en);
            }
        }
    }
    
    public static boolean isEmpty(ValuedEnum[] array){
        return array==null || array.length==0;
    }        
    
    public static void registValuedObjects(ValuedEnum[] valuedObjects) {
        if (isEmpty(valuedObjects)) {
            return;
        }
        
        Class<? extends ValuedEnum> enumClass = valuedObjects[0].getClass();
        ValueAndNameHolder valueAndNameHolder =VALUED_MAPPING.get(enumClass);
        if (valueAndNameHolder == null) {
            valueAndNameHolder = new ValueAndNameHolder();
            VALUED_MAPPING.put(enumClass, valueAndNameHolder);
        }
        valueAndNameHolder.registValues(valuedObjects);
    }

    public static ValuedEnum valueOf(Class<?> valuedClass, String value) {
        ValueAndNameHolder valueAndNameHolder =VALUED_MAPPING.get(valuedClass);
        return valueAndNameHolder==null?null:valueAndNameHolder.valueOf(value);
    }
    
    public static ValuedEnum nameOf(Class<?> valuedClass, String name) {
        ValueAndNameHolder valueAndNameHolder =VALUED_MAPPING.get(valuedClass);
        return valueAndNameHolder==null?null:valueAndNameHolder.nameOf(name);
    }           
    
    
}
