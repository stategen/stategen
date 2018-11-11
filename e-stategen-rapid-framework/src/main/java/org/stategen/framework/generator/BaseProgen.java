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
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.IOHelpers;
import org.stategen.framework.generator.util.PropertiesHelpers;
import org.stategen.framework.generator.util.TemplateHelpers;
import org.stategen.framework.progen.FacadeGenerator;
import org.stategen.framework.util.StringUtil;
import org.stategen.framework.util.XmlUtil;

import cn.org.rapid_framework.generator.util.StringHelper;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class BaseProgen {
    final static Logger logger = LoggerFactory.getLogger(BaseProgen.class);
    String dir_templates_root = null;
    String cmdPath = null;

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

        String systemName = System.getProperty("systemName");
        if (StringUtil.isNotBlank(systemName)) {
            root.put("systemName", systemName);
        }

        String packageName = System.getProperty("packageName");
        if (StringUtil.isNotBlank(packageName)) {
            root.put("packageName", packageName);
        }

        cmdPath = System.getProperty("cmdPath");

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
        for (File ftlFile : allFiles) {
            String relativeFileName = FileHelpers.getRelativeFileName(tempFolderFile, ftlFile);
            if (ftlFile.isFile()) {
                FacadeGenerator.processTemplate(ftlFile, conf, root, cmdPath, relativeFileName);
            } else {
                String targetFileName = TemplateHelpers.processString(root, relativeFileName);
                String filePath = cmdPath + "/" + targetFileName + "/";
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
        String systemPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/system@/");
        processTempleteFiles(root, systemPath);
    }

    public void project() throws IOException, TemplateException, DocumentException {
        Properties root = getRootProperties();
        root.put("projectName", System.getProperty("projectName"));

        String configFile = System.getProperty("generatorConfigFile");
        Properties configs = PropertiesHelpers.load(configFile);
        root.putAll(configs);
        root.putAll(StringHelper.getDirValuesMap(root));

        String projectsPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/project@/");

        String projectName = processTempleteFiles(root, projectsPath);
        String webType = System.getProperty("type");

        if (!"none".equals(webType)) {
            String webTypePath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/" + webType + "/");
            processTempleteFiles(root, webTypePath);
        }
        cmdPath = System.getProperty("cmdPath");
        String pomXmlFilename = StringUtil.concatPath(cmdPath, "pom.xml");
        //dom4j格式化输出把换行等全部去掉，因此这里采用text输出
        String pomXmlText = XmlUtil.appendToNode(pomXmlFilename, "module", projectName);
        if (StringUtil.isNotEmpty(pomXmlText)){
            IOHelpers.saveFile(new File(pomXmlFilename), pomXmlText, StringUtil.UTF_8);
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuffer("修改pom成功:").append(pomXmlFilename).toString());
            }
        }
    }

}
