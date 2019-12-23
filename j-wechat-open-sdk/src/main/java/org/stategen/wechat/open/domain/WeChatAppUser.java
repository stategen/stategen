package org.stategen.wechat.open.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatAppUser extends WeChatAccessToken {
    //  "openid": "OPENID",
    //  "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"

    //    "nickname": "NICKNAME",
    String nickname;
    
    //    "sex": 1,
    Integer sex;

    //    "province": "PROVINCE",
    String province;

    //    "city": "CITY",
    String city;

    //    "country": "COUNTRY",
    String country;
    
    //    "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
    String headimgurl;

    //    "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
    String privilege;

}
