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
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import org.stategen.framework.util.CollectionUtil;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;

/**
 * The Class FastJsonSerializeConfig.
 */
public class FastJsonSerializeConfig extends com.alibaba.fastjson.serializer.SerializeConfig{
    
    
    public void setSerializers(Map<Type, ObjectSerializer> dest){
        this.put(BigInteger.class, ToStringSerializer.instance);
        this.put(Long.class, ToStringSerializer.instance);
        this.put(Long.TYPE, ToStringSerializer.instance);
        if (CollectionUtil.isNotEmpty(dest)){
            for (Entry<Type, ObjectSerializer> entry :dest.entrySet()){
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
