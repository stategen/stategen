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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.spring.util.RequestUtil;
import org.stategen.framework.util.CaseInsensitiveHashMap;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.GetOrCreateWrap;
import org.stategen.framework.util.StringUtil;

import configs.Configration;

/**
 * The Class CookieUtil.
 * http cookie工具类，
 * @author Xia Zhengsheng
 * @version $Id: CookieUtil.java, v 0.1 2017-1-3 21:00:58 Xia zhengsheng Exp $
 */
public class CookieUtil {
    final static Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    /**
     *  <pre>   The Constant COOKIE_NEVER_EXPIRE.  </pre>
     */
    public static final int COOKIE_IN_MEMERY = -1;

    /**
     *  <pre>   The Constant COOKIE_DELETE.  </pre>
     */
    public static final int COOKIE_DELETE = 0;

    /**
     * 仅仅get,不解码,在cookie 令牌校验时，可以提高速度
     *
     * @param httpServletRequest the http servlet request
     * @param name the name
     * @return the cookie value
     */
    public static String getCookieRawValue(String name) {
        if (StringUtil.isBlank(name)) {
            return null;
        }
        
        GetOrCreateWrap<Map<String, Cookie>> getOrCreateWrap = CollectionUtil.getOrCreateMapFormThreadLocal(ServletContextUtil.DECODE_COOKIES_LOCAL,
            CaseInsensitiveHashMap.class);
        Map<String, Cookie> cookieNameCookieMap = getOrCreateWrap.getValue();
        if (getOrCreateWrap.isNew()) {
            HttpServletRequest request = ServletContextUtil.getHttpServletRequest();
            if (request != null) {
                Cookie[] cookies = request.getCookies();
                if (CollectionUtil.isNotEmpty(cookies)) {
                    for (Cookie cookie : cookies) {
                        cookieNameCookieMap.put(cookie.getName(), cookie);
                    }
                }
            }
        }

        Cookie cookie = cookieNameCookieMap.get(name);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 读后同时解码
     * 
     * @param name
     * @return
     */
    public static String getCookieDecodeValue(String name) {
        String cookieValue = getCookieRawValue(name);
        if (StringUtil.isNotEmpty(cookieValue)) {
            try {
                cookieValue = URLDecoder.decode(cookieValue, configs.Constant.defaultCharset);
            } catch (UnsupportedEncodingException e) {
                //skip
            }
        }
        return cookieValue;
    }


    public static Cookie createCookie(String path, String name, Object value, int maxAge) {
        Cookie cookie = processCookie(name, value, maxAge);
        cookie.setPath(path);
        return cookie;
    }

    private static Cookie processCookie(String name, Object value, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        if (value != null) {
            String encodeValue = null;
            try {
                encodeValue = URLEncoder.encode(value.toString(), configs.Constant.defaultCharset);
            } catch (UnsupportedEncodingException e) {
                //skip
            }
            if (StringUtil.isNotEmpty(encodeValue)) {
                cookie.setValue(encodeValue);
            }
        }
        cookie.setMaxAge(maxAge);
        return cookie;
    }


    public static Cookie createCookie(String name, Object value, Integer maxAge) {
        String requestAppName = RequestUtil.getRequestAppName();
        Cookie cookie = createCookie(requestAppName, name, value, maxAge != null ? maxAge : Configration.COOKIE_DEFAULT_AGE);
        return cookie;
    }

    /****
     * cookie的httpOnly属性在Response.addCookie(cookie)后设置无效，所有另外调用
     * 
     * @param cookie
     */
    public static void addCookie(Cookie cookie) {
        HttpServletResponse httpServletResponse = ServletContextUtil.getHttpServletResponse();
        httpServletResponse.addCookie(cookie);
    }
    
    public static void deleteCookie(String cookieName){
        Cookie cookie = createCookie(cookieName, null, CookieUtil.COOKIE_DELETE);
        addCookie(cookie);
    }

}
