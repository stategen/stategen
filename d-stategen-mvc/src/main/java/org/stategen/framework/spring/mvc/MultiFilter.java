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
package org.stategen.framework.spring.mvc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.stategen.framework.util.NumberUtil;
import org.stategen.framework.web.cookie.AntiCookieFakeResponseWrapper;
import org.stategen.framework.web.cookie.RequestUtil;
import org.stategen.framework.web.cookie.ServletContextUtil;

import configs.Configration;

/***
 * 复合型过滤器，配置在web.xml中
 * 该过滤器用来检测cookie是否伪造 ，以及放置新的cookie令牌
 * 
 * @author XiaZhengsheng
 */
public class MultiFilter extends GenericFilterBean {

    final static Logger logger             = LoggerFactory.getLogger(MultiFilter.class);

    /**
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestMapping=null;
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            ServletContextUtil.setRequest(httpServletRequest); 
            requestMapping = RequestUtil.getRequestPath();
            
            boolean needCheck =true;
            int idx = requestMapping.lastIndexOf('.');
            if (idx>0){
                String subName =requestMapping.substring(idx);
                if (Configration.STATIC_RESOURCES.contains(subName)){
                    needCheck =false;
                }
            }
        
        
            if (needCheck){
                AntiCookieFakeResponseWrapper httpServletResponse = new AntiCookieFakeResponseWrapper((HttpServletResponse) response);
                response =httpServletResponse;
                ServletContextUtil.setResponse(httpServletResponse); 
                httpServletResponse.filterRequestCookies();
                //前后将线程池中的refMap设为null
        
                //DDOS防范
                if (NumberUtil.isGreatZero(Configration.MAX_REQUEST_PER_IP_SECOND)) {
                    //该实现可以减少恶意的大批量的访问请求，暂时没实现该功能
                }
        
                //cookie或令牌伪造，生成的都是json
                boolean passed =httpServletResponse.checkTokens();              
                if (!passed) {
                    if (logger.isInfoEnabled()) {
                        logger.info(new StringBuffer(requestMapping).append("被拦截").toString());
                    }
                    return;
                }              
            } 
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error(new StringBuffer().append(requestMapping).append(" 请求出错啦!").append(" \n")
                .toString(), e);
            throw e;
        } finally {
            ServletContextUtil.removeThrdLoc();
        }
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
    }

}
