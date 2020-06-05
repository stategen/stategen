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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.data.Stat;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;

/**
 * *
 * 该类用于获得本地一级缓存，接收zookeeper通知清除本地一级缓存等.
 *
 * @author XiaZhengsheng
 */
public class LocalCacheUtil {
    final static org.slf4j.Logger    logger    = org.slf4j.LoggerFactory.getLogger(ZkResourceDataListener.class);
    private static Map<String, DataWrapper>   resourceCache = new ConcurrentHashMap<String, DataWrapper>();
    public static ZkClient    zkClient       = null;
    private static Map<String/*notifyName*/, String/*notifyName*/> dataListenerCache  = new ConcurrentHashMap<String, String>();

    /**
     * 本地一级缓存的data封装.
     *
     * @author XiaZhengsheng
     */
    static class DataWrapper {
        private Long                nanoTime;
        private Map<String/*dataName*/, Object> dataCache = new ConcurrentHashMap<String, Object>();

        public DataWrapper() {
        }

        public Long getNanoTime() {
            return nanoTime;
        }

        public void setNanoTime(Long nanoTime) {
            this.nanoTime = nanoTime;
        }

        public Object getData(String dataName) {
            return dataCache.get(dataName);
        }

        public void setData(String dataName, Object data) {
            dataCache.put(dataName, data);
        }

        public void clean() {
            dataCache.clear();
        }
    }
    
    public static String buildNotifyName(String notifyName ,String tableName){
        if (StringUtil.isNotEmpty(notifyName) && StringUtil.isNotEmpty(tableName)){
            return new StringBuilder().append(notifyName).append(".").append(tableName).toString();
        }
        return null;
    }
    
    /**
     * Gets the cache.
     *
     * @param <T> the generic type
     * @param notifyName the resource name
     * @param dataNode the data name
     * @return the cache
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCache(String notifyName, String dataNode) {
        DataWrapper dataWrapper = resourceCache.get(notifyName);
        if (dataWrapper != null) {
            return (T) dataWrapper.getData(dataNode);
        }
        return null;
    }

    /**
     * Gets the or create data wrapper.
     *
     * @param notifyName the resource name
     * @return the or create data wrapper
     */
    protected static DataWrapper getOrCreateDataWrapper(String notifyName) {
        DataWrapper dataWrapper = resourceCache.get(notifyName);
        if (dataWrapper == null) {
            dataWrapper = new DataWrapper();
            resourceCache.put(notifyName, dataWrapper);
        }
        return dataWrapper;
    }

    /**
     * Put to cache.
     *
     * @param notifyName the resource name
     * @param dataName the data name
     * @param data the data
     */
    public static void putToCache(String notifyName, String dataName, Object data) {
        Long resourceNano = startListener(notifyName);
        DataWrapper dataWrapper = getOrCreateDataWrapper(notifyName);
        dataWrapper.setData(dataName, data);
        if (resourceNano == null) {
            resourceNano = System.nanoTime();
        }
        dataWrapper.setNanoTime(resourceNano);
    }

    /**
     * The listener interface for receiving zkResourceData events.
     * The class that is interested in processing a zkResourceData
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addZkResourceDataListener<code> method. When
     * the zkResourceData event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ZkResourceDataEvent
     */
    protected static class ZkResourceDataListener implements IZkDataListener {
        private String notifyName = null;

        public ZkResourceDataListener(String notifyName) {
            this.notifyName = notifyName;
        }

        public void handleDataChange(String dataPath, Object data) throws Exception {
            DataWrapper dataWrapper = getOrCreateDataWrapper(notifyName);
            if (!data.equals(dataWrapper.getNanoTime())) {
                Long nanoTime = (Long) data;
                dataWrapper.setNanoTime(nanoTime);
                dataWrapper.clean();
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuilder("dataPath节点时间改变,本地数据失效,将被清除").append(dataPath).append("   ").append(nanoTime).toString());
                }
            }
        }

        public void handleDataDeleted(String dataPath) throws Exception {
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuilder("zk dataPath被删除:").append(dataPath).toString());
            }
        }

    }

    protected static ZkClient getZkClient() {
        if (zkClient == null) {
            AssertUtil.mustNotBlank(LocalCacheZkConfig.zkConnectString, "请配置zookeeper 服务器!");
            zkClient = new ZkClient(LocalCacheZkConfig.zkConnectString, Integer.MAX_VALUE, LocalCacheZkConfig.zkConnectionTimeout,
                new SerializableSerializer());
        }
        return zkClient;
    }

    private static String getResourcePath(String notifyName) {
        return new StringBuilder(LocalCacheZkConfig.rootPath).append(notifyName).toString();
    }

    public static Long getResourceNano(String notifyName) {
        String resourcePath = getResourcePath(notifyName);
        ZkClient zClient = getZkClient();
        if (zClient.exists(resourcePath)) {
            Stat stat = new Stat();
            Long nanoTime = zClient.readData(resourcePath, stat);
            return nanoTime;
        }
        return null;
    }

    /**
     * Start listener.
     * 开始向zookeeper中心监听 notifyName 对应的改变
     *
     * @param notifyName the resource name
     * @return the long
     */
    public static Long startListener(String notifyName) {
        ZkClient zClient = getZkClient();
        String resourcePath = getResourcePath(notifyName);
        String rsName = dataListenerCache.get(notifyName);
        if (rsName == null) {
            zClient.subscribeDataChanges(resourcePath, new org.stategen.framework.cache.LocalCacheUtil.ZkResourceDataListener(notifyName));
            dataListenerCache.put(notifyName, notifyName);
        }
        return getResourceNano(notifyName);
    }

    /**
     * Notify resource changed.
     * 向zookeeper注册中心广播 notifyName 对应的资源改变
     * 
     * @param notifyName the resource name
     */
    public static void notifyResourceChanged(String notifyName) {
        ZkClient zClient = getZkClient();
        Long nanoTime = System.nanoTime();
        String resourcePath = getResourcePath(notifyName);
        if (!zClient.exists(resourcePath)) {
            zClient.createPersistent(resourcePath, true);
        }
        zClient.writeData(resourcePath, nanoTime);
    }

    /**
     * 删除notifyName 对应的监听，基本没用到.
     *
     * @param notifyName the resource name
     * @return true, if successful
     */
    public static boolean deleteResourceNode(String notifyName) {
        ZkClient zClient = getZkClient();
        //删除单独一个节点，返回true表示成功  
        //        boolean e1 = zkClient.delete("/testUserNode");  
        //删除含有子节点的节点  
        String resourcePath = getResourcePath(notifyName);
        return zClient.deleteRecursive(resourcePath);
    }

}
