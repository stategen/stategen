package cn.org.rapid_framework.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** 
 * 生成器的上下文，两个功能
 * 1. 存放在context中的变量将可以在模板中直接引用
 * 2. 通过 GeneratorContext.setGeneratorProperties() 可以覆盖GeneratorProperties中的属性设置,如果为空,则不覆盖
 *  */
public class GeneratorContext {
	/** 
	 * 生成器模板的上下文,存放在context中的变量,模板可以直接引用 
	 **/
    static ThreadLocal<Map> context = new ThreadLocal<Map>();
    /** 
	 * GeneratorProperties可以引用的上下文 
	 **/
    static ThreadLocal<Properties> generatorProperties = new ThreadLocal<Properties>();
    
    public static void clear() {
        context.set(null);
        generatorProperties.set(null);
    }
    
    public static Map getContext() {
        Map map = context.get();
        if(map == null) {
            setContext(new HashMap());
        }
        return context.get();
    }
    
    public static void setContext(Map map) {
        context.set(map);
    }
    
    public static void put(String key,Object value) {
        getContext().put(key, value);
    }
    
    public static Properties getGeneratorProperties() {
    	return generatorProperties.get();
    }
    
    public static void setGeneratorProperties(Properties props) {
    	generatorProperties.set(props);
    }
}