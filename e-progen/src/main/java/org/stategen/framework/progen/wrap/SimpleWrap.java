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

import org.stategen.framework.generator.util.ClassHelpers;

/**
 * The Class SimpleWrap.
 */
public class SimpleWrap extends BaseWrap implements CanbeImportWrap {
    private String convertToClzName;
    
    private String wholeImportPath;

    public SimpleWrap(Class<?> clz, String convertToClzName) {
        super();
        setClazz(clz);
        this.convertToClzName = convertToClzName;
    }

    public String getConvertToClzName() {
        return convertToClzName;
    }

    @Override
    public String toString() {
        return convertToClzName;
    }

    @Override
    public Boolean getIsGeneric() {
        return ClassHelpers.isArray(getClazz());
    }
    
    public Boolean getIsSimple(){
        return true;
    }
    
    @Override
    public String getImportPath() {
        return null;
    }
    
    public String getWholeImportPath() {
        return wholeImportPath;
    }
    
    public void setWholeImportPath(String wholeImportPath) {
        this.wholeImportPath = wholeImportPath;
    }
    
}
