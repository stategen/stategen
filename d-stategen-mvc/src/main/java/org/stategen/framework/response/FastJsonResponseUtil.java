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
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.stategen.framework.web.cookie.ServletContextUtil;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * The Class FastJsonResponseUtil.
 */
public class FastJsonResponseUtil {
    final static org.slf4j.Logger        logger = org.slf4j.LoggerFactory.getLogger(FastJsonResponseUtil.class);

    /***FastJsonHttpMessageConverter 的 afterProperties时赋值*/
    public static FastJsonHttpMessageConverter FASTJSON_HTTP_MESSAGE_CONVERTOR;

    @SuppressWarnings("resource")
    public static void writeResponse(Object result) {
        HttpServletResponse httpServletResponse = ServletContextUtil.getHttpServletResponse();
        ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(httpServletResponse);
        HttpHeaders headers = servletServerHttpResponse.getHeaders();
        
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.OK.value());
        try {
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            FASTJSON_HTTP_MESSAGE_CONVERTOR.write(result, headers.getContentType(), new HttpOutputMessage() {
                @Override
                public OutputStream getBody() throws IOException {
                    return outputStream;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            });
        } catch (HttpMessageNotWritableException | IOException e) {
            logger.error("internal error", e);
        }
    }
    


}
