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

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.InitializingBean;
import org.stategen.framework.util.AssertUtil;

/**
 * *
 * 封装本地一级缓存的类，该类用于代替代码中直接调用 LocalCacheUtil中的方法.
 *
 * @author XiaZhengsheng
 * @param <T> the generic type
 */
public class LocalCacheNameTaker<T> extends BaseLocalCacheNameTaker implements InitializingBean {
    protected String                 dataNode;

    protected ReentrantReadWriteLock rrwLock = new ReentrantReadWriteLock();

    public LocalCacheNameTaker() {
        super();
    }

    public LocalCacheNameTaker(String notifyName, String tableName, String dataNode) {
        super();
        setNotifyName(notifyName);
        setTableName(tableName);
        setDataNode(dataNode);
    }

    @SuppressWarnings("unchecked")
    public T getCache() {
        return (T) LocalCacheUtil.getCache(getNotifyName(), dataNode);
    }

    public void putToCache(T data) {
        LocalCacheUtil.putToCache(getNotifyName(), dataNode, data);
    }

    public String getDataNode() {
        return dataNode;
    }

    public void setDataNode(String dataNode) {
        this.dataNode = dataNode;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtil.mustNotEmpty(this.getTableName(), "table name must not be null");
        AssertUtil.mustNotEmpty(this.getNotifyName(), "notify name must not be null");
        AssertUtil.mustNotEmpty(dataNode, "data node name must not be null");
    }

    /***直接拿到整个对象，同一个实例不能与 public <K> T get(K key, CacheGenerator<T> generator) 同时使用*/
    public T get(CacheGenerator<T> generator) {
        T cache = null;
        //读锁锁
        rrwLock.readLock().lock();
        try {
            cache = this.getCache();
            if (cache == null) {
                //读锁解
                rrwLock.readLock().unlock();
                try {
                    rrwLock.writeLock().lock();
                    try {
                        cache = this.getCache();
                        if (cache == null) {
                            cache = generator.generateCache();
                            this.putToCache(cache);
                        }
                    } finally {
                        rrwLock.writeLock().unlock();
                    }
                } finally {
                    //读锁锁
                    rrwLock.readLock().lock();
                }
            }
        } finally {
            //读锁解
            rrwLock.readLock().unlock();
        }
        return cache;
    }

    /***根据Key拿取对象，同一个实例不能与 public T get(CacheGenerator<T> generator) 同时使用*/
    @SuppressWarnings("unchecked")
    public <K> T get(K key, CacheGenerator<T> generator) {
        //读锁锁
        rrwLock.readLock().lock();
        try {
            HashMap<K, T> cacheMap = (HashMap<K, T>) this.getCache();
            if (cacheMap == null) {
                //读锁解
                rrwLock.readLock().unlock();
                try {
                    rrwLock.writeLock().lock();
                    try {
                        cacheMap = (HashMap<K, T>) this.getCache();
                        if (cacheMap == null) {
                            cacheMap = new HashMap<K, T>();
                            this.putToCache((T) cacheMap);
                        }
                    } finally {
                        rrwLock.writeLock().unlock();
                    }
                } finally {
                    //读锁锁
                    rrwLock.readLock().lock();
                }
            }

            T partCache = cacheMap.get(key);
            if (partCache == null) {
                //读锁解
                rrwLock.readLock().unlock();
                try {
                    rrwLock.writeLock().lock();
                    if (partCache == null) {
                        try {
                            partCache = generator.generateCache();
                            cacheMap.put(key, partCache);
                        } finally {
                            rrwLock.writeLock().unlock();
                        }
                    }
                } finally {
                    //读锁锁
                    rrwLock.readLock().lock();
                }
            }
            return partCache;
        } finally {
            //读锁解
            rrwLock.readLock().unlock();
        }
    }
}
