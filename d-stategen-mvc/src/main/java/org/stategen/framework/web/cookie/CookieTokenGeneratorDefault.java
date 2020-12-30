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
package org.stategen.framework.web.cookie;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.InitializingBean;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.MD5Util;
import org.stategen.framework.util.NumberUtil;
import org.stategen.framework.util.StringUtil;

import configs.Configration;

/**
 * **
 * cookie令牌生成校验接口的默认实现类，.
 *
 * @author XiaZhengsheng
 */
public class CookieTokenGeneratorDefault implements CookieTokenGenerator ,InitializingBean {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CookieTokenGeneratorDefault.class);

    /***
     * 根据配置的混淆码，以及cookie排序后的值，算出令牌
     * @see cn.org.rapid_framework.http.CookieTokenGenerator#genToken(cn.org.rapid_framework.web.AntiCookieFakeResponseWrapper)
     */
    @Override
    public String genToken(Map<String, Cookie> reuestCookieMap) {
        if (CollectionUtil.isNotEmpty(reuestCookieMap)){
            int filteredCount =0;
            StringBuilder sb = new StringBuilder(512);
            for (Cookie cookie : reuestCookieMap.values()) {
                int cookieMaxAge = cookie.getMaxAge();
                if (NumberUtil.isNullOrZero(cookieMaxAge)) {
                    continue;
                }
                filteredCount++;
                String cookieValue = cookie.getValue();
                sb.append(cookieValue);
            }
            
            if (NumberUtil.isGreatZero(filteredCount)){
                sb.append(Configration.COOKIE_TOKEN_MIX);
                String tokenValue = MD5Util.md5(sb.toString());
                return tokenValue;
            } 
        }

        //没有带令牌，由到method方法根绝CookieCheck看是否请求合
        return null;
    }

    public void setLoginLogoutPaths(String logInOutPaths) {
        Configration.LOGIN_OUT_PATHS.clear();
        List<String> cookieExcepts = StringUtil.parserToList(logInOutPaths);
        if (CollectionUtil.isNotEmpty(cookieExcepts)) {
            Configration.LOGIN_OUT_PATHS.addAll(cookieExcepts);
        }
    }

    public void setTokenMix(String tokenMix) {
        if (StringUtil.isNotBlank(tokenMix)) {
            Configration.COOKIE_TOKEN_MIX = tokenMix;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Configration.TOKEN_GENERATOR = this;
    }



}
