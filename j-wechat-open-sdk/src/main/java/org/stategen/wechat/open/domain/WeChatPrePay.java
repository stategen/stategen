package org.stategen.wechat.open.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatPrePay {

    private String sign;
    private String prepayId;
    private String partnerId;
    private String appId;
    
    
    private String timeStamp;
    private String nonceStr;
    /**
     * 由于package为java保留关键字，因此改为packageValue. 前端使用时记得要更改为package
     */
    @XStreamAlias("package")
    private String packageValue;
    private String signType;
    private String paySign;
    
    private String mwebUrl;
    
    private String codeUrl;
}
