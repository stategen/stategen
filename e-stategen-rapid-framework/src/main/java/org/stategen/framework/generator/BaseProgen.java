/*
 * Copyright (C) 2018 niaoge<78493244@qq.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.stategen.framework.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.GenNames;
import org.stategen.framework.generator.util.GenProperties;
import org.stategen.framework.generator.util.IOHelpers;
import org.stategen.framework.generator.util.TemplateHelpers;
import org.stategen.framework.progen.FacadeGenerator;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;
import org.stategen.framework.util.XmlUtil;

import cn.org.rapid_framework.generator.util.StringHelper;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class BaseProgen {

    final static Logger logger = LoggerFactory.getLogger(BaseProgen.class);

    static String   APPEND_TAG_DO_NOT_CHANGE = "<!--APPEND_TAG_DO_NOT_CHANGE-->";
    static String   APPEND_TAG_DO_NOT_CHANGE_REG = "\\s*<\\!--\\s*APPEND_TAG_DO_NOT_CHANGE\\s*-->";

    static String[] springbootForReplaces    = { "springboot_import", "springboot_dependencies", "springboot_plugin" };

    static String springboot_import       = "<!-- springboot_import -->";

    static String springboot_dependencies = "<!-- springboot_dependencies -->";

    static String springboot_plugin       = "<!-- springboot_plugin --> ";

    static String ROLE ="role";

    public BaseProgen(Object global) {

    }


    protected Properties getRootProperties() throws IOException {
        String     genConfigXml = GenProperties.getGenConfigXmlIfRunTest();
        Properties mergedProps                  = GenProperties.getAllMergedPropsByOrder(genConfigXml);

        String demo = mergedProps.getProperty(ROLE);
        mergedProps.put(ROLE, StringUtil.equals("true", demo));

        GenProperties.systemName  = mergedProps.getProperty(GenNames.systemName);
        GenProperties.packageName = mergedProps.getProperty(GenNames.packageName);
        GenProperties.cmdPath     = mergedProps.getProperty(GenNames.cmdPath);

        return mergedProps;
    }

    private String processTempleteFiles(Properties root, String tempPath) throws TemplateException, IOException {

        Configuration conf           = TemplateHelpers.getConfiguration(tempPath);
        File          tempFolderFile = FileHelpers.getFile(tempPath);
        conf.setDirectoryForTemplateLoading(tempFolderFile);
        List<File>   allFiles    = FileHelpers.searchAllNotIgnoreFile(tempFolderFile);
        String       projectFoldName = null;
        int          folderCount = 0;
        List<String> outFiles    = new ArrayList<String>();
        for (File ftlFile : allFiles) {
            String relativeFileName = FileHelpers.getRelativeFileName(tempFolderFile, ftlFile);
            if (ftlFile.isFile()) {
                FacadeGenerator.processTemplate(ftlFile, conf, root, GenProperties.getProjectsPath(), relativeFileName,
                        outFiles);
            } else {
                String targetFileName = TemplateHelpers.processTemplitePath(root, relativeFileName);
                String filePath       = StringUtil.concatPath(GenProperties.getProjectsPath(), targetFileName) + '/';
                filePath = FileHelpers.replaceUnOverridePath(filePath);
                FileHelpers.parentMkdir(filePath);
                folderCount++;
                if (projectFoldName == null && folderCount == 2) {
                    projectFoldName = targetFileName;
                }
            }
        }
        return projectFoldName;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void system() throws IOException, TemplateException {
        Properties root = getRootProperties();
        root.putAll(StringHelper.getDirValuesMap((Map)root));

        //system 映射到 system
        String projectsTempPath = FileHelpers.getCanonicalPath(GenProperties.dir_templates_root + "/java/system@/");
        processTempleteFiles(root, projectsTempPath);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void project() throws IOException, TemplateException, DocumentException {
        Properties root = getRootProperties();
        GenProperties.projectName = System.getProperty("projectName");

        GenProperties.putAppName(root,GenProperties.projectName);

        root.putAll(StringHelper.getDirValuesMap((Map)root));

        Boolean hasClient = false;
        String  webType   = System.getProperty("webType");
        if (StringUtil.isNotBlank(webType) && !"-e".equals(webType)) {
            hasClient = true;
            root.put("webType", webType);
        } else {
            root.put("webType", "");
        }
        root.put("hasClient", hasClient);

        String projectTempPath   = FileHelpers.getCanonicalPath(GenProperties.dir_templates_root + "/java/project@/");
        String projectFolderName = processTempleteFiles(root, projectTempPath);

        String projectsPomXmlFilename = StringUtil.concatPath(GenProperties.getProjectsPath(), "pom.xml");
        //dom4j格式化输出把换行等全部去掉，因此这里采用text输出
        String pomXmlText = XmlUtil.appendToNode(projectsPomXmlFilename, "module", projectFolderName);
        if (StringUtil.isNotEmpty(pomXmlText)) {
            IOHelpers.saveFile(new File(projectsPomXmlFilename), pomXmlText, StringUtil.UTF_8);
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuilder("修改pom成功:").append(projectsPomXmlFilename).toString());
            }
        }
        if (hasClient) {
            processClient(root, hasClient, webType, projectFolderName);
        }

    }



    private void processClient(
            Properties root,
            Boolean hasClient,
            String webType,
            String projectFolderName) throws IOException, TemplateException, DocumentException {
        String webTypePath = FileHelpers
                .getCanonicalPath(GenProperties.dir_templates_root + "/java/frontend/" + webType + '/');
        File   webTypeFile = new File(webTypePath);
        AssertUtil.mustTrue(webTypeFile.exists(), webType + " 类型不存在 ,请输入 gen.sh -h 查看具体类型");
        String currentProjectPath = StringUtil.concatPath(GenProperties.getProjectsPath(), projectFolderName);

        String frontendName = GenProperties.projectName + "_frontend_" + webType;
        root.put("frontendName", frontendName);
        //向pom中增加插件
        String pomToReplaceFileName = StringUtil.joinSLash(currentProjectPath, GenProperties.projectName + "-frontend-" + webType, "pomPluginText");
        String pomToReplaceFileName_lock =pomToReplaceFileName+".lock";
        //和之前的兼容,查看一下不带.lock的是否存在
        boolean pomToReplaceFileOldExists = FileHelpers.isExists(pomToReplaceFileName);
        if (pomToReplaceFileOldExists == false){
            pomToReplaceFileOldExists = FileHelpers.isExists(pomToReplaceFileName_lock);
        }

        processTempleteFiles(root, webTypePath);

        if (pomToReplaceFileOldExists == false) {
            String mavenPluginExcutionText = IOHelpers.readFile(new File(pomToReplaceFileName_lock), StringUtil.UTF_8);
            mavenPluginExcutionText ="\n" + mavenPluginExcutionText + "\n                            " + APPEND_TAG_DO_NOT_CHANGE;

            String projectPomXml  = StringUtil.concatPath(currentProjectPath, "pom.xml");
            String projectPomText = IOHelpers.readFile(new File(projectPomXml), StringUtil.UTF_8);
            projectPomText = projectPomText.replaceFirst(APPEND_TAG_DO_NOT_CHANGE_REG, mavenPluginExcutionText);
            IOHelpers.saveFile(new File(projectPomXml), projectPomText, StringUtil.UTF_8);
        }
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void client() throws IOException, TemplateException, DocumentException {
        Properties root = getRootProperties();
        String projectFolderName=GenProperties.checkCmdIn7(root,"client");

        root.putAll(StringHelper.getDirValuesMap((Map)root));

        Boolean hasClient = false;
        String  webType   = System.getProperty("webType");
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
