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
/****
 * spring从/data/config/dalgenX.xml读取数据后放入该config中
 * 
 * @author XiaZhengsheng
 */
public class LocalCacheZkConfig {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LocalCacheZkConfig.class);
    
    protected static String                               zkConnectString     = null;
    protected static Integer                              zkConnectionTimeout = 10000;
    protected static String                               rootPath            = "/dalgenX/resourceCache";
    
    public void setRootPath(String rootPath) {
        AssertUtil.mustNotBlank(rootPath, "rootPath can not be empty");
        if (!rootPath.startsWith("/")){
            rootPath = "/"+rootPath;
        }
        
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }
        LocalCacheZkConfig.rootPath = rootPath;
    }

    public  void setConnectString(String zkConnectString) {
        AssertUtil.mustNotBlank(zkConnectString, "rootPath can not be empty!");
        final String zk="zookeeper://";
        if (zkConnectString.startsWith(zk)){
            zkConnectString=zkConnectString.substring(zk.length());;
        }
        LocalCacheZkConfig.zkConnectString = zkConnectString;
    }

    public void setConnectionTimeout(Integer zkConnectionTimeout) {
        if (zkConnectionTimeout <= 0) {
            logger.warn(new StringBuffer("zkConnectionTimeout时间小于0，设置不成功,").append(zkConnectionTimeout).toString());
            return;
        }
        LocalCacheZkConfig.zkConnectionTimeout = zkConnectionTimeout;
    }
}
