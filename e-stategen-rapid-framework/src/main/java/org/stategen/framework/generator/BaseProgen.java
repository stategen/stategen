/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.org.rapid_framework.generator.util.StringHelper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.IOHelpers;
import org.stategen.framework.generator.util.PropertiesHelpers;
import org.stategen.framework.generator.util.TemplateHelpers;
import org.stategen.framework.progen.FacadeGenerator;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;
import org.stategen.framework.util.XmlUtil;

public class BaseProgen {
    final static Logger logger = LoggerFactory.getLogger(BaseProgen.class);
    String dir_templates_root = null;
    String projectsPath = null;
    String systemName = null;
    String projectName = null;
    String packageName = null;

    String cmdPath = null;

    static String APPEND_TAG_DO_NOT_CHANGE = "<!--APPEND_TAG_DO_NOT_CHANGE-->";
    static String[] springbootForReplaces = {"springboot_import", "springboot_dependencies", "springboot_plugin"};


    static String springboot_import = "<!-- springboot_import -->";
    static String springboot_dependencies = "<!-- springboot_dependencies -->";
    static String springboot_plugin = "<!-- springboot_plugin --> ";


    public BaseProgen(Object global) {

    }

    protected Properties getRootProperties() throws IOException {
        Properties root = new Properties();
        root.put("generator_tools_class", "org.stategen.framework.util.StringUtil");
        root.put("gg_isOverride", "true");

        root.put("generator_sourceEncoding", "UTF-8");
        root.put("generator_outputEncoding", "UTF-8");
        root.put("gg_isOverride", "true");

        //将表名从复数转换为单数
        root.put("tableNameSingularize", "true");

        //加载默认配置文件
        String dalgenPath = System.getProperty("dalgenPath");
        String genFileName = FileHelpers.getCanonicalPath(dalgenPath + "/gen.xml");

        root.putAll(PropertiesHelpers.load(genFileName));

        //加载项目配置文件，如果相同，覆盖默认配置
        root.put("dalgenPath", dalgenPath);

        systemName = System.getProperty("systemName");
        if (StringUtil.isNotBlank(systemName)) {
            root.put("systemName", systemName);
        }

        packageName = System.getProperty("packageName");
        if (StringUtil.isNotBlank(packageName)) {
            root.put("packageName", packageName);
        }

        cmdPath = System.getProperty("cmdPath");
        projectsPath = System.getProperty("projectsPath");

        dir_templates_root = dalgenPath + "/templates/" + root.getProperty("dao_type");

        return root;
    }

    private String processTempleteFiles(Properties root, String tempPath) throws TemplateException, IOException {

        Configuration conf = TemplateHelpers.getConfiguration(tempPath);
        File tempFolderFile = FileHelpers.getFile(tempPath);
        conf.setDirectoryForTemplateLoading(tempFolderFile);
        List<File> allFiles = FileHelpers.searchAllNotIgnoreFile(tempFolderFile);
        String projectName = null;
        int folderCount = 0;
        List<String> outFiles = new ArrayList<String>();
        for (File ftlFile : allFiles) {
            String relativeFileName = FileHelpers.getRelativeFileName(tempFolderFile, ftlFile);
            if (ftlFile.isFile()) {
                FacadeGenerator.processTemplate(ftlFile, conf, root, projectsPath, relativeFileName, outFiles);
            } else {
                String targetFileName = TemplateHelpers.processTemplitePath(root, relativeFileName);
                String filePath = StringUtil.concatPath(projectsPath, targetFileName) + "/";
                filePath = FileHelpers.replaceUnOverridePath(filePath);
                FileHelpers.parentMkdir(filePath);
                folderCount++;
                if (projectName == null && folderCount == 2) {
                    projectName = targetFileName;
                }
            }
        }
        return projectName;
    }

    public void system() throws IOException, TemplateException {
        Properties root = getRootProperties();
        root.putAll(StringHelper.getDirValuesMap(root));

        //system 映射到 system
        String projectsTempPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/system@/");
        processTempleteFiles(root, projectsTempPath);
    }

