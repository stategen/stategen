package org.stategen.framework.generator.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.StringUtils;
import org.stategen.framework.util.BusinessAssert;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

public class GenProperties {


    public static String dir_templates_root = null;
    public static String dalgenHome = null;
    public static String tableName = null;
    
    private static String projectsPath = null;
    public static String projectPath = null;
    public static String projectName = null;
    public static String systemName = null;
    public static String packageName = null;

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
            String     configFile = System.getProperty(GenNames.genConfigXml);
            return configFile;
        }
        
        String testClassPath = GenProperties.class.getResource("/").toString();
        projectPath = FileHelpers.getCanonicalPath(testClassPath + "../../");
        setProjectsPath(FileHelpers.getCanonicalPath(projectPath + "../"));
        String genConfigXml = FileHelpers.getCanonicalPath(getProjectsPath() + "/gen_config.xml");
        
        System.setProperty(GenNames.projectsPath, getProjectsPath());
        System.setProperty(GenNames.projectPath, projectPath);
        return genConfigXml;
    }
    
    public static void genStaticFiles(Properties properties,String...addtionalStatics) {

        String staticFiles[] = {"cn.org.rapid_framework.generator.util.StringHelper",
                "org.apache.commons.lang.StringUtils",
                "org.stategen.framework.util.CollectionUtil",
                "org.stategen.framework.util.Setting",
                "org.stategen.framework.generator.util.CompatibleHelper",
                "org.stategen.framework.util.StringUtil",
                "org.stategen.framework.generator.util.TableHelper",
                "org.stategen.framework.util.Context"
        };
            
        if (CollectionUtil.isNotEmpty(addtionalStatics)){
            staticFiles=StringUtils.concatenateStringArrays(staticFiles, addtionalStatics);
        }
        
        String joinedFiles = StringUtil.concat(",", staticFiles);
        properties.put("generator_tools_class",joinedFiles);
//        properties.put("generator_tools_class","cn.org.rapid_framework.generator.util.StringHelper,org.apache.commons.lang.StringUtils,org.stategen.framework.util.CollectionUtil,org.stategen.framework.util.Setting,org.stategen.framework.generator.util.CompatibleHelper,org.stategen.framework.util.StringUtil,org.stategen.framework.generator.util.TableHelper");
    }

    public static Properties getAllMergedProps(String genConfigXml) throws IOException {

        Map<String, String> environments = System.getenv();

        String dalgenHome = environments.get(GenNames.DALGENX_HOME);
        BusinessAssert.mustNotBlank(dalgenHome, GenNames.DALGENX_HOME + " 环境变量没有设!");

        String genXml = dalgenHome + "/gen.xml";

        Properties mergedProps = new Properties();
        mergedProps.put("gg_isOverride", "true");

        mergedProps.put("generator_sourceEncoding", "UTF-8");
        mergedProps.put("generator_outputEncoding", "UTF-8");

        //将表名从复数转换为单数
        mergedProps.put("tableNameSingularize", "true");

        mergedProps.putAll(environments);

        Properties systemProperties = System.getProperties();
        mergedProps.putAll(systemProperties);

        Properties genXmlProperties = PropertiesHelpers.load(genXml);
        mergedProps.putAll(genXmlProperties);
        if (FileHelpers.isExists(genConfigXml)) {
            Properties genConfigXmlProperties = PropertiesHelpers.load(genConfigXml);
            mergedProps.putAll(genConfigXmlProperties);
        }

        dalgenHome = (String) mergedProps.get(GenNames.DALGENX_HOME);
        dir_templates_root = dalgenHome + "/templates/";
        mergedProps.put("dir_templates_root", dir_templates_root);
        
        setProjectsPath(mergedProps.getProperty(GenNames.projectsPath));
        tableName=mergedProps.getProperty(GenNames.tableName);

        return mergedProps;
    }
}
