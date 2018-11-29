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
package org.stategen.framework.enums;

/**
 * The Enum DataOpt.
 */
public enum DataOpt {
    /***替换state中的Array*/
    FULL_REPLACE,
    
    /***更新当前Array中的内容，如果Id相同，则用新的替换，如果没有，则增加*/
    APPEND_OR_UPDATE,
    
    /***带入id查找当前Array中的内容，如果有，删除*/
    DELETE_IF_EXIST,
}
