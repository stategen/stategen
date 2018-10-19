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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.InitializingBean;
import org.stategen.framework.cache.LocalCacheNameTaker;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringComparetor;
import org.stategen.framework.util.StringUtil;

/**
 * *
 * 抽象的国际化资原提供器，分布式资原提供器,
 * 继承它的类需实现 getTranslateBeans 的方法.
 *
 * @author XiaZhengsheng
 */
public abstract class AbstractBundleProvider implements BundleProvider,InitializingBean {
    final static org.slf4j.Logger                                      logger                  = org.slf4j.LoggerFactory.getLogger(AbstractBundleProvider.class);

    private String                                                     tableName             = null;

    private String                                                     notifyName            = null;

    private final String                                               bundleName              = "resource_bundle";

    private LocalCacheNameTaker<Map<Object, SignedResourceBundle>> bundleTaker             = null;

    private  Lock                                                       resourceBundleLock      =  new ReentrantLock();

//    private AtomicBoolean bundleCreateState =new AtomicBoolean(false);
    private Boolean                                                    useCodeAsDefaultMessage = null;
    
    private Object defaultIndex ;
    
//    private static Set<String> notifyNames =new HashSet<String>();

    public Map<Object, SignedResourceBundle> createDistributeResourceBundleMap() {
        
        Map<Object, SignedResourceBundle> result = new HashMap<Object, SignedResourceBundle>();
        List<ConfigBean> configBeans = getConfigBeans(notifyName,tableName);
        if (CollectionUtil.isEmpty(configBeans)) {
            return result;
        }
        
        Map<String /*index*/, List<ConfigBean>> configBeanMap = new HashMap<String, List<ConfigBean>>();
        for (ConfigBean configBean : configBeans) {
            String index = configBean.getIndex();
            
            List<ConfigBean> subConfigBeans = configBeanMap.get(index);
            if (subConfigBeans == null) {
                subConfigBeans = new ArrayList<ConfigBean>();
                configBeanMap.put(index, subConfigBeans);
            }
            subConfigBeans.add(configBean);
        }

        if (CollectionUtil.isEmpty(configBeanMap)) {
            return result;
        }

        for (Entry<String, List<ConfigBean>> configEntry : configBeanMap.entrySet()) {
            String indexString=configEntry.getKey(); 
            Object index =stringToIndex(indexString); 

            List<ConfigBean> subConfigBeans = configEntry.getValue();
            if (CollectionUtil.isEmpty(subConfigBeans)) {
                continue;
            }

            Map<String, String> sortedBundleMap = new TreeMap<String, String>(new StringComparetor());
            for (ConfigBean configBean : subConfigBeans) {
                sortedBundleMap.put(configBean.getKey(), configBean.getValue());
            }

            boolean calcUseCodeAsDefaultMessage = useCodeAsDefaultMessage != null ? useCodeAsDefaultMessage : false;
            SignedResourceBundle bundle = new SignedResourceBundle(calcUseCodeAsDefaultMessage,index);
            putSortedMapToBundle(sortedBundleMap, bundle);
            result.put(index, bundle);
        }
        return result;
    }

    public static void putSortedMapToBundle(Map<String, String> sortedMap, SignedResourceBundle bundle) {
        int hash = 1;
        for (Entry<String, String> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            hash = 31 * hash + key.hashCode();
            hash = 31 * hash + value.hashCode();
            bundle.put(key, value);
        }
        bundle.setSign(hash);
    }

    public SignedResourceBundle getDistributeResourceBundle(Object locale) {
        Map<Object, SignedResourceBundle> resourceBundleMap = getResourceBundleMap();
        SignedResourceBundle signedResourceBundle = resourceBundleMap.get(locale);
        return signedResourceBundle;
    }

    public Map<Object, SignedResourceBundle> getResourceBundleMap() {
        Map<Object, SignedResourceBundle> resourceBundleMap = bundleTaker.getCache();
        if (resourceBundleMap == null) {
            resourceBundleLock.lock();
            try {
                //再获得一次,如果有其它线程设置了这个值，就不用再创建了,通常上面有锁可以保证第一个线程一定能够获得这个值
                resourceBundleMap = bundleTaker.getCache();
                if (resourceBundleMap == null) {
                    resourceBundleMap = createDistributeResourceBundleMap();
                    bundleTaker.putToCache(resourceBundleMap);
                } 
            } finally {
                resourceBundleLock.unlock();
            }
        }
        return resourceBundleMap;
    }
    
    protected SignedResourceBundle getSignedResourceBundleIfNullDefault(Locale locale){
        SignedResourceBundle signedResourceBundle = getDistributeResourceBundle(locale);
        if (signedResourceBundle==null){
            if (this.defaultIndex!=null){
                signedResourceBundle=getDistributeResourceBundle(defaultIndex);
            }
        }
        return signedResourceBundle;
    }

    @Override
    public String resolveCodeWithoutArguments(String code, Locale locale) {
        SignedResourceBundle signedResourceBundle =getSignedResourceBundleIfNullDefault(locale);
        
        if (signedResourceBundle != null) {
            return signedResourceBundle.getString(code);
        }
        return null;
    }
    
    public void createBundleTaker(){
        if (notifyName!=null && tableName!=null){
            bundleTaker = new LocalCacheNameTaker<Map<Object, SignedResourceBundle>>(notifyName,tableName,bundleName);
        }
    }
    
    public Map<String,String> getBundle(Object index){
        Map<String,String> result =null;
        SignedResourceBundle signedResourceBundle = getDistributeResourceBundle(index);
        if (signedResourceBundle != null) {
            result= signedResourceBundle.getLookUp();
        }
        
        if (result==null){
            result =new HashMap<String, String>(0);
        }
        
        return result;
    }
    
    @Override
    public Integer getBundleSign(Object index){
        Integer result =null;
        SignedResourceBundle signedResourceBundle = getDistributeResourceBundle(index);
        if (signedResourceBundle != null) {
            result= signedResourceBundle.getSign();
        }
        
        if (result==null){
            result =1;  
        }
        return result;
    }

    @Override
    public MessageFormat resolveCode(String code, Locale locale) {
        SignedResourceBundle signedResourceBundle =getSignedResourceBundleIfNullDefault(locale);
        if (signedResourceBundle != null) {
            return signedResourceBundle.getFormat(code, locale);
        }
        return null;
    }

    public void setNotifyName(String notifyName) {
        this.notifyName = notifyName;
        createBundleTaker();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        createBundleTaker();
    }

    @Override
    public void setUseCodeAsDefaultMessage(Boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }

    @Override
    public Boolean getUseCodeAsDefaultMessage() {
        return useCodeAsDefaultMessage;
    }
    
    public void setDefaultIndex(String defaultIndex) {
        if (StringUtil.isNotEmpty(defaultIndex)){
            this.defaultIndex=stringToIndex(defaultIndex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtil.mustNotBlank(notifyName, "notifyName can not be empty!");
        AssertUtil.mustNotBlank(tableName, "tableName can not be empty!");
    }

    @Override
    public List<ConfigBean> getConfigBeans(String notifyName, String tableName) {
        return null;
    }

    @Override
    public Object stringToIndex(String indexString) {
        return null;
    }
}
