/*
 * Copyright (C) 2018 niaoge<78493244@qq.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.spring.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import javax.org.stategen.framework.lite.BaseBusinessException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.stategen.framework.lite.BaseResponse;
import org.stategen.framework.lite.HandleError;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.response.ResponseStatusTypeHandler;
import org.stategen.framework.response.ResponseUtil;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.StringUtil;

/**
 * The Class CollectExceptionJsonHandler.
 */
public class CollectExceptionJsonHandler extends ResponseStatusTypeHandler implements HandlerExceptionResolver {
    
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CollectExceptionJsonHandler.class);
    
    @Override
    public ModelAndView resolveException(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Object handler,
            Exception ex) {
        String        failMessage = ex.getMessage();
        StringBuilder sb          = new StringBuilder(httpServletRequest.getRequestURI()).append(" ");
        Throwable throwable =ex;
        if (ex instanceof BaseBusinessException) {
            sb.append("业务异常：").append("\n").append(failMessage);
        } else if (ex instanceof UndeclaredThrowableException) {
            //sentinel没有配置 handleException时，被拦截
            //1.查看SentinelResource是否家配置msg
            throwable = ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
            String blockMsg = throwable.getMessage();
            if (StringUtil.isEmpty(blockMsg)) {
                //从缓存中拿,这个消息是 配置 bean SentinelBlockHandler 后才有的
                blockMsg = ResponseUtil.BLOCK_MAP.get(throwable.getClass());
            }
            
            if (StringUtil.isNotEmpty(blockMsg)) {
                failMessage =blockMsg;
            }
            
            sb.append("请求截获一个限流异常:").append("\n").append(failMessage).append(" ").append(ex.getMessage());
            
        } else {
            sb.append("请求产生了一个错误:").append("\n").append(failMessage).append(" ").append(ex.getMessage()).append(" \n");
        }
        
        logger.error(sb.toString(), ex);
        
        ModelAndView modelAndView = new ModelAndView();
        if (!(handler instanceof HandlerMethod)) {
            return modelAndView;
        }
        
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method              method        = handlerMethod.getMethod();
        
        HandleError     handleError         = AnnotationUtil.getMethodOrOwnerAnnotation(method, HandleError.class);
        IResponseStatus errorResponseStatus = this.getResponseStatus();
        
        if (handleError == null || !handleError.exclude()) {
            ResponseBody responseBodyAnno = AnnotationUtil.getMethodOrOwnerAnnotation(method, ResponseBody.class);
            if (responseBodyAnno != null) {
                BaseResponse<?> errorResponse = ResponseUtil.buildResponse(null, errorResponseStatus);
                errorResponse.setExeptionClass(throwable.getClass().getSimpleName());
                errorResponse.setMessage(failMessage);
                httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                boolean supportMethod = ResponseBodyAdviceWrapper.supportMethod(method);
                ResponseUtil.writhResponse(supportMethod, errorResponse);
                
            } else {
                String redirect = errorResponseStatus.isRedirect() ? "redirect:" : null;
                String viewName = StringUtil.concatNoNull(redirect, errorResponseStatus.getErrorPage());
                modelAndView.setViewName(viewName);
            }
        }
        return modelAndView;
    }
    
    public <T extends Enum<T> & IResponseStatus> void setResponseStatusOfException(T responseStatusOfException) {
        super.setResponseStatus(responseStatusOfException);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
    
}