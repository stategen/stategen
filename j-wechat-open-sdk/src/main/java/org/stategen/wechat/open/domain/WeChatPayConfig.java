package org.stategen.wechat.open.domain;

import com.github.binarywang.wxpay.config.WxPayConfig;

import lombok.Data;

@Data
public class WeChatPayConfig  extends WxPayConfig{
    
    private String refundNotifyUrl;

}
