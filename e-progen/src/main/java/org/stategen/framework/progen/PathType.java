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

import java.util.HashMap;
import java.util.Map;

import org.stategen.framework.progen.wrap.ApiWrap;
import org.stategen.framework.progen.wrap.BeanWrap;
import org.stategen.framework.progen.wrap.CanbeImportWrap;
import org.stategen.framework.progen.wrap.EnumWrap;

/**
 * The Enum PathType.
 */
public enum PathType {
    
    ENUM("enum",EnumWrap.class),
    BEAN("bean",BeanWrap.class),
    API("api",ApiWrap.class);
    
    PathType(String wrapName, Class<? extends CanbeImportWrap> canbeImportWrapClass){
        this.wrapName=wrapName;
        this.canbeImportWrapClass=canbeImportWrapClass;
    }
    
    private String wrapName;
    
    private Class<? extends CanbeImportWrap> canbeImportWrapClass;
    

    public String getWrapName() {
        return wrapName;
    }
    
    public Class<? extends CanbeImportWrap> getCanbeImportWrapClass() {
        return canbeImportWrapClass;
    }
    
    private static Map<Class<? extends CanbeImportWrap>, PathType> baseWrapPathTypMap =null;
    
    public static PathType  getPathTypeByWrap(CanbeImportWrap canbeImportWrap){
        Class<?> canBeImportClass =canbeImportWrap.getClass();
        if (baseWrapPathTypMap==null){
            baseWrapPathTypMap =new HashMap<Class<? extends CanbeImportWrap>, PathType>(2);
            for (PathType pathType : PathType.values()) {
                baseWrapPathTypMap.put(pathType.getCanbeImportWrapClass(), pathType);
            }
        }
        
        return baseWrapPathTypMap.get(canBeImportClass);
    }
    

}
