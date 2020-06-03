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

/**
 * *
 * 接口，用来生成 i18 ,
 * 这个接口用来处理java代码中的国际化问题，目前在框间中配合Response setMessage一起工作.
 *
 */
public interface I18nGenerator {
    
    /***
     *
     * 
     * @param destMessage  原如的信息，destMessage
     * @param agrs
     * @return
     */
    public String genBundleMessage(String destMessage,Object...agrs);
    
}
