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
package org.stategen.framework.response;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stategen.framework.lite.BaseResponse;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.spring.mvc.SpringContextHolder;
import org.stategen.framework.web.cookie.ServletContextUtil;

import configs.Constant;

/**
 * The Class ResponseUtil.
 */
public class ResponseUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResponseUtil.class);

    @SuppressWarnings("unchecked")
    public static <T> BaseResponse<T> buildResponse(T data, IResponseStatus responseStatus) {
        BaseResponse<T> resultResponse = null;
        if (data == null) {
            resultResponse = SpringContextHolder.getBean(Constant.RESPONSE_NAME);
            resultResponse.setStatus(responseStatus);
        } else {
            Class<?> resultClz = data.getClass();
            if (!BaseResponse.class.isAssignableFrom(resultClz)) {
                resultResponse = SpringContextHolder.getBean(Constant.RESPONSE_NAME);
                resultResponse.setData(data);
                resultResponse.setStatus(responseStatus);
            } else {
                resultResponse = (BaseResponse<T>) data;
            }
        }
        return resultResponse;
    }

    public static void writeResponsePage(IResponseStatus responseStatus) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = ServletContextUtil.getHttpServletResponse();
        if (responseStatus.isRedirect()) {
            httpServletResponse.sendRedirect(responseStatus.getErrorPage());
        } else {
            HttpServletRequest httpServletRequest = ServletContextUtil.getHttpServletRequest();
            httpServletRequest.getRequestDispatcher(responseStatus.getErrorPage()).forward(httpServletRequest, httpServletResponse);
        }
    }
    
    public static <T> void writhResponse(Boolean supportMethod,T data, IResponseStatus responseStatus) {
        BaseResponse<Object> response = ResponseUtil.buildResponse(data, responseStatus);
        writhResponse(supportMethod,response);
    }
    
    public static <T> void writhResponse(Boolean supportMethod,BaseResponse<T> response) {
        if (supportMethod){
            FastJsonResponseUtil.writeResponse(response);
        } else {
            if (response._getData()!=null){
                FastJsonResponseUtil.writeResponse(response._getData());
            } else {
                FastJsonResponseUtil.writeResponse(response._getStatus());
            }
        }
    }

    //    public static Object getFailResponse(Method method,  Class<? extends Annotation> annotationType , boolean redirect, String onFailResouce, String onFailMessage, Exception e) {
    //        FailResponse failResponse = Configration.getFailResonse(annotationType);
    //        if (failResponse != null) {
    //            ResponseBody responseBody = AnnotationUtil.getAnnotation(method, ResponseBody.class);
    //            if (responseBody == null) {
    //                if (StringUtil.isEmpty(onFailResouce)) {
    //                    onFailResouce = Constant.LOGIN_HTM;
    //                }
    //
    //                if (redirect) {
    //                    onFailResouce = new StringBuilder(Constant.REDIRECT).append("/").append(onFailResouce).toString();
    //                }
    //
    //                if (logger.isInfoEnabled()) {
    //                    logger.info(new StringBuilder("输出info信息: onFail:").append(onFailResouce).toString());
    //                }
    //                return onFailResouce;
    //            } else {
    //                if (logger.isInfoEnabled()) {
    //                    logger.info(new StringBuilder("输出拦截信息: json:").append(onFailMessage).toString());
    //                }
    //                
    //                Object failMessage = failResponse.getFailMessage(method, annotationType, onFailMessage,e);
    //                return failMessage;
    //            }
    //        } else {
    //            String failMessage = new StringBuilder("failResponse").append(annotationType).append(" not defined!").toString();
    //            logger.warn(new StringBuilder("输出warn信息:failMessage:").append(failResponse).toString());
    //
    //            return BaseResponse.error(failMessage);
    //        }
    //    }
}
