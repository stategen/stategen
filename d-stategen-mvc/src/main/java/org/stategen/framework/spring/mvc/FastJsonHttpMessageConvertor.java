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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.stategen.framework.response.FastJsonResponseUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * The Class FastJsonHttpMessageConverter.
 */
public class FastJsonHttpMessageConvertor extends FastJsonHttpMessageConverter implements InitializingBean{

    
    @Override
    public void afterPropertiesSet() throws Exception {
        FastJsonResponseUtil.FASTJSON_HTTP_MESSAGE_CONVERTOR =this;
    }
    
    
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();

        boolean writeAsToString = false;
        if (obj != null) {
            String className = obj.getClass().getName();
            //string 直接写入string,不加双引号
            if (obj instanceof String) {
                writeAsToString = true;
            }
            else if ("com.fasterxml.jackson.databind.node.ObjectNode".equals(className)) {
                writeAsToString = true;
            }
        }
        
        FastJsonConfig fastJsonConfig = getFastJsonConfig();

        if (writeAsToString) {
            String text = obj.toString();
            OutputStream out = outputMessage.getBody();
            out.write(text.getBytes());
            if (fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength(text.length());
            }
        } else {
            int len = JSON.writeJSONString(outnew, //
                    fastJsonConfig.getCharset(), //
                    obj, //
                    fastJsonConfig.getSerializeConfig(), //
                    fastJsonConfig.getSerializeFilters(), //
                    fastJsonConfig.getDateFormat(), //
                    JSON.DEFAULT_GENERATE_FEATURE, //
                    fastJsonConfig.getSerializerFeatures());
            if (fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength(len);
            }

            OutputStream out = outputMessage.getBody();
            outnew.writeTo(out);
        }


        outnew.close();
    }

}