    public void project() throws IOException, TemplateException, DocumentException {
        Properties root = getRootProperties();
        projectName = System.getProperty("projectName");
        root.put("projectName", projectName);

        String configFile = System.getProperty("generatorConfigFile");
        Properties configs = PropertiesHelpers.load(configFile);
        root.putAll(configs);
        root.putAll(StringHelper.getDirValuesMap(root));

        Boolean hasClient = false;
        String webType = System.getProperty("webType");
        if (StringUtil.isNotBlank(webType) && !"-e".equals(webType)) {
            hasClient = true;
            root.put("webType", webType);
        } else {
            root.put("webType", "");
        }
        root.put("hasClient", hasClient);

        String projectTempPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/project@/");
        String projectFolderName = processTempleteFiles(root, projectTempPath);

        String projectsPomXmlFilename = StringUtil.concatPath(projectsPath, "pom.xml");
        //dom4j格式化输出把换行等全部去掉，因此这里采用text输出
        String pomXmlText = XmlUtil.appendToNode(projectsPomXmlFilename, "module", projectFolderName);
        if (StringUtil.isNotEmpty(pomXmlText)) {
            IOHelpers.saveFile(new File(projectsPomXmlFilename), pomXmlText, StringUtil.UTF_8);
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuffer("修改pom成功:").append(projectsPomXmlFilename).toString());
            }
        }
        if (hasClient) {
            processClient(root, hasClient, webType, projectFolderName);
        }

    }

    private void processClient(Properties root, Boolean hasClient, String webType,
                               String projectFolderName) throws IOException, TemplateException, DocumentException {
        String webTypePath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/frontend/" + webType + "/");
        File webTypeFile = new File(webTypePath);
        AssertUtil.mustTrue(webTypeFile.exists(), webType + " 类型不存在 ,请输入 gen.sh -h 查看具体类型");
        String currentProjectPath = StringUtil.concatPath(projectsPath, projectFolderName);

        String frontendName = projectName + "_frontend_" + webType;
        root.put("frontendName", frontendName);

        String pomToReplaceFileName = StringUtil.concatPath(currentProjectPath, projectName + "-frontend-" + webType, "pom");
        File pomToReplaceFile = new File(pomToReplaceFileName);
        boolean pomToReplaceFileOldExists = pomToReplaceFile.exists() && pomToReplaceFile.isFile();
        processTempleteFiles(root, webTypePath);

        if (!pomToReplaceFileOldExists) {
            String mavenPluginExcutionText = IOHelpers.readFile(new File(pomToReplaceFileName), StringUtil.UTF_8);
            mavenPluginExcutionText += "\n                    " + APPEND_TAG_DO_NOT_CHANGE;

            String projectPomXml = StringUtil.concatPath(currentProjectPath, "pom.xml");
            String projectPomText = IOHelpers.readFile(new File(projectPomXml), StringUtil.UTF_8);
            projectPomText = projectPomText.replace(APPEND_TAG_DO_NOT_CHANGE, mavenPluginExcutionText);
            IOHelpers.saveFile(new File(projectPomXml), projectPomText, StringUtil.UTF_8);
        }
    }

    public void root() throws IOException, TemplateException {
        Properties root = getRootProperties();
        String configFile = System.getProperty("generatorConfigFile");
        Properties configs = PropertiesHelpers.load(configFile);
        root.putAll(configs);
        root.putAll(StringHelper.getDirValuesMap(root));

        systemName = root.getProperty("systemName");

        String projectFolderName = StringUtil.trimLeftFormRightTo(cmdPath, StringUtil.SLASH);

        String projectFrefix = "7-" + systemName + "-web-";
        AssertUtil.mustTrue(projectFolderName.startsWith(projectFrefix), "client 命令必须在" + "7-" + systemName + "-web-xxx 执行");

        projectName = StringUtil.trimePrefixIgnoreCase(projectFolderName, projectFrefix);
        root.put("projectName", projectName);

        String springRootTempPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/spring-root@/");
        String currentProjectPath = StringUtil.concatPath(projectsPath, projectFolderName);
        processTempleteFiles(root, springRootTempPath);

        String projectPomXml = StringUtil.concatPath(currentProjectPath, "pom.xml");
        String projectPomText = IOHelpers.readFile(new File(projectPomXml), StringUtil.UTF_8);
        for (String springBootForReplace : springbootForReplaces) {
            String spingBootReplaceFileName = StringUtil.concatPath(currentProjectPath, "src/main/java/config/", springBootForReplace);
            String spingBootReplaceText = IOHelpers.readFile(new File(spingBootReplaceFileName), StringUtil.UTF_8);

            projectPomText = projectPomText.replace("<!--" + springBootForReplace + "-->", spingBootReplaceText);
        }
        IOHelpers.saveFile(new File(projectPomXml), projectPomText, StringUtil.UTF_8);

//        Boolean hasClient = false;
//        String webType = System.getProperty("webType");
//        if (StringUtil.isNotBlank(webType) && !"-e".equals(webType)) {
//            hasClient = true;
//            root.put("webType", webType);
//        } else {
//            root.put("webType", "");
//        }
//        root.put("hasClient", hasClient);
//
//        if (hasClient) {
//            processClient(root, hasClient, webType, projectFolderName);
//        }
    }

    public void client() throws IOException, TemplateException, DocumentException {
        Properties root = getRootProperties();
        String configFile = System.getProperty("generatorConfigFile");
        Properties configs = PropertiesHelpers.load(configFile);
        root.putAll(configs);
        root.putAll(StringHelper.getDirValuesMap(root));

        systemName = root.getProperty("systemName");

        String projectFolderName = StringUtil.trimLeftFormRightTo(cmdPath, StringUtil.SLASH);

        String projectFrefix = "7-" + systemName + "-web-";
        AssertUtil.mustTrue(projectFolderName.startsWith(projectFrefix), "client 命令必须在" + "7-" + systemName + "-web-xxx 执行");

        projectName = StringUtil.trimePrefixIgnoreCase(projectFolderName, projectFrefix);
        root.put("projectName", projectName);

        Boolean hasClient = false;
        String webType = System.getProperty("webType");
        if (StringUtil.isNotBlank(webType) && !"-e".equals(webType)) {
            hasClient = true;
            root.put("webType", webType);
        } else {
            root.put("webType", "");
        }
        root.put("hasClient", hasClient);

        if (hasClient) {
            processClient(root, hasClient, webType, projectFolderName);
        }
    }


}
