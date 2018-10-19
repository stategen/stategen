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
package org.stategen.framework.cache;

/**
 * 封装缓存名称的基类.
 *
 * @author XiaZhengsheng
 */
abstract class BaseLocalCacheNameTaker{
    //private String resourceName ;
    
    private String tableName ;
    
    private String notifyName ;
    
    private String internalNotifyName;
    
    
//    public void setResourceName(String resourceName) {
//        AssertUtil.mustNotBlank(resourceName, "resourceName can not be empty!");
//        this.resourceName =resourceName;
//    }
    
//    public String getResourceName() {
//        return resourceName;
//    }
    
    public void setNotifyName(String notifyName) {
        internalNotifyName=null;
        this.notifyName = notifyName;
    }

    public void setTableName(String tableName) {
        internalNotifyName=null;
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return tableName;
    }    
    
    public String getNotifyName() {
        if (internalNotifyName==null){
            internalNotifyName=LocalCacheUtil.buildNotifyName(notifyName, tableName);
        }
        return internalNotifyName;
    }
}
