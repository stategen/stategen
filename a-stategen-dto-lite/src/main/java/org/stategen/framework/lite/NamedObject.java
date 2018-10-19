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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * *
 *  该类暂时无用.
 *
 * @author XiaZhengsheng
 */
public abstract class NamedObject implements ValuedEnum<Integer> {
    
    private static Map<Class<?>, Map<Integer, NamedObject>> namedMap =new ConcurrentHashMap<Class<?>, Map<Integer,NamedObject>>();

    private Integer value;
    private String name;
    
    
    /**
     * 
     * @param name
     * @param value
     */
    public NamedObject(String name , Integer value){
        this.name =name;
        this.value =value;
        Map<Integer, NamedObject> thisClassCache =namedMap.get(this.getClass());
        if (thisClassCache==null){
            thisClassCache =new ConcurrentHashMap<Integer, NamedObject>();
            namedMap.put(this.getClass(), thisClassCache);
        }
        thisClassCache.put(value, this);
    }
    
    /**
     * Gets 获取名称
     *
     * @return the name
     */
    public  String getName(){
        return name;
    }
    
    @Override
    public Integer getValue() {
        return  value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}