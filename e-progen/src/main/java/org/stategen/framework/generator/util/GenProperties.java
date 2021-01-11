package org.stategen.framework.generator.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.BusinessAssert;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenProperties {
    public final static String  generator_tools_class="generator_tools_class";
    public final static String  gen_config_xml="/gen_config.xml";

    
    public static String dir_templates_root = null;
    
    public static String dalgenHome         = null;
    
    public static String tableName          = null;
    
    private static String projectsPath = null;
    
    public static String  projectPath  = null;
    
    public static String  projectName  = null;
    
    public static String  systemName   = null;
    
    public static String  packageName  = null;
    
    public static String cmdPath = null;
    
    public static DaoType daoType = DaoType.ibatis;
    
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenProperties.class);
    
    public static void setProjectsPath(String projectsPath) {
        GenProperties.projectsPath = projectsPath;
    }
    
    public static String getProjectsPath() {
        return projectsPath;
    }
    
    public static String getGenConfigXmlIfRunTest() throws IOException {
        String cmdPath = System.getProperty(GenNames.cmdPath);
        if (StringUtil.isNotEmpty(cmdPath)) {
            String configFile = System.getProperty(GenNames.genConfigXml);
            return configFile;
        }
        
        String testClassPath = GenProperties.class.getResource("/").toString();
        projectPath = FileHelpers.getCanonicalPath(testClassPath + "../../");
        setProjectsPath(FileHelpers.getCanonicalPath(projectPath + "../"));
        String genConfigXml = FileHelpers.getCanonicalPath(getProjectsPath() + gen_config_xml);
        
        System.setProperty(GenNames.projectsPath, getProjectsPath());
        System.setProperty(GenNames.projectPath, projectPath);
        return genConfigXml;
    }
    
    public static void putStaticFiles(Properties properties, String... addtionalStatics) {
        
        String staticFiles[] = { "cn.org.rapid_framework.generator.util.StringHelper",
                "org.apache.commons.lang.StringUtils",
                "org.stategen.framework.util.CollectionUtil",
                "org.stategen.framework.util.Setting",
                "org.stategen.framework.generator.util.CompatibleHelper",
                "org.stategen.framework.util.StringUtil",
                "org.stategen.framework.generator.util.TableHelper",
                "org.stategen.framework.util.Context",
                "org.stategen.framework.generator.util.FoldUtil"
        };
        
        if (CollectionUtil.isNotEmpty(addtionalStatics)) {
            staticFiles = StringUtils.concatenateStringArrays(staticFiles, addtionalStatics);
        }
        
        String joinedFiles = StringUtil.concat(",", staticFiles);

        properties.put(generator_tools_class, joinedFiles);

        //        properties.put("generator_tools_class","cn.org.rapid_framework.generator.util.StringHelper,org.apache.commons.lang.StringUtils,org.stategen.framework.util.CollectionUtil,org.stategen.framework.util.Setting,org.stategen.framework.generator.util.CompatibleHelper,org.stategen.framework.util.StringUtil,org.stategen.framework.generator.util.TableHelper");
    }

    public static void genStaticTools(Properties properties){
        Map<String, Object> toolsMap = getToolsMap(properties,generator_tools_class);
        properties.putAll(toolsMap);
    }

    private static String[] tokenizeToStringArray(String str, String seperators) {
        if (str == null) {
            return new String[0];
        }
        StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
        List<String> result = new ArrayList<>();

        while (tokenlizer.hasMoreElements()) {
            String s = (String) tokenlizer.nextElement();
            result.add(s);
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static String[] getStringArray(Properties properties,String key) {
        String v = properties.getProperty(key);
        if(v == null) {
            return new String[0];
        }else {
            return tokenizeToStringArray(v, ", \t\n\r\f");
        }
    }


    /** 得到模板可以引用的工具类 */
    public static Map<String, Object> getToolsMap(Properties properties,String keyCode) {
        Map<String, Object> toolsMap = new HashMap<>();
        String[] tools = getStringArray(properties,keyCode);
        for (String className : tools) {
            try {
                Object instance = ClassHelper.newInstance(className);
                toolsMap.put(Class.forName(className).getSimpleName(), instance);
                log.debug("put tools class:" + className + " with key:"
                        + Class.forName(className).getSimpleName());
            } catch (Exception e) {
                log.error("cannot load tools by className:" + className + " cause:" + e);
            }
        }
        return toolsMap;
    }

    public static void putGenXml(Properties mergedProps) throws IOException {
        mergedProps.putAll(PropertiesHelpers.load(dalgenHome + "/gen.xml"));
    }

    public static void putGenCustomXml(Properties mergedProps) throws IOException {
        mergedProps.putAll(PropertiesHelpers.load(dalgenHome + "/gen.custom.xml"));
    }

    public static  void convertToFoldNumberType(Properties mergedProps){
        String foldNumberType = mergedProps.getProperty("foldNumberType");
        logger.info(new StringBuilder("输出info信息: foldNumberType:").append(foldNumberType).toString());
        FoldNumberType.convertToFoldNumberType(foldNumberType);
    }

    public static Properties getAllMergedPropsByOrder(String genConfigXml) throws IOException {
        
        Map<String, String> environments = System.getenv();
        
        dalgenHome = environments.get(GenNames.DALGENX_HOME);
        BusinessAssert.mustNotBlank(dalgenHome, GenNames.DALGENX_HOME + " 环境变量没有设!");
        
        Properties mergedProps = new Properties();
        mergedProps.put("gg_isOverride", "true");
        
        mergedProps.put("generator_sourceEncoding", "UTF-8");
        mergedProps.put("generator_outputEncoding", "UTF-8");

        //将表名从复数转换为单数
        mergedProps.put("tableNameSingularize", "true");

        mergedProps.putAll(environments);

        Properties systemProperties = System.getProperties();
        mergedProps.putAll(systemProperties);
        
        //以下是覆盖顺序
        putGenXml(mergedProps);
        //这个文件不会上传，属于用户自己的全局设置
        putGenCustomXml(mergedProps);

        if (FileHelpers.isExists(genConfigXml)) {
            mergedProps.putAll(PropertiesHelpers.load(genConfigXml));
        }
        
        dalgenHome         = (String) mergedProps.get(GenNames.DALGENX_HOME);
        dir_templates_root = dalgenHome + "/templates/";
        mergedProps.put("dir_templates_root", dir_templates_root);
        
        setProjectsPath(mergedProps.getProperty(GenNames.projectsPath));
        tableName = mergedProps.getProperty(GenNames.tableName);
        GenProperties.putStaticFiles(mergedProps);
        genStaticTools(mergedProps);

        convertToFoldNumberType(mergedProps);

        return mergedProps;
    }

    public static void putAppName(Properties root, String projectName) {
        String systemName = root.getProperty("systemName");
        String appName = StringUtil.capfirst(systemName)+StringUtil.capfirst(projectName);
        root.put("appName", appName);
    }

    public static String checkCmdIn7(Properties pts,String cmd) throws IOException {
        String systemName = pts.getProperty("systemName");
        cmdPath =pts.getProperty("cmdPath");

        if (logger.isInfoEnabled()){
            logger.info(new StringBuilder("输出info信息: cmdPath:").append(cmdPath).toString());
        }
        String cmdParnet = new File(cmdPath).getParent();
        GenProperties.convertToFoldNumberType(pts);

        String projectFolderName = StringUtil.trimLeftFormRightTo(cmdPath, StringUtil.SLASH);
        String projectFrefix = FoldUtil.get(7, systemName) + "-web-";

        String pojo=FoldUtil.get(1,systemName)+"-pojo";
        String facade=FoldUtil.get(2,systemName)+"-facade";
        String intergrade=FoldUtil.get(3,systemName)+"-intergrade";
        String dao=FoldUtil.get(4,systemName)+"-dao";
        String service=FoldUtil.get(5,systemName)+"-service";
        String web_base=FoldUtil.get(6,systemName)+"-web-base";

        Set<String> syss = new HashSet<>(Arrays.asList(pojo, facade, intergrade, dao, service, web_base));



        AssertUtil.mustTrue(projectFolderName.startsWith(projectFrefix) && !syss.contains(projectFolderName),
                cmd+"命令必须在" + projectFrefix + "-xxx 项目内执行");

        GenProperties.projectName = StringUtil.trimePrefixIgnoreCase(projectFolderName, projectFrefix);
        String projectName =GenProperties.projectName;
        pts.put("projectName", projectName);
        putAppName(pts, projectName);
        return projectFolderName;
    }


}
