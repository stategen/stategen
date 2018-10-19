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
package org.stategen.framework.convertion;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.stategen.framework.util.CollectionUtil;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

/**
 * The Class FastJsonParserConfig.
 */
public class FastJsonParserConfig extends ParserConfig {
    
    public FastJsonParserConfig(){
        super();
        ParserConfig.global =this;
    }
    
    public void setDeserializers(Map<Type, ObjectDeserializer> dest){
        if (CollectionUtil.isNotEmpty(dest)){
            for (Entry<Type, ObjectDeserializer> entry :dest.entrySet()){
                this.putDeserializer(entry.getKey(), entry.getValue());
            }
        }
    }
    
//    @Override
//    public ObjectDeserializer getDeserializer(Type type) {
//        ObjectDeserializer deserializer =null;
//        if (type instanceof Class){
//            Class<?> clz =(Class<?>)type;
//            if (ValuedEnum.class.isAssignableFrom(clz)){
//                deserializer = getDerializers().get(ValuedEnum.class);
//                if (deserializer!=null){
//                    return deserializer;
//                }
//            }
//        }
//        return super.getDeserializer(type);
//    }
    
//    @Override
//    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
//        ObjectDeserializer deserializer =null;
//        if (ValuedEnum.class.isAssignableFrom(clazz)){
//            deserializer = super.getDeserializer(ValuedEnum.class);
//            return deserializer;
//        }
//        
//        deserializer= super.getDeserializer(clazz, type);
//        return deserializer;
//    }
    
//    @Override
//    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
//        ObjectDeserializer deserializer =null;
//        if (ValuedEnum.class.isAssignableFrom(clazz)){
//            deserializer = super.getDeserializer(clazz,ValuedEnum.class);
//            return deserializer;
//        }
//        
//        deserializer= super.getDeserializer(clazz, type);
//        return deserializer;
//    }    
}
