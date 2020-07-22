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
package org.stategen.framework.checker;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;

import org.stategen.framework.annotation.CookieCheck;
import org.stategen.framework.lite.ICookieType;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.web.cookie.CookieGroup;

/**
 * The Class DefaultCookieCheckChecker.
 */
public class DefaultCookieCheckChecker extends AbstractMethodChecker<CookieCheck> {

    @Override
    public IResponseStatus doCheck(Method method, CookieCheck anno, Class<? extends IResponseStatus> defaultResponseStatusTypeClzOfCheckFail) {
        CookieCheck cookieCheck=(CookieCheck)anno;
        Class<? extends ICookieType> cookieTypeClz =cookieCheck.cookieTypeClz();
        String cookieName = cookieCheck.cookieName();
        CookieGroup<?> cookieGroup = CookieGroup.getCookieGroup(cookieTypeClz);

        //如果相应的cookie不存在
        Cookie cookie = cookieGroup.getCookie(cookieName);
        if (cookie == null) {
            Class<? extends IResponseStatus> responseStatusClzOfCheckFail = cookieCheck.responseStatusClzOfCheckFail();
            if (responseStatusClzOfCheckFail==null){
                responseStatusClzOfCheckFail=defaultResponseStatusTypeClzOfCheckFail;
            }
            IResponseStatus responseStatus = IResponseStatus.getResponseStatus(responseStatusClzOfCheckFail);
            return responseStatus;
        }
        
        return null;
    }

    @Override
    public Class<CookieCheck> getCheckAnnoClz() {
        return CookieCheck.class;
    }


}
