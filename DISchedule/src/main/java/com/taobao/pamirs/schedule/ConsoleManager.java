package com.taobao.pamirs.schedule;

import java.io.IOException;
import java.util.Properties;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.IScheduleDataManager;
import com.taobao.pamirs.schedule.zk.ScheduleStrategyDataManager4ZK;
import com.taobao.pamirs.schedule.zk.ZKManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ConsoleManager {	
	protected static transient Logger logger = LoggerFactory.getLogger(ConsoleManager.class);
	
//	public final static String configFile = System.getProperty("user.dir") + File.separator
//			+ "pamirsScheduleConfig.properties";

	private static TBScheduleManagerFactory scheduleManagerFactory;	
    
	public static boolean isInitial() throws Exception{
		return scheduleManagerFactory != null;
	}
	
	public static boolean  initial() throws Exception{
		if(scheduleManagerFactory != null){
			return true;
		}
		
		scheduleManagerFactory = new TBScheduleManagerFactory();
		scheduleManagerFactory.start = false;
		Properties Properties = PropertiesOwn.loadProperties();
		if (Properties!=null){
		    scheduleManagerFactory.init(Properties);
            logger.info("加载Schedule配置文件：\n"+Properties);
            return true;
		} else {
            logger.warn(new StringBuffer("tbSchedule没有读取到真正的配置文件").toString());
            return false;  
		}
	}	
	
	public static TBScheduleManagerFactory getScheduleManagerFactory() throws Exception {
		if(isInitial() == false){
			initial();
		}
		return scheduleManagerFactory;
	}
	public static IScheduleDataManager getScheduleDataManager() throws Exception{
		if(isInitial() == false){
			initial();
		}
		return scheduleManagerFactory.getScheduleDataManager();
	}
	
	public static ScheduleStrategyDataManager4ZK getScheduleStrategyManager() throws Exception{
		if(isInitial() == false){
			initial();
		}
		return scheduleManagerFactory.getScheduleStrategyManager();
	}

    
    public static Properties loadConfig() throws IOException{
        Properties properties =PropertiesOwn.loadProperties();
        if (properties==null){
            properties=ZKManager.createProperties();
        }
        return properties;
    }
    
    //properties = ZKManager.createProperties(); loadConfig
    
	public static void saveConfigInfo(Properties p) throws Exception {
//		try {
//			FileWriter writer = new FileWriter(configFile);
//			p.store(writer, "");
//			writer.close();
//		} catch (Exception ex) {
//			throw new Exception("不能写入配置信息到文件：" + configFile,ex);
//		}
		
		if(scheduleManagerFactory == null){
			initial();
		}else{
			scheduleManagerFactory.reInit(p);
		}
	}
	public static void setScheduleManagerFactory(
			TBScheduleManagerFactory scheduleManagerFactory) {
		ConsoleManager.scheduleManagerFactory = scheduleManagerFactory;
	}
	
}
