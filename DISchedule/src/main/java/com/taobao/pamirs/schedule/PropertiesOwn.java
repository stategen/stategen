package com.taobao.pamirs.schedule;

import java.io.IOException;
import java.util.Properties;

import com.taobao.pamirs.schedule.zk.ZKManager.keys;

public class PropertiesOwn {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PropertiesOwn.class);
    
    private static  String zkConnectString=keys.zkConnectString.getDefaultValue();  
    private static  String  rootPath=keys.zkConnectString.getDefaultValue();  
    private static  String  userName=keys.userName.getDefaultValue();
    private static  String  password=keys.password.getDefaultValue();
    private static  String  zkSessionTimeout=keys.zkSessionTimeout.getDefaultValue();
    private static  String  isCheckParentPath=keys.isCheckParentPath.getDefaultValue();
    
    public static void setZkConnectString(String zkConnectString) {
        final String zkHead ="zookeeper://";
        if (zkConnectString!=null && zkConnectString.startsWith(zkHead)){
            zkConnectString =zkConnectString.substring(zkHead.length());
        }
        PropertiesOwn.zkConnectString = zkConnectString;
    }
    
    public static void setRootPath(String rootPath) {
        PropertiesOwn.rootPath = rootPath;
    }
    
    public static void setUserName(String userName) {
        PropertiesOwn.userName = userName;
    }
    
    public static void setPassword(String password) {
        PropertiesOwn.password = password;
    }
    
    public static void setZkSessionTimeout(String zkSessionTimeout) {
        PropertiesOwn.zkSessionTimeout = zkSessionTimeout;
    }
    
    public static void setIsCheckParentPath(String isCheckParentPath) {
        PropertiesOwn.isCheckParentPath = isCheckParentPath;
    }
    
//    public final static String application_properties_file ="application.properties";
//    public final static String schedule_config_file="schedule.config.file";
//    
//    public static String       systemName             = "system.name";
//    
//    public static final String schedule_zkConfig_prefix = "scheduleManagerFactory.zkConfig";
//    
//    public static void findConfigFile(){
//        if (configFile==null && systemName==null){
//            if (logger.isInfoEnabled()) {
//                logger.info(new StringBuilder("读取:").append(application_properties_file).toString());
//            }
//            
//            Properties properties =PropertiesUtil.loadPropertiesFromClasspath(application_properties_file);
//            String configlFileName =null;
//            if (properties!=null){
//                if (logger.isInfoEnabled()) {
//                    logger.info(new StringBuilder("读取:").append(application_properties_file).append(" 成功!").toString());
//                }                
//                configlFileName=PropertiesUtil.getStringProperty(schedule_config_file, properties);
//                systemName=PropertiesUtil.getStringProperty(systemName, properties);
//                if (systemName==null){
//                    logger.warn(new StringBuilder("请在application.properties中设置system.name").toString());
//                }
//            } else {
//                logger.warn(new StringBuilder("没有从application.properties找到键值 :").append(schedule_config_file).toString());
//            }
//            
//            if (logger.isInfoEnabled()) {
//                logger.info(new StringBuilder(schedule_config_file).append("的配置文件为:").append(configlFileName).toString());
//            }
//            
//            if (configlFileName!=null){
//                String realUriPathByOs = OSUtil.getRealUriPathByOs(configlFileName);
//                if (realUriPathByOs.startsWith(OSUtil.filePrefix)){
//                    configFile =realUriPathByOs.substring(OSUtil.filePrefix.length());
//                } else {
//                    configFile =realUriPathByOs;
//                }
//            }
//            
//            if (logger.isInfoEnabled()) {
//                logger.info(new StringBuilder("configFile为:").append(configFile).append(",systemName为:").append(systemName).toString());
//            }   
//            
//        }
//    }
    
    public static Properties loadProperties() throws IOException{
        Properties dest= new Properties();
        dest.put(keys.zkConnectString.toString(),zkConnectString);  
        dest.put(keys.rootPath.toString(),rootPath);  
        dest.put(keys.userName.toString(),userName);  
        dest.put(keys.password.toString(),password);  
        dest.put(keys.zkSessionTimeout.toString(),zkSessionTimeout);  
        dest.put(keys.isCheckParentPath.toString(),isCheckParentPath);  
        return dest;
    }
}
