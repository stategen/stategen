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
package org.stategen.framework.spring.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.stategen.framework.util.StringUtil;
import org.stategen.framework.web.cookie.ServletContextUtil;

/**
 * The Class RequestUtil.
 * http request请求工具类
 *
 * @author Xia Zhengsheng
 * @version $Id: RequestUtil.java, v 0.1 2017-1-3 21:10:13 Xia zhengsheng Exp $
 */
public class RequestUtil {
    
    
    /**
     *  <pre>   The Constant logger.  </pre>
     */
    final static Logger logger = LoggerFactory.getLogger(RequestUtil.class);
    
    /**
     * Gets the requst uri no app name.
     * <pre>获得不含有应用的路径，
     * 如 http://localhost:8080/api/order/getOrder?id=123----》/order/getOrder
     * </pre>
     * @param request the request
     * @return the requst uri no app name
     */

    
    public static String getRequestPath(){
        HttpServletRequest request =ServletContextUtil.getHttpServletRequest();
        return request.getServletPath();
    }
    
    /**
     * Gets the reqest uri.
     * <pre>获得应用名,
     * 如 http://localhost:8080/api/order/getOrder?id=123----》/api/
     * </pre>
     *
     * @param request the request
     * @return the reqest uri
     */
    public static String getRequestAppName(){
        HttpServletRequest request =ServletContextUtil.getHttpServletRequest();
       return request.getContextPath()+"/";
    }
    
    /**
     * Gets the ip addr.
     * 获得用户IP,copy from convertUtil,改写为直接比较大写
     * @param request the request
     * @return the ip addr
     */
    public static String getIpAddr() {
        HttpServletRequest request =ServletContextUtil.getHttpServletRequest();
        //不再比较大小写，基础方法应快速执行
        final String UNKNOWN = "UNKNOWN";
        String ipAdd = request.getHeader("x-forwarded-for");

        if (StringUtil.isBlank(ipAdd) || UNKNOWN.equals(StringUtil.toUpperCase(ipAdd))) {
            ipAdd = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtil.isBlank(ipAdd) || UNKNOWN.equals(StringUtil.toUpperCase(ipAdd))) {
            ipAdd = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtil.isBlank(ipAdd) || UNKNOWN.equals(StringUtil.toUpperCase(ipAdd))) {
            ipAdd = request.getRemoteAddr();
        }
        return ipAdd;
    }



    /**
     * Gets the parameter.
     *
     * @param request the http servlet request
     * @param name the name
     * @return the parameter
     */
    public static String getParameter(HttpServletRequest request, String name) {
        if (request != null && StringUtil.isNotBlank(name)) {
            return request.getParameter(name);
        }
        return null;
    }

    /**
     * <pre>需要在web.xml内配置 配置
     *     &lt;listener&gt;
             &lt;listener-class&gt;
             org.springframework.web.context.request.RequestContextListener
             &lt;/listener-class&gt;
             &lt;/listener&gt;
     * </pre>
     * @return
     */
    public static HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return  request;
    }
    
    public static Locale getLocale(){
        return (Locale) getRequest().getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

}
