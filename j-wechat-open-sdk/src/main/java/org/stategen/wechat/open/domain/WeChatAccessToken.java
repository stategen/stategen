package org.stategen.wechat.open.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatAccessToken {
    //    "access_token": "ACCESS_TOKEN",
    private String access_token;

    //    "expires_in": 7200,
    private Integer expires_in;
    
    //    "refresh_token": "REFRESH_TOKEN",
    private String refresh_token;

    //    "openid": "OPENID",
    private String openid;

    //    "scope": "SCOPE",
    private String scope;

    //    "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
    private String unionid;

    //    "errcode":40029,
    private Integer errcode;

    //    "errmsg":"invalid code";
    private String errmsg;

}


