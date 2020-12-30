package org.stategen.framework.util;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Context.class);
    static Map<String, Object> VAR_MAP = new HashMap<String, Object>();
    
    public static Map<String, Integer> FACADE_SERIVCES_METHOD_COUNT =new HashMap<String, Integer>();
    
    public static void clear() {
        VAR_MAP.clear();
    }
    
    public static String put(String key ,Object value) {
        VAR_MAP.put(key, value);
        //freeMarker输出空
        return "";
    }
    
    public static Object get(String key) {
        Object object = VAR_MAP.get(key);
        if (object==null) {
            return "";
        }
        return object;
    }
    public static Object pop(String key) {
        boolean containsKey = VAR_MAP.containsKey(key);
        if (containsKey) {
            Object remove = VAR_MAP.remove(key);
            return remove;
        }
        return null;
    }
    
    
    public static boolean hasMethods(String javaFile) {
        Integer count = FACADE_SERIVCES_METHOD_COUNT.get(javaFile);
        boolean result = NumberUtil.isGreatZero(count);
        return result;
    }
    
}
