package org.stategen.wechat.open.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;

import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CopyUtil;
import org.stategen.framework.util.HttpsUtil;
import org.stategen.framework.util.NumberUtil;
import org.stategen.wechat.open.domain.WeChatAccessToken;
import org.stategen.wechat.open.domain.WeChatAppUser;

import com.alibaba.fastjson.JSON;

public interface WeChatOpenService {

    String getSecret(String appid);

    //  final String CHECK_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/auth?access_token=${0}&openid={1}";

    default WeChatAccessToken getWxAccessToken(String appid, String code) {
        AssertUtil.mustNotBlank(appid, "appid is blank or null");
        AssertUtil.mustNotBlank(code, "code is blank or null");
        String secret = getSecret(appid);
        AssertUtil.mustNotBlank(secret, "secret is blank or null");

        final String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";

        String accessTokenUrl = MessageFormat.format(ACCESS_TOKEN, appid, secret, code);
        String doGetStr = HttpsUtil.doGet(accessTokenUrl);
        WeChatAccessToken result = JSON.parseObject(doGetStr, WeChatAccessToken.class);
        return result;
    }

    default WeChatAppUser interalgetWeChatUser(WeChatAccessToken weChatAccessToken) {
        final String USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}";
        String url = MessageFormat.format(USERINFO, weChatAccessToken.getAccess_token(), weChatAccessToken.getOpenid());
        String doGetStr = HttpsUtil.doGet(url);
        WeChatAppUser weChatAppUser = JSON.parseObject(doGetStr, WeChatAppUser.class);
        return weChatAppUser;
    }

    default WeChatAccessToken refreshAccessToken(WeChatAccessToken weChatAccessToken, String appid) {
        final String REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid={0}&grant_type=refresh_token&refresh_token={1}";

        String url = MessageFormat.format(REFRESH_ACCESS_TOKEN, appid, weChatAccessToken.getRefresh_token());
        String doGetStr = HttpsUtil.doGet(url);
        WeChatAccessToken result = JSON.parseObject(doGetStr, WeChatAccessToken.class);
        return result;
    }

    /***
     * 用户数量是不受限制的，因些不能将用户 ACCESS_TOKEN放置于redis，应该将这些信息存于客户端
     * 
     * @param weChatAccessToken
     * @return
     * @throws GeneralSecurityException 
     * @throws IOException 
     */
    default WeChatAppUser getWeChatUser(WeChatAccessToken weChatAccessToken, String appid) {
        WeChatAppUser weChatAppUser = interalgetWeChatUser(weChatAccessToken);
        if (!NumberUtil.isNullOrZero(weChatAppUser.getErrcode())) {
            weChatAccessToken = refreshAccessToken(weChatAccessToken, appid);
            weChatAppUser = interalgetWeChatUser(weChatAccessToken);
        }

        AssertUtil.mustTrue(NumberUtil.isNullOrZero(weChatAppUser.getErrcode()), weChatAppUser.getErrmsg());
        CopyUtil.merge(weChatAccessToken, weChatAppUser);
        CopyUtil.merge(weChatAppUser, weChatAccessToken);
        return weChatAppUser;
    }
    
    default WeChatAppUser getWeChatUser(String appid, String code)  {
        WeChatAccessToken weChatAccessToken = this.getWxAccessToken(appid, code);
        return getWeChatUser(weChatAccessToken, appid);
    }

}
