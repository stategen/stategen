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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.stategen.framework.response.FastJsonResponseUtil;
import org.stategen.framework.util.AssertUtil;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.util.IOUtils;

/**
 * The Class FastJsonHttpMessageConverter.
 */
public class FastJsonHttpMessageConverter extends com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter
                                          implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        FastJsonResponseUtil.FASTJSON_HTTP_MESSAGE_CONVERTOR = this;
    }
    
    @Override
    protected boolean canRead(MediaType mediaType) {
        boolean result =super.canRead(mediaType);
        if (!result) {
            //spring 在没有 获取 mediaType 强行赋值 MediaType.APPLICATION_OCTET_STREAM_VALUE
            result =MediaType.APPLICATION_OCTET_STREAM==mediaType;
        }
        return result;
    }

    @Override
    protected void writeInternal(Object obj,
                                 HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        if (obj != null && obj instanceof String) {
            //string 直接写入string,不加双引号
            FastJsonConfig fastJsonConfig = getFastJsonConfig();

            String       text = obj.toString();
            
            //headers先获得
            HttpHeaders headers = outputMessage.getHeaders();
            OutputStream out  = outputMessage.getBody();
            out.write(text.getBytes());
            if (fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength(text.length());
            }

            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder("fastjson write plain text\n").append(text).toString());
            }
            text = null;
            return;
        }

        super.writeInternal(obj, outputMessage);

    }

    private ByteBuffer getByteBuffer(InputStream is) throws IOException {
        byte[] bytes  = new byte[1024 * 64];
        int    offset = 0;
        for (;;) {
            int readCount = is.read(bytes, offset, bytes.length - offset);
            if (readCount == -1) {
                break;
            }
            offset += readCount;
            if (offset == bytes.length) {
                byte[] newBytes = new byte[bytes.length * 3 / 2];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                bytes = newBytes;
            }
        }
        return ByteBuffer.wrap(bytes, 0, offset);
    }

    @Override
    public Object read(Type type,
                       Class<?> contextClass,
                       HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        type = getType(type, contextClass);
        if (type == String.class) {
            InputStream    is             = inputMessage.getBody();
            FastJsonConfig fastJsonConfig = getFastJsonConfig();
            Charset        charset        = fastJsonConfig.getCharset();
            charset = AssertUtil.ifNull(charset, IOUtils.UTF8);
            ByteBuffer byteBuffer = getByteBuffer(is);

            String result = new String(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit(), charset);
            if (logger.isDebugEnabled()) {
                logger.debug(new StringBuilder("fastjson read plain text:\n").append(result).toString());
            }
            return result;
        }
        return super.read(type, contextClass, inputMessage);
    }

}
