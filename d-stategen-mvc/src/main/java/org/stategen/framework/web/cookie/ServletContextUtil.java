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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * *
 * ServletContext工具类，目前该类只有2个 ThreadLocal 分别装载  HttpServletRequest HttpServletResponse
 * 方便在业务代码随时拿到这2个值，而不用配在spring mvc controller的 方法上.
 *
 * @author XiaZhengsheng
 */
public class ServletContextUtil {

    /***在MultiFilter内清除或重新赋值*/
    private static ThreadLocal<HttpServletRequest>  REQUEST_LOCAL  = new ThreadLocal<HttpServletRequest>();
    
    /***在MultiFilter内清除或重新赋值*/
    private static ThreadLocal<HttpServletResponse> RESPONSE_LOCAL = new ThreadLocal<HttpServletResponse>();
    
    /***在MultiFilter内清除或重新赋值*/
    public static ThreadLocal<Map<String, Cookie>> DECODE_COOKIES_LOCAL = new ThreadLocal<Map<String, Cookie>>();
    
    
    public static ThreadLocal<Map<String/*cookieNamePrefix*/, CookiesHolder>> PREFIX_NAME_COOKIESHOLDERMAP_THREADLOCAL = new ThreadLocal<Map<String, CookiesHolder>>();
    
    
    private static volatile String requestMapping =null;
    
    public static String getRequestMapping(){
        if (requestMapping==null){
            requestMapping =RequestUtil.getRequestPath();
        }
        return requestMapping;
    }
    

    public static void setRequest(HttpServletRequest request) {
        REQUEST_LOCAL.set(request);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return REQUEST_LOCAL.get();
    }

    public static void setResponse(HttpServletResponse request) {
        RESPONSE_LOCAL.set(request);
    }

    public static HttpServletResponse getHttpServletResponse() {
        return RESPONSE_LOCAL.get();
    }
    
    public static void removeThrdLoc(){
        REQUEST_LOCAL.remove();
        RESPONSE_LOCAL.remove();
        DECODE_COOKIES_LOCAL.remove();
        PREFIX_NAME_COOKIESHOLDERMAP_THREADLOCAL.remove();
    }
    

}
