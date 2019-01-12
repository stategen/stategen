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

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.lite.ICookieType;
import org.stategen.framework.util.CollectionUtil;

/**
 * The Class AntiCookieFakeResponseWrapper.
 * 对HttpServletResponse的再次封装， 该类用来检测cookie是否被篡改，下次生成时，再次向cookie中生成令牌
 *
 * @author Xia Zhengsheng
 * @version $Id: AntiCookieFakeResponseWrapper.java, v 0.1 2017-1-3 20:40:41 Xia zhengsheng Exp $
 */
public class AntiCookieFakeResponseWrapper extends HttpServletResponseWrapper {

    final static Logger         logger            = LoggerFactory.getLogger(AntiCookieFakeResponseWrapper.class);

    public AntiCookieFakeResponseWrapper(HttpServletResponse response) {
        super(response);
        
    }

    /***
     * @see javax.servlet.http.HttpServletResponseWrapper#addCookie(javax.servlet.http.Cookie)
     */
    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
    }

    public void filterRequestCookies() {
         Map<Class<? extends ICookieType>, CookieGroup<?>> cookieGroupMap = CookieGroup.getCookieGroupMap();
        if (CollectionUtil.isEmpty(cookieGroupMap)){
            return;
        }
        
        HttpServletRequest request = ServletContextUtil.getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        if (CollectionUtil.isNotEmpty(cookies)){
            Collection<CookieGroup<?>> cookieGroups = cookieGroupMap.values();
            for (Cookie cookie : cookies) {
                for (CookieGroup<?> cookieGroup : cookieGroups) {
                    if (cookieGroup.filterRequestCookie(cookie)){
                        break;
                    }
                }
            }
        }
    }

    public boolean checkTokens() {
        Map<Class<? extends ICookieType>, CookieGroup<?>> cookieGroupMap = CookieGroup.getCookieGroupMap();
        if (CollectionUtil.isEmpty(cookieGroupMap)){
            return true;
        }
        
        Collection<CookieGroup<?>> cookieGroups = cookieGroupMap.values();
        for (CookieGroup<?> cookieGroup : cookieGroups) {
            boolean passed= cookieGroup.checkTokenIfNotWritResponse();
            if (!passed){
                return false;
            }
        }
        
        return true;
    }



}
