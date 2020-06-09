package com.github.binarywang.wxpay.service.impl;

import org.stategen.framework.util.ThreadLocalUtil;

public class WxPayServiceCleanImpl extends WxPayServiceImpl {
    
    static {
        //注册以便线程不用时清空
        ThreadLocalUtil.registThreadLocal(wxApiData); 
    }
    

}
