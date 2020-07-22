package org.stategen.framework.cachetst;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.stategen.framework.cache.LocalCacheNameTaker;
import org.stategen.framework.cache.LocalCacheNotifier;
import org.stategen.framework.cache.LocalCacheUtil;
import org.stategen.framework.cache.LocalCacheZkConfig;

public class LocalCacheTst {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LocalCacheTst.class);
    
    @Before
    public void beforeTst(){
        LocalCacheZkConfig localCacheZkConfig =new LocalCacheZkConfig();
        localCacheZkConfig.setConnectionTimeout(10000);
        localCacheZkConfig.setConnectString("localhost:2181");
        localCacheZkConfig.setRootPath("/dalgenX/resourceCache/test/");
    }
    
    @SuppressWarnings("static-access")
    @Test
    public void testLocalCache() throws InterruptedException{
        final String province ="province";
        if (logger.isInfoEnabled()) {
            logger.info(new StringBuilder("输出info信息: province:").append(province).toString());
        }
        
        LocalCacheNameTaker<List<String>> nameTaker =new LocalCacheNameTaker<>("appapi",province, "不包括省下面的等");
        List<String> provinces =new ArrayList<String>();
        provinces.add("Mercury");
        provinces.add("Venus");
        provinces.add("Earth");
        provinces.add("JavaSoft");
        provinces.add("Mars");
        provinces.add("Jupiter");
        provinces.add("Saturn");
        provinces.add("Uranus");
        provinces.add("Neptune");
        nameTaker.putToCache(provinces);
        
        System.out.println("provinces<===========>:" + nameTaker.getCache());
        
        LocalCacheNotifier localCacheNotifier =new LocalCacheNotifier("appapi",province);
        
        localCacheNotifier.notifyResourceChanged();
        
        Thread.currentThread().sleep(10000L);
        
        System.out.println("provinces<===========>:" + nameTaker.getCache());
        
        LocalCacheUtil.deleteResourceNode(nameTaker.getNotifyName());
    }

}
