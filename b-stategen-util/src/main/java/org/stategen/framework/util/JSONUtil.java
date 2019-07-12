package org.stategen.framework.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    public static String readAll(Reader reader) {
        StringBuilder buf = new StringBuilder();

        try {
            char[] chars = new char[2048];
            for (;;) {
                int len = reader.read(chars, 0, chars.length);
                if (len < 0) {
                    break;
                }
                buf.append(chars, 0, len);
            }
        } catch(Exception ex) {
            throw new JSONException("read string from reader error", ex);
        }

        return buf.toString();
    }
    
    public static JSONObject loadJson(String resourcePath) throws IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(resourcePath), StringUtil.UTF_8);
        String jsonString =readAll(reader);
        JSONObject dataJson =JSONObject.parseObject(jsonString);
        return dataJson;
    }
    
    public static String toJSONString(Object object){
        return JSON.toJSONString(object);
    }
}
