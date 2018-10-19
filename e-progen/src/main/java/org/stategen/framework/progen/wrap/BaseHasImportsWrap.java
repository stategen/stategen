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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.stategen.framework.progen.GenContext;
import org.stategen.framework.util.StringComparetor;
import org.stategen.framework.util.StringUtil;

/**
 * The Class BaseHasImportsWrap.
 */
public abstract class BaseHasImportsWrap extends EntityWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BaseHasImportsWrap.class);
    
    private Map<String/*simpleClassName*/,CanbeImportWrap> imports =new TreeMap<String, CanbeImportWrap>(new StringComparetor());
    
    
    public void addImport(BaseWrap baseWrap){
        if (baseWrap instanceof CanbeImportWrap){
            CanbeImportWrap canbeImportWrap =  (CanbeImportWrap)baseWrap;
            if (canbeImportWrap.getClazz().equals(Void.TYPE)) {
                return ;
            }
            String importPath = canbeImportWrap.getImportPath();
            String wholeImportPath = canbeImportWrap.getWholeImportPath();
            if (StringUtil.isEmpty(importPath) && StringUtil.isEmpty(wholeImportPath)){
                return;
            }
            
            Class<?> wrapClz = canbeImportWrap.getClazz();
            if (getClazz()!=wrapClz){
                if (GenContext.wrapContainer.needAddImports(wrapClz)){
                    String simpleName = wrapClz.getSimpleName();
                    imports.put(simpleName,canbeImportWrap);
                }
            }
        }
    }
    
    public Collection<CanbeImportWrap> getImports() {
        return imports.values();
    }
    
}
