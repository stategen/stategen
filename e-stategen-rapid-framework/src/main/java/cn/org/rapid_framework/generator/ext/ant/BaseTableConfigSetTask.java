package cn.org.rapid_framework.generator.ext.ant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.stategen.framework.util.ThreadLocalUtil;

import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
/**
 * 如果要使用TableConfigSet的GeneratorTask，则可以继承该类
 * 
 * @author badqiu
 *
 */
public abstract class BaseTableConfigSetTask extends BaseGeneratorTask{
	
	private String tableConfigFiles; 
	protected TableConfigSet tableConfigSet;
	
	private static ThreadLocal<Map<String, Object>> threadLocalCache = ThreadLocalUtil.createLocalThread();

	@Override
	protected void executeBefore() throws Exception {
		super.executeBefore();
		
		if(tableConfigFiles == null || "".equals(tableConfigFiles.trim())) {
			throw new Exception("'tableConfigFiles' must be not null");
		}
		
		if(tableConfigSet == null) {
			Map<String, Object> cache = getThreadLocalCache();
			tableConfigSet = (TableConfigSet)cache.get(tableConfigFiles);
			if(tableConfigSet == null) {
				tableConfigSet = parseForTableConfigSet(getPackage(), getProject().getBaseDir().getAbsoluteFile(), tableConfigFiles);
			}
			cache.put(tableConfigFiles, tableConfigSet);
		}
	}

	public static Map<String, Object> getThreadLocalCache() {
		Map<String, Object> map = threadLocalCache.get();
		if(map == null) {
			map = new HashMap<>();
			threadLocalCache.set(map);
		}
		return map;
	}

	public void setTableConfigFiles(String tableConfigFiles) {
		this.tableConfigFiles = tableConfigFiles;
	}

	
    static TableConfigSet parseForTableConfigSet(String _package,File basedir,String tableConfigFiles) {
    	return TableConfigXmlBuilder.parseFromXML(_package, basedir, tableConfigFiles);
    }
}
