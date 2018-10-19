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
package org.stategen.framework.progen.wrap;

import org.stategen.framework.progen.GenContext;
import org.stategen.framework.progen.PathType;
import org.stategen.framework.util.BusinessAssert;

/**
 * The Interface CanbeImportWrap.
 */
public interface CanbeImportWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CanbeImportWrap.class);
    
    Class<?> getClazz();
    
    void setClazz(Class<?> clazz);
    
    public default String getImportPath(){
        Class<?> clazz =getClazz();
        if (clazz.equals(Void.TYPE)) {
            return null;
        }
        
        if (GenContext.wrapContainer.checkAndGetFromSimple(clazz)!=null){
            return null;
        }
        
        PathType pathType = PathType.getPathTypeByWrap(this);
        BusinessAssert.mustNotNull(pathType, this.getClass()+"没有找到对应的路径");
        String importPath = GenContext.pathMap.get(pathType);
        BusinessAssert.mustNotNull(importPath, "没有找到对应的路径");
        return importPath;
    }
    
    public default String getWholeImportPath() {
        return null;
    }
}
