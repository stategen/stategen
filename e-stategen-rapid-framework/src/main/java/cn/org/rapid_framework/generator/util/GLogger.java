package cn.org.rapid_framework.generator.util;

import java.io.UnsupportedEncodingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GLogger {
    
    /***windows gitbash中，gen.sh为utf-8时，.groovy正常， 由java->class文件中的中文字符串全部乱码，需经下面转换，不影响eclipse console*/
    private static String getUtf8String(String s) {
        if (s == null) {
            return null;
        }
        try {
            return new String(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return s;
        }
        
    }
    
    public static void trace(String s) {
        if (log.isTraceEnabled())
            log.trace(getUtf8String(s));
        
    }
    
    public static void debug(String s) {
        if (log.isDebugEnabled())
            log.debug(getUtf8String(s));
    }
    
    public static void debug(String s, Object o) {
        if (log.isDebugEnabled())
            log.debug(getUtf8String(s), o);
    }
    
    public static void info(String s) {
        if (log.isInfoEnabled())
            log.info(getUtf8String(s));
    }
    
    public static void warn(String s) {
        if (log.isWarnEnabled())
            log.warn(getUtf8String(s));
    }
    
    public static void warn(String s, Throwable e) {
        if (log.isWarnEnabled())
            log.warn(getUtf8String(s), e);
        
    }
    
    public static void error(String s) {
        log.error(getUtf8String(s));
    }
    
    public static void error(String s, Throwable e) {
        log.error(getUtf8String(s), e);
    }
    
}
