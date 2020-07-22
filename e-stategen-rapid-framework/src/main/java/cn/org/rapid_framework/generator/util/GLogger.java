package cn.org.rapid_framework.generator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import cn.org.rapid_framework.generator.GeneratorProperties;

import lombok.Cleanup;

public class GLogger {
	public static final int TRACE = 60;
	public static final int DEBUG = 70;
	public static final int INFO = 80;
	public static final int WARN = 90;
	public static final int ERROR = 100;

	public static int logLevel = INFO;
	final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GLogger.class);
	
//	public static PrintStream out = System.out;
//	public static PrintStream err = System.err;

	public static void trace(String s) {
	    logger.trace(s);
	    
//		if (logLevel <= TRACE)
//			out.println("[Generator TRACE] " + s);
	}
	
	public static void debug(String s) {
	    logger.debug(s);
//		if (logLevel <= DEBUG)
//			out.println("[Generator DEBUG] " + s);
	}

	public static void info(String s) {
	    logger.info(s);
//		if (logLevel <= INFO)
//			out.println("[Generator INFO] " + s);
	}

	public static void warn(String s) {
	    logger.warn(s);
//		if (logLevel <= WARN)
//			err.println("[Generator WARN] " + s);
	}

	public static void warn(String s, Throwable e) {
	    logger.warn(s,e);
	    
//		if (logLevel <= WARN) {
//			err.println("[Generator WARN] " + s + " cause:"+e);
//			e.printStackTrace(err);
//		}
	}

	public static void error(String s) {
	    logger.error(s);
//		if (logLevel <= ERROR)
//			err.println("[Generator ERROR] " + s );
	}

	public static void error(String s, Throwable e) {
	    logger.error(s,e);
//		if (logLevel <= ERROR) {
//			err.println("[Generator ERROR] " + s + " cause:"+e);
//			e.printStackTrace(err);
//		}
	}
	
	public static int perfLogLevel = TRACE;
	
    public static void perf(String s) {
        
        if(perfLogLevel <= INFO) {
           logger.trace(s);
//            out.println("[Generator Performance] " + "() " + s);
////            new Throwable().printStackTrace(out); //print call trace
        }
    }
	
	public static void println(String s) {
		if (logLevel <= INFO) {
		    logger.info(s);
		}
	}
	
	static {
	    init_with_log4j_config();
	}
	
	public static void init_with_log4j_config() {
	    Properties props = loadLog4jProperties();
	    logLevel = toLogLevel(props.getProperty("cn.org.rapid_framework.generator.util.GLogger","INFO"));
	    perfLogLevel = toLogLevel(props.getProperty("cn.org.rapid_framework.generator.util.GLogger.perf","ERROR"));
	}
	
	public static int toLogLevel(String level) {
        if("TRACE".equalsIgnoreCase(level)) {
            return TRACE;
        }
        if("DEBUG".equalsIgnoreCase(level)) {
            return DEBUG;
        }	    
	    if("INFO".equalsIgnoreCase(level)) {
	        return INFO;
	    }
	    if("WARN".equalsIgnoreCase(level)) {
            return WARN;
        }
	    if("ERROR".equalsIgnoreCase(level)) {
            return ERROR;
        }
	    if("FATAL".equalsIgnoreCase(level)) {
            return ERROR;
        }
	    if("ALL".equalsIgnoreCase(level)) {
            return ERROR;
        }
	    return WARN;
	}
	
    public static void debug(String string, Map<String, Object> templateModel) {
        if (logLevel <= DEBUG) {
            GLogger.println(string);
            if(templateModel == null) return;
            for(Object key : templateModel.keySet()) {
                if(System.getProperties().containsKey(key) || GeneratorProperties.getProperties().containsKey(key)) {
                    continue;
                }
                if(key.toString().endsWith("_dir")) {
                    continue;
                }
                GLogger.println(key+" = " + templateModel.get(key));
            }
        }
    }

    private static Properties loadLog4jProperties()  {
        try {
            File file = FileHelper.getFileByClassLoader("log4j.properties");
    	    Properties props = new Properties();
    	    @Cleanup
    	    FileInputStream in = new FileInputStream(file);
    	    props.load(in);
    	    return props;
        }catch(FileNotFoundException e) {
            GLogger.warn("not found log4j.properties, cause:"+e);
            return new Properties();
        }catch(IOException e) {
            GLogger.warn("load log4j.properties occer error, cause:"+e);
            return new Properties();
        }
    }
    
}
