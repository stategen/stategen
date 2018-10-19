/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.serialize.support.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.alibaba.dubbo.common.utils.PojoUtils;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;

/**
 * JsonObjectInput
 * 
 * @author william.liangf
 */
public class GenericFastJsonObjectInput extends FastJsonObjectInput {

    public GenericFastJsonObjectInput(InputStream in) {
        super(in);
    }

    public GenericFastJsonObjectInput(Reader reader) {
        super(reader);
    }

    protected boolean isConvertToJsonByType(Class<?> clz, Type type) {
        if (clz == null) {
            return false;
        }

        if (type == null) {
            return false;
        }

        if (clz.isEnum()) {
            return false;
        }

        if (ReflectUtils.isPrimitives(clz)) {
            return false;
        }
//
//        if (clz.isArray()) {
//            return false;
//        }
//        if (Collection.class.isAssignableFrom(clz)) {
//            return false;
//        }
//
//        if (Map.class.isAssignableFrom(clz)) {
//            return false;
//        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        Object value = null;
        if (isConvertToJsonByType(cls, type)) {
            String json = readLine();
            value = JSON.parseObject(json, type);
        } else {
            value = super.readObject(cls, type);
            return (T) value;
        }
        return (T) PojoUtils.realize(value, cls, type);
    }
    
    private static ParserConfig dubboFastjsonConfig =null;
    
    private static ParserConfig getDubboFastjsonConfigParseConfig(){
        if (dubboFastjsonConfig==null){
            ParserConfig newConfig =new ParserConfig();
            ParserConfig globalInstance = ParserConfig.getGlobalInstance();
            Field[] declaredFields = ParserConfig.class.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isAccessible()){
                    field.setAccessible(true);
                }
                try {
                    Object globalValue = field.get(globalInstance);
                    field.set(newConfig, globalValue);                        
                } catch (IllegalArgumentException e) {
                    //skip
                } catch (IllegalAccessException e) {
                    //skip
                }
            }
            //必须是AutoTypeSupport=true
            newConfig.setAutoTypeSupport(true);
            dubboFastjsonConfig =newConfig;
        }
        return dubboFastjsonConfig;
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        String json = readLine();
        if (json == null) {
            return null;
        }
        DefaultJSONParser parser = new DefaultJSONParser(json, getDubboFastjsonConfigParseConfig(), JSON.DEFAULT_PARSER_FEATURE);
        Object value = parser.parse();
        parser.handleResovleTask(value);
        parser.close();

        return value;
    }

}