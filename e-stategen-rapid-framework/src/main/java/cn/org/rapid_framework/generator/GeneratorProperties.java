package cn.org.rapid_framework.generator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.PropertiesHelper;
import cn.org.rapid_framework.generator.util.typemapping.DatabaseTypeUtils;


/**
 * 生成器配置类
 * 用于装载generator.properties,generator.xml文件
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class GeneratorProperties {
	
	static final String PROPERTIES_FILE_NAMES[] = new String[]{"generator.properties","generator.xml"};
	
	static PropertiesHelper propertiesHelper = new PropertiesHelper(new Properties(),true);
	private GeneratorProperties(){}
	static {
		reload();
	}
	
	public static void load(String... files) throws InvalidPropertiesFormatException, IOException {
		putAll(PropertiesHelper.load(files));
	}

	public static void putAll(Properties props) {
		GeneratorProperties.getHelper().putAll(props);
	}
	
	public static void clear() {
		GeneratorProperties.getHelper().clear();
	}
	
	public static void reload() {
		try {
			GLogger.println("Start Load GeneratorPropeties from classpath:"+Arrays.toString(PROPERTIES_FILE_NAMES));
			Properties p = new Properties();
			String[] loadedFiles = PropertiesHelper.loadAllPropertiesFromClassLoader(p,PROPERTIES_FILE_NAMES);
			GLogger.println("GeneratorPropeties Load Success,files:"+Arrays.toString(loadedFiles));
			
			setSepicalProperties(p, loadedFiles);
			
			setProperties(p);
		}catch(IOException e) {
			throw new RuntimeException("Load "+PROPERTIES_FILE_NAMES+" error",e);
		}
	}

	private static void setSepicalProperties(Properties p, String[] loadedFiles) {
		if(loadedFiles != null && loadedFiles.length > 0) {
			String basedir = p.getProperty("basedir");
			if(basedir != null && basedir.startsWith(".")) {
				p.setProperty("basedir", new File(new File(loadedFiles[0]).getParent(),basedir).getAbsolutePath());
			}
		}
	}

	public static String getDatabaseType(String key) {
		return getDatabaseType(getHelper().getProperties(),key);
	}
	
	public static String getDatabaseType(Map p,String key) {
		if(p.containsKey(key)) {
			return (String)p.get(key);
		}
		String jdbcDriver = (String)p.get(GeneratorConstants.JDBC_DRIVER.code);
        return DatabaseTypeUtils.getDatabaseTypeByJdbcDriver(jdbcDriver);
	}
	
	public static Properties getProperties() {
		return getHelper().getProperties();
	}
	
	private static PropertiesHelper getHelper() {
		Properties fromContext = GeneratorContext.getGeneratorProperties();
		if(fromContext != null) {
			return new PropertiesHelper(fromContext,true);
		}
		return propertiesHelper;
	}
	
	public static String getProperty(String key, String defaultValue) {
		return getHelper().getProperty(key, defaultValue);
	}
	
	public static String getProperty(String key) {
		return getHelper().getProperty(key);
	}

	public static String getProperty(GeneratorConstants key) {
		return getHelper().getProperty(key.code,key.defaultValue);
	}
	
	public static String getRequiredProperty(String key) {
		return getHelper().getRequiredProperty(key);
	}

	public static String getRequiredProperty(GeneratorConstants key) {
		return getHelper().getRequiredProperty(key.code);
	}
	
	public static int getRequiredInt(String key) {
		return getHelper().getRequiredInt(key);
	}
	
	public static boolean getRequiredBoolean(String key) {
		return getHelper().getRequiredBoolean(key);
	}
	
	public static String getNullIfBlank(String key) {
		return getHelper().getNullIfBlank(key);
	}

	public static String getNullIfBlank(GeneratorConstants key) {
		return getHelper().getNullIfBlank(key.code);
	}
	
	public static String[] getStringArray(String key) {
		return getHelper().getStringArray(key);
	}
	
	public static String[] getStringArray(GeneratorConstants key) {
		return getHelper().getStringArray(key.code);
	}
	
	public static int[] getIntArray(String key) {
		return getHelper().getIntArray(key);
	}
	
	public static boolean getBoolean(String key,boolean defaultValue) {
	    return getHelper().getBoolean(key, defaultValue);
	}

	public static boolean getBoolean(GeneratorConstants key) {
	    return getHelper().getBoolean(key.code, Boolean.parseBoolean(key.defaultValue));
	}
	
	public static void setProperty(GeneratorConstants key,String value) {
		setProperty(key.code,value);
	}
	
	public static void setProperty(String key,String value) {
		GLogger.debug("[setProperty()] "+key+"="+value);
		getHelper().setProperty(key, value);
	}

	public static void setProperties(Properties inputProps) {
		propertiesHelper = new PropertiesHelper(inputProps,true);
        for(Iterator it = propertiesHelper.entrySet().iterator();it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
//            assertPropertyKey(entry.getKey().toString());
            GLogger.debug("[Property] "+entry.getKey()+"="+entry.getValue());
        }
        GLogger.println("");
        
	}

}
