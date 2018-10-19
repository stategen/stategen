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

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import org.stategen.framework.util.StringComparetor;
import org.stategen.framework.util.StringUtil;

/**
 * The Class CookiesHolder.
 */
public class CookiesHolder {
    
    private Map<String, Cookie> cookieMap =new TreeMap<String, Cookie>(new StringComparetor());
    private String requestAppName =null;
    
    private Cookie tokenCookie =null;
    
    private boolean httpOnly=true;
    
    
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    public String getRequestAppName() {
        return requestAppName;
    }
    
    public Map<String, Cookie> getCookieMap() {
        return cookieMap;
    }
    
    public Cookie getValiCookie(String wholeCookieName){
        return cookieMap.get(wholeCookieName);
    }
    
    public void setTokenCookie(Cookie tokenCookie) {
        this.tokenCookie = tokenCookie;
    }
    
    public void addRequestCookie(Cookie cookie){
        this.cookieMap.put(cookie.getName(), cookie);
    }
    
    public Cookie getTokenCookie() {
        return tokenCookie;
    }
    
    public Cookie appendNormalCookie(String wholeCookieName,Object value, Integer secondsOfCookieAge){
        Cookie cookie = this.appendCookie(wholeCookieName, value,secondsOfCookieAge);
        this.cookieMap.put(wholeCookieName, cookie);
        return cookie;
    }
    
    public Cookie appendTokenCookie(String wholeCookieName, String calcuToken, Integer secondsOfCookieAge){
        tokenCookie= appendCookie(wholeCookieName,calcuToken,secondsOfCookieAge);
        return tokenCookie;
    }

    private Cookie appendCookie(String wholeCookieName ,Object value, Integer secondsOfCookieAge) {
        Cookie cookie =null;
        if (value==null || (value instanceof String  && (StringUtil.isBlank((String)value)))){
            cookie = CookieUtil.createCookie(wholeCookieName, null, CookieUtil.COOKIE_DELETE);
        } else {
            cookie= CookieUtil.createCookie(wholeCookieName, value, secondsOfCookieAge);
        }
        cookie.setHttpOnly(httpOnly);
        CookieUtil.addCookie(cookie);
        return cookie;
    }

    public String readAndDecodeCookieValue(String wholeCookieName) {
        return CookieUtil.getCookieDecodeValue(wholeCookieName);
    }

}
