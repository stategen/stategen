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

import org.stategen.framework.util.AssertUtil;

/**
 * *
 * 该类封装本地一级缓存的通知类，用于zookeeper中注册监听器和获得zookeeper中的通知
 * 该类避免直接操作 LocalCacheUtil.
 *
 * @author XiaZhengsheng
 */
public class LocalCacheNotifier extends BaseLocalCacheNameTaker {

    public LocalCacheNotifier(){
        super();
    }
    
    public LocalCacheNotifier(String notifyName,  String tableName) {
        super();
        setNotifyName(notifyName);
        setTableName(tableName);
    }
    
    public void notifyResourceChanged(){
        LocalCacheUtil.notifyResourceChanged(getNotifyName());
    }
    
    public void notifyResourceChanged(String resourceName){
        AssertUtil.mustNotBlank(resourceName);
        LocalCacheUtil.notifyResourceChanged(LocalCacheUtil.buildNotifyName(resourceName, getTableName()));
    }

    public boolean deleteResourceNode() {
        return LocalCacheUtil.deleteResourceNode(getNotifyName());
    }
}
