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
package org.stategen.framework.progen;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.IOHelpers;
import org.stategen.framework.generator.util.TemplateHelpers;
import org.stategen.framework.progen.wrap.ApiWrap;
import org.stategen.framework.progen.wrap.CanbeImportWrap;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The Class FacadeGenerator.
 */
public class FacadeGenerator {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FacadeGenerator.class);

    public void genFacades(List<Class<?>> classes, Properties root) throws IOException, TemplateException {

        String controlleSuffix = root.getProperty("controller_name_suffix");

        Map<Class<?>, CanbeImportWrap> apiWrapMap = GenContext.wrapContainer.getCanbeImportWrapMapByPathType(PathType.API);

        Set<String> apis = new HashSet<String>();

        for (Class<?> clz : classes) {
            if (AnnotatedElementUtils.findMergedAnnotation(clz, Controller.class) != null) {
                String controllerName = clz.getSimpleName();
                String apiName = StringUtil.trimRight(controllerName, controlleSuffix);

                AssertUtil.mustNotContainsAndAdd(apis, apiName, "不能存在2个相同的api:" + apiName + controlleSuffix);

                try {
                    ApiWrap controllerWrap = new ApiWrap(clz, apiName);
                    if (controllerWrap.isApi()) {
                        apiWrapMap.put(clz, controllerWrap);
                        if ("App".equals(controllerWrap.toString())){
                            GenContext.appWrap=controllerWrap;
                        }
                    }
                } catch (Exception e) {
                    logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage()).append(apiName)
                        .append(" \n").toString(), e);
                    throw e;
                }
            }
        }
        AssertUtil.mustNotNull(GenContext.appWrap,"缺少AppController,必须有一个Controller为AppController,而且上面的标注@ApiConfig(menu = false),\n"
                + "因为一些ajax Options的方法是统一从这个Controller中拿取");

        GenContext.wrapContainer.scanBeanRelationShipAndMakeFeilds();
        List<String> tempDirs = GenContext.tempDirs;

        String outDir = GenContext.outDir;
        String outConfigDir = GenContext.outConfigDir;
        root.put("configDir", outConfigDir);

        String tempRootPath = GenContext.tempRootPath;
        String projectRootPath = GenContext.projectRootPath;

        Map<String, List<CanbeImportWrap>> canbeImportWrapMap = new HashMap<String, List<CanbeImportWrap>>();

        for (Entry<PathType, String> entry : GenContext.pathMap.entrySet()) {
            PathType pathType = entry.getKey();
            String importPath = entry.getValue();
            
            List<String> tempWholePaths=getTempPaths(tempDirs, tempRootPath, importPath, "dal");
            
            String reletiveOutWholePath = StringUtil.joinSLash(projectRootPath, outDir, importPath);
            String outWholePath = FileHelpers.getFile(reletiveOutWholePath).getAbsolutePath();
            FreeMarkerTempFileContext beanFltContext = new FreeMarkerTempFileContext(tempWholePaths);
            Configuration conf = beanFltContext.getConf();
            List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(conf, Arrays.asList("macro.include.ftl"));
            conf.setAutoIncludes(availableAutoInclude);

            Map<Class<?>, CanbeImportWrap> wrapMap = GenContext.wrapContainer.getCanbeImportWrapMapByPathType(pathType);
            canbeImportWrapMap.put(importPath, new ArrayList<CanbeImportWrap>(wrapMap.values()));

            for (CanbeImportWrap canbeImportWrap : wrapMap.values()) {
                String wrapName = pathType.getWrapName();
                root.put(wrapName, canbeImportWrap);
                for (FtlFile ftlFile : beanFltContext.getFtlFiles()) {
                    File tempPathFile = ftlFile.getTempPathFile();
                    File tempFile = ftlFile.getTempFile();
                    String relativeFileName = FileHelpers.getRelativeFileName(tempPathFile, tempFile);
                    processTemplate(tempFile, conf, root, outWholePath, relativeFileName);
                }
                root.remove(wrapName);
            }
        }
        List<String> tempWholePaths=getTempPaths(tempDirs, tempRootPath, "", "set");
        FreeMarkerTempFileContext beanFltContext = new FreeMarkerTempFileContext(tempWholePaths);
        
        String reletiveOutWholePath = StringUtil.joinSLash(projectRootPath, outDir, outConfigDir);
        String outWholePath = FileHelpers.getFile(reletiveOutWholePath).getAbsolutePath();
        
        Configuration conf = beanFltContext.getConf();
        List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(conf, Arrays.asList("macro.include.ftl"));
        conf.setAutoIncludes(availableAutoInclude);
        root.putAll(canbeImportWrapMap);

        for (FtlFile ftlFile : beanFltContext.getFtlFiles()) {
            File tempPathFile = ftlFile.getTempPathFile();
            File tempFile = ftlFile.getTempFile();
            String relativeFileName = FileHelpers.getRelativeFileName(tempPathFile, tempFile);
            processTemplate(tempFile, conf, root, outWholePath, relativeFileName);
        }
    }

    private List<String> getTempPaths(List<String> tempDirs, String tempRootPath, String importPath,String subName) throws IOException {
        List<String> tempWholePaths =new ArrayList<String>(tempDirs.size()*2);
        
        for (String tempDir : tempDirs) {
            String tempPath = StringUtil.joinSLash(tempDir, subName);
            if (StringUtil.isNotEmpty(importPath)){
                tempPath=StringUtil.joinSLash(tempPath,importPath);
            }
            String tempWholePath = StringUtil.joinSLash(tempRootPath, tempPath);
            tempWholePaths.add(tempWholePath);
        }
        String sharedPath = FileHelpers.getCanonicalPath(StringUtil.joinSLash(tempRootPath, tempDirs.get(0), "share"));
        tempWholePaths.add(sharedPath);
        return tempWholePaths;
    }

    public static void processTemplate(File tempFile,Configuration conf, Properties root, String outputDir,
                                       String relativeFileName) throws TemplateException, IOException {

        final String blobExt = "@blob";
        final String tempExt = "@temp";
        
        
        String targetFileName = TemplateHelpers.processString(root, relativeFileName);
        if (StringUtil.isEmpty(targetFileName)){
            targetFileName=StringUtil.SLASH; 
        }

        String notOverrideTargetFilenName = null;
        boolean doOverride = true;
        if (targetFileName.endsWith(tempExt)) {
            notOverrideTargetFilenName = targetFileName;
            targetFileName = StringUtil.trimRight(targetFileName, tempExt);
            notOverrideTargetFilenName = FileHelpers.getCanonicalPath(outputDir, notOverrideTargetFilenName);
            notOverrideTargetFilenName =FileHelpers.replaceUnOverridePath(notOverrideTargetFilenName);
        } else if (targetFileName.endsWith("@")) {
            doOverride = false;
            targetFileName = StringUtil.trimRight(targetFileName, "@");
        }

        boolean isBlob = false;
        if (targetFileName.endsWith(blobExt)) {
            targetFileName = targetFileName.substring(0, targetFileName.length() - blobExt.length());
            isBlob = true;
        }

        CharArrayWriter writer = new CharArrayWriter(1024 * 1024);
        
        String fullTargetFileName = FileHelpers.getCanonicalPath(outputDir, targetFileName);
        
        if (FileHelpers.isUnOverridePath(fullTargetFileName)){
            fullTargetFileName =FileHelpers.replaceUnOverridePath(fullTargetFileName);
            doOverride =false;
        }

        File fullTargetFile = FileHelpers.parentMkdir(fullTargetFileName);
        boolean isDirectory = fullTargetFile.isDirectory();
        if (!isDirectory && (isBlob || FileHelpers.isBinaryFile(fullTargetFileName))) {
            if (fullTargetFile.exists()){
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuffer("二进制文件已存在，忽略写入:").append(fullTargetFile).toString());
                }
                return ;
            }
            
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuffer("写入二进制文件").append(fullTargetFile).toString());
            }
            Files.copy(tempFile.toPath(), fullTargetFile.toPath());
            writer.close();
            return;
        } else {
            if (!isDirectory) {
                Template template = conf.getTemplate(relativeFileName, Locale.getDefault(), StringUtil.UTF_8);
                processFileString(root, writer, template, targetFileName);
            }
        }
        


        String newTargetFileName = fullTargetFileName;
        String newText = writer.toString();
        writer.close();

        if (!isDirectory) {
            if (fullTargetFile.exists() && fullTargetFile.isFile()) {
                if (!doOverride) {
                    logger.info(new StringBuffer("模版文件或文件夹以@或@.ftl结尾，当目标文件存在时，忽略写入:" + fullTargetFileName).append('\n'+newText).toString());
                    return;
                }
                
                if (FileHelpers.isSameFileText(fullTargetFileName, newText)) {
                    logger.info("文件比较结果:相同，忽略写入:" + fullTargetFileName);
                    return;
                }

                if (StringUtil.isNotBlank(notOverrideTargetFilenName)) {
                    File tmpFile = FileHelpers.parentMkdir(notOverrideTargetFilenName);
                    if (!tmpFile.isDirectory() && FileHelpers.isSameFileText(notOverrideTargetFilenName, newText)) {
                        logger.info("文件比较结果:相同，忽略写入:" + notOverrideTargetFilenName);
                        return;
                    }
                    //写入临时文件，以便复制
                    newTargetFileName = notOverrideTargetFilenName;
                }
            }

            File newTargetFile = new File(newTargetFileName);
            IOHelpers.saveFile(newTargetFile, newText, StringUtil.UTF_8);
            logger.info("写入文件===========>:" + newTargetFileName);
        } 
    }

    private static String processFileString(Properties root, CharArrayWriter writer, Template template,
                                            String fileName) throws TemplateException, IOException {
        try {
            template.process(root, writer);
        } catch (Exception e) {
            logger.error(new StringBuffer("在制作: ").append(fileName).append(" 产生了错误: ").append(e.getMessage()).append(" \n").toString(), e);

            throw e;
        }
        return writer.toString();
    }

}
