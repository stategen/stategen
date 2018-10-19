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
package org.stategen.framework.spring.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * *
 * 国际化或分布式资源提供器接口.
 *
 * @author XiaZhengsheng
 */
public interface BundleProvider {
    
    public String resolveCodeWithoutArguments(String code, Locale locale);

    public MessageFormat resolveCode(String code, Locale locale);
    
    public List<ConfigBean> getConfigBeans(String resourceName, String tableName);
    
    public void setUseCodeAsDefaultMessage(Boolean useCodeAsDefaultMessage);
    
    public Boolean getUseCodeAsDefaultMessage();
    
    public Map<String,String> getBundle(Object index);
    
    public Integer getBundleSign(Object index);
    
    public Object stringToIndex(String indexString);
    
}
