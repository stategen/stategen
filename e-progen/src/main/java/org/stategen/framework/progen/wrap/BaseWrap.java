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

import java.util.Map;

import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.lite.IPageList;
import org.stategen.framework.lite.IPagination;
import org.stategen.framework.lite.SimpleResponse;
import org.stategen.framework.progen.GenContext;

/**
 * The Class BaseWrap.
 */
public abstract class BaseWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BaseWrap.class);
    private Class<?> clazz;

    private String description;
    
    private Boolean genForm =false;
    

    public Boolean getIsArray() {
        return ClassHelpers.isArray(clazz);
    }

    public Boolean getIsSimple() {
        return false;
    }

    public Boolean getIsMap() {
        return Map.class.isAssignableFrom(clazz);
    }

    public Boolean getIsGeneric() {
        return false;
    }

    public boolean getIsEnum() {
        return clazz.isEnum();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getType() {
        BaseWrap baseWrap = GenContext.wrapContainer.get(clazz);
        return baseWrap.toString();
    }

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPageList() {
        return IPageList.class.isAssignableFrom(getClazz());
    }

    public Boolean getIsSimpleResponse() {
        return SimpleResponse.class.isAssignableFrom(getClazz());
    }
    
    public String getDescription() {
        return description;
    }
    
    public Boolean getIsPagination(){
        return IPagination.class.isAssignableFrom(getClazz());
    }

    public Boolean getGenForm() {
        return genForm;
    }
    
    public void setGenForm(Boolean genForm) {
        this.genForm = genForm;
    }
    
    public Boolean getIsBean(){
        return false;
    }
    
}
