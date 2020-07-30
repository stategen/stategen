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
package org.stategen.framework.progen;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
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
import org.stategen.framework.util.CommonComparetor;
import org.stategen.framework.util.StringUtil;
import org.yaml.snakeyaml.Yaml;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Cleanup;

/**
 * The Class FacadeGenerator.
 */
public class FacadeGenerator {
    
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FacadeGenerator.class);
    
    public void genFacades(Set<Class<?>> allcClasses, Properties root) throws IOException, TemplateException {
        
        String controlleSuffix = root.getProperty("controller_name_suffix");
        
        Set<String>                  apis           = new HashSet<String>();
        Map<String, CanbeImportWrap> destApiWrapMap = new HashMap<String, CanbeImportWrap>();
        
        for (Class<?> clz : allcClasses) {
            if (AnnotatedElementUtils.findMergedAnnotation(clz, Controller.class) != null) {
                String controllerName = clz.getSimpleName();
                String apiName        = StringUtil.trimRight(controllerName, controlleSuffix);
                
                AssertUtil.mustNotContainsAndAdd(apis, apiName, "不能存在2个相同的api:" + apiName + controlleSuffix);
                
                try {
                    ApiWrap controllerWrap = new ApiWrap(clz, apiName);
                    if (controllerWrap.isApi()) {
                        destApiWrapMap.put(apiName, controllerWrap);
                        if ("App".equals(controllerWrap.toString())) {
                            GenContext.appWrap = controllerWrap;
                        }
                    }
                } catch (Exception e) {
                    logger.error(new StringBuilder("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:\n").append(e.getMessage())
                            .append(apiName)
                            .append(" \n").toString(), e);
                    throw e;
                }
            }
        }
        
        //api 排序后再装入
        List<String> apiNames = new ArrayList<String>(destApiWrapMap.keySet());
        apiNames.sort(new CommonComparetor());
        Map<Class<?>, CanbeImportWrap> apiWrapMap = GenContext.wrapContainer.getCanbeImportWrapMapByPathType(PathType.API);
        for (String apiName : apiNames) {
            CanbeImportWrap apiWrap = destApiWrapMap.get(apiName);
            apiWrapMap.put(apiWrap.getClazz(), apiWrap);
        }
        
        AssertUtil.mustNotNull(GenContext.appWrap,
                "缺少AppController,必须有一个Controller为AppController,而且上面的标注@ApiConfig(menu = false),\n"
                        + "因为一些ajax Options的方法是统一从这个Controller中拿取");
        
        GenContext.wrapContainer.scanBeanRelationShipAndMakeFeilds();
        List<String> tempDirs = GenContext.tempDirs;
        
        String outDir = GenContext.outDir;
        
        String tempRootPath    = StringUtil.concatPath(GenContext.tempRootPath, "facade");
        String projectRootPath = GenContext.projectRootPath;
        
        Map<String, List<CanbeImportWrap>> canbeImportWrapMap = new HashMap<String, List<CanbeImportWrap>>();
        
        //统计生成的Files
        List<String> outWholeFiles = new ArrayList<String>();
        String       outPath       = FileHelpers.getFile(StringUtil.joinSLash(projectRootPath, outDir)).getAbsolutePath().replace("\\",
                "/");
        
        //读取yaml文件，放置frontendPagckageName，flutter不支持本包下绝对路径有点愚蠢
        String yamlFileName = StringUtil.concatPath(new File(outPath + "/../../").getCanonicalPath(), "pubspec.yaml");
        File   yamlFile     = new File(yamlFileName);
        if (yamlFile.exists() && yamlFile.isFile()) {
            Yaml                yaml                 = new Yaml();
            @Cleanup
            FileInputStream     yamlFileStream       = new FileInputStream(yamlFile);
            Map<String, String> map                  = yaml.load(yamlFileStream);
            String              frontendPagckageName = map.get("name");
            root.put("frontendPagckageName", "package:" + frontendPagckageName);
        }
        
        for (Entry<PathType, String> entry : GenContext.pathMap.entrySet()) {
            PathType pathType   = entry.getKey();
            String   importPath = entry.getValue();
            
            List<String> tempWholePaths = getTempPaths(tempDirs, tempRootPath, importPath, "dal");
            
            String reletiveOutWholePath = StringUtil.joinSLash(projectRootPath, outDir, importPath);
            String outWholePath         = FileHelpers.getFile(reletiveOutWholePath).getAbsolutePath();
            
            FreeMarkerTempFileContext beanFltContext = new FreeMarkerTempFileContext(tempWholePaths);
            Configuration             conf           = beanFltContext.getConf();
            conf.setDefaultEncoding(StringUtil.UTF_8);
            List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(conf, Arrays.asList("macro.include.ftl"));
            conf.setAutoIncludes(availableAutoInclude);
            
            Map<Class<?>, CanbeImportWrap> wrapMap = GenContext.wrapContainer.getCanbeImportWrapMapByPathType(pathType);
            canbeImportWrapMap.put(importPath, new ArrayList<CanbeImportWrap>(wrapMap.values()));
            
            for (CanbeImportWrap canbeImportWrap : wrapMap.values()) {
                String wrapName = pathType.getWrapName();
                root.put(wrapName, canbeImportWrap);
                for (FtlFile ftlFile : beanFltContext.getFtlFiles()) {
                    File   tempPathFile     = ftlFile.getTempPathFile();
                    File   tempFile         = ftlFile.getTempFile();
                    String relativeFileName = FileHelpers.getRelativeFileName(tempPathFile, tempFile);
                    processTemplate(tempFile, conf, root, outWholePath, relativeFileName, outWholeFiles);
                }
                root.remove(wrapName);
            }
        }
        
        //以下处理set文件夹模版循环问题
        //获取 生成的代码文件
        List<String> interFiles = new ArrayList<String>();
        List<String> pageFiles  = new ArrayList<String>();
        filterOutFiles(outWholeFiles, outPath, interFiles, pageFiles);
        
        interFiles.sort(new CommonComparetor());
        pageFiles.sort(new CommonComparetor());
        
        root.put("interFiles", interFiles);
        root.put("pageFiles", pageFiles);
        
        List<String>              tempWholePaths = getTempPaths(tempDirs, tempRootPath, "", "set");
        FreeMarkerTempFileContext beanFltContext = new FreeMarkerTempFileContext(tempWholePaths);
        
        String reletiveOutWholePath = StringUtil.joinSLash(projectRootPath, outDir);
        String outWholePath         = FileHelpers.getFile(reletiveOutWholePath).getAbsolutePath();
        
        Configuration conf = beanFltContext.getConf();
        conf.setDefaultEncoding(StringUtil.UTF_8);
        List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(conf, Arrays.asList("macro.include.ftl"));
        
        conf.setAutoIncludes(availableAutoInclude);
        root.putAll(canbeImportWrapMap);
        
        List<String> setOutFiles = new ArrayList<String>();
        for (FtlFile ftlFile : beanFltContext.getFtlFiles()) {
            File   tempPathFile     = ftlFile.getTempPathFile();
            File   tempFile         = ftlFile.getTempFile();
            String relativeFileName = FileHelpers.getRelativeFileName(tempPathFile, tempFile);
            processTemplate(tempFile, conf, root, outWholePath, relativeFileName, setOutFiles);
        }
    }
    
    private void filterOutFiles(List<String> outWholeFiles, String outPath, List<String> interFiles, List<String> pageFiles) {
        for (String wholeFile : outWholeFiles) {
            if (wholeFile.startsWith(outPath)) {
                String outFile = wholeFile.substring(outPath.length());
                interFiles.add(outFile);
            } else {
                String relativePath = FileHelpers.getRelativePath(outPath, wholeFile);
                pageFiles.add(relativePath);
            }
        }
    }
    
    private List<String> getTempPaths(List<String> tempDirs, String tempRootPath, String importPath, String subName) throws IOException {
        List<String> tempWholePaths = new ArrayList<String>(tempDirs.size() * 2);
        
        for (String tempDir : tempDirs) {
            String tempPath = StringUtil.joinSLash(tempDir, subName);
            if (StringUtil.isNotEmpty(importPath)) {
                tempPath = StringUtil.joinSLash(tempPath, importPath);
            }
            String tempWholePath = StringUtil.joinSLash(tempRootPath, tempPath);
            tempWholePaths.add(tempWholePath);
        }
        String sharedPath = FileHelpers.getCanonicalPath(StringUtil.joinSLash(tempRootPath, tempDirs.get(0), "share"));
        tempWholePaths.add(sharedPath);
        return tempWholePaths;
    }
    
    public static void processTemplate(
            File tempFile,
            Configuration conf,
            Properties root,
            String outputDir,
            String relativeFileName,
            List<String> outFiles) throws TemplateException, IOException {
        
        final String blobExt = "@blob";
        final String tempExt = "@temp";
        final String at      = "@";
        
        String targetFileName = TemplateHelpers.processTemplitePath(root, relativeFileName);
        if (StringUtil.isEmpty(targetFileName)) {
            targetFileName = StringUtil.SLASH;
        }
        
        String  notOverrideTargetFilenName = null;
        boolean doOverride                 = true;
        if (targetFileName.endsWith(tempExt)) {
            notOverrideTargetFilenName = targetFileName;
            targetFileName             = StringUtil.trimRight(targetFileName, tempExt);
            notOverrideTargetFilenName = FileHelpers.getCanonicalPath(outputDir, notOverrideTargetFilenName);
            notOverrideTargetFilenName = FileHelpers.replaceUnOverridePath(notOverrideTargetFilenName);
        } else if (targetFileName.endsWith(at)) {
            doOverride     = false;
            targetFileName = StringUtil.trimRight(targetFileName, at);
        }
        
        boolean isBlob = false;
        if (targetFileName.endsWith(blobExt)) {
            targetFileName = targetFileName.substring(0, targetFileName.length() - blobExt.length());
            isBlob         = true;
        }
        @Cleanup
        CharArrayWriter writer = new CharArrayWriter(1024 * 1024);
        //这一步就要判断，因为getCanonicalPath可能就不再含有@/
        
        if (FileHelpers.isUnOverridePath(tempFile.getCanonicalPath())) {
            doOverride = false;
        }
        
        String fullTargetFileName = FileHelpers.getCanonicalPath(outputDir, targetFileName);
        fullTargetFileName = FileHelpers.replaceUnOverridePath(fullTargetFileName);
        File fullTargetFile = FileHelpers.parentMkdir(fullTargetFileName);
        
        boolean isDirectory = fullTargetFile.isDirectory();
        if (!isDirectory && (isBlob || FileHelpers.isBinaryFile(fullTargetFileName))) {
            if (fullTargetFile.exists()) {
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuilder("二进制文件已存在，忽略写入:").append(fullTargetFile).toString());
                }
                return;
            }
            
            if (logger.isInfoEnabled()) {
                logger.info(new StringBuilder("写入二进制文件").append(fullTargetFile).toString());
            }
            Files.copy(tempFile.toPath(), fullTargetFile.toPath());
            return;
        } else {
            if (!isDirectory) {
                Template template = conf.getTemplate(relativeFileName, Locale.getDefault(), StringUtil.UTF_8);
                try {
                    processFileString(root, writer, template, targetFileName);
                } catch (TemplateException e) {
                    logger.error(new StringBuilder("写入文件出错, 模版:")
                            .append(tempFile.getCanonicalPath())
                            .append("\n目录文件:").append(fullTargetFileName)
                            .append(e.getMessage())
                            .append(" \n")
                            .toString(), e);
                    
                    throw e;
                }
                outFiles.add(fullTargetFileName);
            }
        }
        
        String newTargetFileName = fullTargetFileName;
        String newText           = writer.toString();
        
        if (!isDirectory) {
            if (fullTargetFile.exists() && fullTargetFile.isFile()) {
                if (!doOverride) {
                    logger.info(new StringBuilder("模版文件或文件夹以@或@.ftl结尾，当目标文件存在时，忽略写入:" + fullTargetFileName).append('\n').append(newText)
                            .toString());
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
    
    private static String processFileString(
            Properties root,
            CharArrayWriter writer,
            Template template,
            String fileName) throws TemplateException, IOException {
        try {
            template.process(root, writer);
        } catch (Exception e) {
            logger.error(new StringBuilder("在制作: ").append(fileName).append(" 产生了错误: ").append("，请先检查其对应的Controller上是否有编译错误，再检查模版文件错误\n").append(e.getMessage()).toString(), e);
            
            throw e;
        }
        return writer.toString();
    }
    
}
