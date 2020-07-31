/*
 * copy from rapid-framework<code.google.com/p/rapid-framework> and modify by niaoge
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
package cn.org.rapid_framework.generator;

import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_EXCLUDES;
import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_INCLUDES;
import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_OUTPUT_ENCODING;
import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_REMOVE_EXTENSIONS;
import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_SOURCE_ENCODING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stategen.framework.generator.util.FmHelper;
import org.stategen.framework.generator.util.GenProperties;
import org.stategen.framework.generator.util.TemplateHelpers;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.util.AntPathMatcher;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.FileHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.GeneratorException;
import cn.org.rapid_framework.generator.util.IOHelper;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.ZipUtils;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Cleanup;

/**
 * 代码生成器核心引擎 主要提供以下两个方法供外部使用
 * 
 * <pre>
 * generateBy() 用于生成文件
 * deleteBy() 用于删除生成的文件
 * </pre>
 * 
 * @author badqiu
 * @email badqiu(a)gmail.com
 */

@SuppressWarnings("all")
public class Generator {
    private static final String GENERATOR_INSERT_LOCATION       = "generator-insert-location";
    private ArrayList<File>     templateRootDirs                = new ArrayList<File>();
    private String              outRootDir;
    private boolean             ignoreTemplateGenerateException = true;
    private String              removeExtensions                = GeneratorProperties
                                                                        .getProperty(GENERATOR_REMOVE_EXTENSIONS);
    private boolean             isCopyBinaryFile                = true;

    private String              includes                        = GeneratorProperties
                                                                        .getProperty(GENERATOR_INCLUDES);         // 需要处理的模板，使用逗号分隔符,示例值: java_src/**,java_test/**
    private String              excludes                        = GeneratorProperties
                                                                        .getProperty(GENERATOR_EXCLUDES);         // 不需要处理的模板，使用逗号分隔符,示例值: java_src/**,java_test/**
    private String              sourceEncoding                  = GeneratorProperties
                                                                        .getProperty(GENERATOR_SOURCE_ENCODING);
    private String              outputEncoding                  = GeneratorProperties
                                                                        .getProperty(GENERATOR_OUTPUT_ENCODING);

    public Generator() {
    }

    public void setTemplateRootDir(File templateRootDir) {
        setTemplateRootDirs(new File[] { templateRootDir });
    }

    /**
     * 设置模板目录，支持用逗号分隔多个模板目录，如 template/rapid,template/company
     * 
     * @param templateRootDir
     */
    public void setTemplateRootDir(String templateRootDir) {
        setTemplateRootDirs(StringHelper.tokenizeToStringArray(templateRootDir, ","));
    }

    public void setTemplateRootDirs(File... templateRootDirs) {
        this.templateRootDirs = new ArrayList<File>(Arrays.asList(templateRootDirs));
    }

    public void setTemplateRootDirs(String... templateRootDirs) {
        ArrayList<File> tempDirs = new ArrayList<File>();
        for (String dir : templateRootDirs) {
            tempDirs.add(FileHelper.getFile(dir));
        }
        this.templateRootDirs = tempDirs;
    }

    public void addTemplateRootDir(File file) {
        templateRootDirs.add(file);
    }

    public void addTemplateRootDir(String file) {
        templateRootDirs.add(FileHelper.getFile(file));
    }

    public boolean isIgnoreTemplateGenerateException() {
        return ignoreTemplateGenerateException;
    }

    public void setIgnoreTemplateGenerateException(boolean ignoreTemplateGenerateException) {
        this.ignoreTemplateGenerateException = ignoreTemplateGenerateException;
    }

    public boolean isCopyBinaryFile() {
        return isCopyBinaryFile;
    }

    public void setCopyBinaryFile(boolean isCopyBinaryFile) {
        this.isCopyBinaryFile = isCopyBinaryFile;
    }

    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public void setSourceEncoding(String sourceEncoding) {
        if (StringUtil.isBlank(sourceEncoding))
            throw new IllegalArgumentException("sourceEncoding must be not empty");
        this.sourceEncoding = sourceEncoding;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        if (StringUtil.isBlank(outputEncoding))
            throw new IllegalArgumentException("outputEncoding must be not empty");
        this.outputEncoding = outputEncoding;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    /** 设置不处理的模板路径,可以使用ant类似的值,使用逗号分隔，示例值： **\*.ignore */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public void setOutRootDir(String rootDir) {
        if (rootDir == null)
            throw new IllegalArgumentException("outRootDir must be not null");
        this.outRootDir = rootDir;
    }

    public String getOutRootDir() {
        //		if(outRootDir == null) throw new IllegalStateException("'outRootDir' property must be not null.");
        return outRootDir;
    }

    public void setRemoveExtensions(String removeExtensions) {
        this.removeExtensions = removeExtensions;
    }

    public void deleteOutRootDir() throws IOException {
        if (StringUtil.isBlank(getOutRootDir()))
            throw new IllegalStateException("'outRootDir' property must be not null.");
        GLogger.println("[delete dir]    " + getOutRootDir());
        FileHelper.deleteDirectory(new File(getOutRootDir()));
    }

//    /**
//     * 生成文件
//     * 
//     * @param templateModel 生成器模板可以引用的变量
//     * @param filePathModel 文件路径可以引用的变量
//     * @throws Exception
//     */
//    public Generator generateBy(Map<String,Object> templateModel, Map<String, Object> filePathModel,boolean isTable) throws Exception {
//        processTemplateRootDirs(templateModel, filePathModel, false,isTable);
//        return this;
//    }
//
//    /**
//     * 删除生成的文件
//     * 
//     * @param templateModel 生成器模板可以引用的变量
//     * @param filePathModel 文件路径可以引用的变量
//     * @return
//     * @throws Exception
//     */
//    public Generator deleteBy(Map<String, Object> templateModel, Map<String, Object> filePathModel,boolean isTable) throws Exception {
//        processTemplateRootDirs(templateModel, filePathModel, true,isTable);
//        return this;
//    }

    public void processTemplateRootDirs(Map<String, Object> templateModel, Map<String, Object> filePathModel, boolean isDelete,boolean isTable)
            throws Exception {
        if (StringUtil.isBlank(getOutRootDir())) {
            throw new IllegalStateException("'outRootDir' property must be not empty.");
        }
        if (templateRootDirs == null || templateRootDirs.size() == 0) {
            throw new IllegalStateException("'templateRootDirs'  must be not empty");
        }

        GLogger.debug("******* Template reference variables *********", templateModel);
        GLogger.debug("\n\n******* FilePath reference variables *********", filePathModel);

        //生成 路径值,如 pkg=com.company.project 将生成 pkg_dir=com/company/project的值
        templateModel.putAll(StringHelper.getDirValuesMap(templateModel));
        filePathModel.putAll( StringHelper.getDirValuesMap(filePathModel));

        GeneratorException ge = new GeneratorException("generator occer error, Generator BeanInfo:"
                + BeanHelper.describe(this));
        List<File> processedTemplateRootDirs = processTemplateRootDirs();

        int processedTemplateRootDirsSize = processedTemplateRootDirs.size();
        for (int i = 0; i < processedTemplateRootDirsSize; i++) {
            File templateRootDir = (File) processedTemplateRootDirs.get(i);
            List<Exception> exceptions = scanTemplatesAndProcess(templateRootDir,
                    processedTemplateRootDirs, templateModel, filePathModel, isDelete,isTable);
            ge.addAll(exceptions);
        }
        if (!ge.exceptions.isEmpty())
            throw ge;
    }

    /**
     * 用于子类覆盖,预处理模板目录,如执行文件解压动作
     **/
    protected List<File> processTemplateRootDirs() throws Exception {
        return unzipIfTemplateRootDirIsZipFile();
    }

    /**
     * 解压模板目录,如果模板目录是一个zip,jar文件 . 并且支持指定 zip文件的子目录作为模板目录,通过 !号分隔 指定zip文件:
     * c:\\some.zip 指定zip文件子目录: c:\some.zip!/folder/
     * 
     * @throws MalformedURLException
     **/
    private List<File> unzipIfTemplateRootDirIsZipFile() throws MalformedURLException {
        List<File> unzipIfTemplateRootDirIsZipFile = new ArrayList<File>();
        int templateRootDirsSize = this.templateRootDirs.size();
        for (int i = 0; i < templateRootDirsSize; i++) {
            File file = templateRootDirs.get(i);
            String templateRootDir = FileHelper.toFilePathIfIsURL(file);

            String subFolder = "";
            int zipFileSeperatorIndexOf = templateRootDir.indexOf('!');
            if (zipFileSeperatorIndexOf >= 0) {
                subFolder = templateRootDir.substring(zipFileSeperatorIndexOf + 1);
                templateRootDir = templateRootDir.substring(0, zipFileSeperatorIndexOf);
            }

            if (new File(templateRootDir).isFile()) {
                File tempDir = ZipUtils.unzip2TempDir(new File(templateRootDir),
                        "tmp_generator_template_folder_for_zipfile");
                unzipIfTemplateRootDirIsZipFile.add(new File(tempDir, subFolder));
            } else {
                unzipIfTemplateRootDirIsZipFile.add(new File(templateRootDir, subFolder));
            }
        }
        return unzipIfTemplateRootDirIsZipFile;
    }

    /**
     * 搜索templateRootDir目录下的所有文件并生成东西
     * 
     * @param templateRootDir 用于搜索的模板目录
     * @param templateRootDirs freemarker用于装载模板的目录
     */
    private List<Exception> scanTemplatesAndProcess(File templateRootDir,
                                                    List<File> templateRootDirs, Map<String, Object> templateModel,
                                                    Map<String, Object> filePathModel, boolean isDelete,boolean isTable)
            throws Exception {
        if (templateRootDir == null)
            throw new IllegalStateException("'templateRootDir' must be not null");
        GLogger.println("[load template] \ntemplateRootDir = "
                + templateRootDir.getAbsolutePath() + "\noutRootDir:"
                + new File(outRootDir).getAbsolutePath());

        List<File> srcFiles = FileHelper.searchAllNotIgnoreFile(templateRootDir);

        List<Exception> exceptions = new ArrayList<>();
        int srcFilesSize = srcFiles.size();
        for (int i = 0; i < srcFilesSize; i++) {
            File srcFile = (File) srcFiles.get(i);
            try {
                if (isDelete) {
                    new TemplateProcessor(templateRootDirs).executeDelete(templateRootDir,
                            templateModel, filePathModel, srcFile);
                } else {
                    long start = System.currentTimeMillis();
                    new TemplateProcessor(templateRootDirs).executeGenerate(templateRootDir,
                            templateModel, filePathModel, srcFile,isTable);
                    GLogger.perf("genereate by tempate cost time:"
                            + (System.currentTimeMillis() - start) + "ms");
                }
            } catch (Exception e) {
                if (ignoreTemplateGenerateException) {
                    GLogger.warn("iggnore generate error,template is:" + srcFile + " cause:" + e);
                    exceptions.add(e);
                } else {
                    throw e;
                }
            }
        }
        return exceptions;
    }

    /**
     * 单个模板文件的处理器
     **/
    private class TemplateProcessor {
        private GeneratorControl gg               = new GeneratorControl();
        private List<File>       templateRootDirs = new ArrayList<File>();

        public TemplateProcessor(List<File> templateRootDirs) {
            super();
            this.templateRootDirs = templateRootDirs;
        }

        private void executeGenerate(File templateRootDir, Map<String, Object> templateModel, Map<String, Object> filePathModel,
                                     File srcFile,boolean isTable) throws Exception {
            String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
            if (GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, includes, excludes)) {
                return;
            }
            String outputFilePath = proceeForOutputFilepath(filePathModel, templateFile);

            final String dubbleEqu="/==";
            if (outputFilePath.indexOf(dubbleEqu)>0) {
               //如果不是当前daoType 的文件夹 则跳过
               if (outputFilePath.indexOf(dubbleEqu+GenProperties.daoType)<0) {
                   return ;
               } else {
                   outputFilePath =outputFilePath.replace(dubbleEqu+GenProperties.daoType, "");
               }
            }
            
            if (isCopyBinaryFile && FileHelper.isBinaryFile(srcFile)) {
                File outputFile = new File(getOutRootDir(), outputFilePath);
                GLogger.println("[copy binary file by extention] from:" + srcFile + " => "
                        + outputFile);
                FileHelper.parentMkdir(outputFile);
                @Cleanup
                FileInputStream fileInputStream = new FileInputStream(srcFile);
                @Cleanup
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                IOHelper.copy(fileInputStream, fileOutputStream);
                return;
            }
            
            initGeneratorControlProperties(srcFile, outputFilePath);
            processTemplateForGeneratorControl(templateModel, templateFile);

            if (gg.isIgnoreOutput()) {
                GLogger.println("[not generate] by gg.isIgnoreOutput()=true on template:"
                        + templateFile);
                return;
            }

            if (StringUtil.isNotBlank(gg.getOutputFile())) {
                generateNewFileOrInsertIntoFile(templateFile, gg.getOutputFile(), templateModel,isTable);
            }
        }

        /**
         * Execute delete.
         *
         * @param templateRootDir the template root dir
         * @param templateModel the template model
         * @param filePathModel the file path model
         * @param srcFile the src file
         * @throws SQLException the SQL exception
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws TemplateException the template exception
         */
        private void executeDelete(File templateRootDir, Map<String, Object> templateModel, Map<String, Object> filePathModel,
                                   File srcFile) throws SQLException, IOException,
                                                TemplateException {
            String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
            if (GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, includes, excludes)) {
                return;
            }
            String outputFilePath = proceeForOutputFilepath(filePathModel, templateFile);
            final String dubbleEqu="/==";
            if (outputFilePath.indexOf(dubbleEqu)>0) {
               //如果不是当前daoType
               if (outputFilePath.indexOf(dubbleEqu+GenProperties.daoType)<0) {
                   return ;
               } else {
                   outputFilePath =outputFilePath.replace(dubbleEqu+GenProperties.daoType, "");
               }
            }
            
            initGeneratorControlProperties(srcFile, outputFilePath);
            gg.deleteGeneratedFile = true;
            processTemplateForGeneratorControl(templateModel, templateFile);
            GLogger.println("[delete file] file:" + new File(gg.getOutputFile()).getAbsolutePath());
            new File(gg.getOutputFile()).delete();
        }

        private void initGeneratorControlProperties(File srcFile, String outputFile)
                throws SQLException {
            gg.setSourceFile(srcFile.getAbsolutePath());
            gg.setSourceFileName(srcFile.getName());
            gg.setSourceDir(srcFile.getParent());
            gg.setOutRoot(getOutRootDir());
            gg.setOutputEncoding(outputEncoding);
            gg.setSourceEncoding(sourceEncoding);
            gg.setMergeLocation(GENERATOR_INSERT_LOCATION);
            gg.setOutputFile(outputFile);
        }

        private void processTemplateForGeneratorControl(Map<String, Object> templateModel, String templateFile)
                                                                                               throws IOException,
                                                                                               TemplateException {
            templateModel.put("gg", gg);
            Template template = getFreeMarkerTemplate(templateFile);
            template.process(templateModel, IOHelper.NULL_WRITER);
        }

        /** 处理文件路径的变量变成输出路径 */
        private String proceeForOutputFilepath(Map<String, Object> filePathModel, String templateFile)
                throws IOException {
            String outputFilePath = templateFile;

            //TODO 删除兼容性的@testExpression
//            int testExpressionIndex = -1;
//            if ((testExpressionIndex = templateFile.indexOf('@')) != -1) {
//                outputFilePath = templateFile.substring(0, testExpressionIndex);
//                String testExpressionKey = templateFile.substring(testExpressionIndex + 1);
//                Object expressionValue = filePathModel.get(testExpressionKey);
//                if (expressionValue == null) {
//                    System.err.println("[not-generate] WARN: test expression is null by key:["
//                            + testExpressionKey + "] on template:[" + templateFile + "]");
//                    return null;
//                }
//                if (!"true".equals(String.valueOf(expressionValue))) {
//                    GLogger.println("[not-generate]\t test expression '@" + testExpressionKey
//                            + "' is false,template:" + templateFile);
//                    return null;
//                }
//            }

            for (String removeExtension : removeExtensions.split(",")) {
                if (outputFilePath.endsWith(removeExtension)) {

                    outputFilePath = StringUtil.trimSubfix(outputFilePath,removeExtension);
                    break;
                }
            }
            Configuration conf = GeneratorHelper.newFreeMarkerConfiguration(templateRootDirs,
                    sourceEncoding, "/filepath/processor/");

            //使freemarker支持过滤,如 ${className?lower_case} 现在为 ${className^lower_case}

            outputFilePath =TemplateHelpers.replaceSpacialPathWords(outputFilePath);
            String result =FmHelper.processTemplateString(outputFilePath, filePathModel,
                conf);

            return result;
        }

        private Template getFreeMarkerTemplate(String templateName) throws IOException {
            return GeneratorHelper.newFreeMarkerConfiguration(templateRootDirs, sourceEncoding,
                    templateName).getTemplate(templateName);
        }

        private void generateNewFileOrInsertIntoFile(String templateFile, String outputFilePath,
                                                     Map<String, Object> templateModel,boolean isTable) throws Exception {
            Template template = getFreeMarkerTemplate(templateFile);
            template.setOutputEncoding(gg.getOutputEncoding());
            boolean hasAtNotRelace = false;
            if (outputFilePath.contains("@")){
                outputFilePath =outputFilePath.replaceAll("@", "");
                hasAtNotRelace =true;
            }
            
            File absoluteOutputFilePath = FileHelper.parentMkdir(outputFilePath);
            
            if (absoluteOutputFilePath.exists()) {
                @Cleanup
                StringWriter newFileContentCollector = new StringWriter();
                if (GeneratorHelper.isFoundInsertLocation(gg, template, templateModel,
                        absoluteOutputFilePath, newFileContentCollector)) {
                    GLogger.println("[insert]\t generate content into:" + outputFilePath);
                    IOHelper.saveFile(absoluteOutputFilePath, newFileContentCollector.toString(),
                            gg.getOutputEncoding());
                    return;
                }
            }

            if (absoluteOutputFilePath.exists() && !gg.isOverride()) {
                GLogger.println("[not generate]\t by gg.isOverride()=false and outputFile exist:"
                        + outputFilePath);
                return;
            }

            if (absoluteOutputFilePath.exists()) {
                GLogger.println("[override] template:\n" + templateFile + "\n===> " + outputFilePath);
            } else {
                GLogger.println("[generate] template:\n" + templateFile + "\n===> " + outputFilePath);
            }
            FmHelper.processTemplate(template, templateModel, absoluteOutputFilePath,
                    gg.getOutputEncoding(),isTable, hasAtNotRelace);
        }
    }

    static class GeneratorHelper {

        public static boolean isIgnoreTemplateProcess(File srcFile, String templateFile,
                                                      String includes, String excludes) {
            if (srcFile.isDirectory() || srcFile.isHidden())
                return true;
            if (templateFile.trim().equals(""))
                return true;
            if (srcFile.getName().toLowerCase().endsWith(".include")) {
                GLogger.println("[skip]\t\t endsWith '.include' template:" + templateFile);
                return true;
            }
            if (srcFile.getName().toLowerCase().endsWith(".include.ftl")) {
                GLogger.println("[skip]\t\t endsWith '.include' template:" + templateFile);
                return true;
            }
            templateFile = templateFile.replace('\\', '/');
            for (String exclude : StringHelper.tokenizeToStringArray(excludes, ",")) {
                if (new AntPathMatcher().match(exclude.replace('\\', '/'), templateFile))
                    return true;
            }
            if (StringUtil.isBlank(includes)) {
                return false;
            }
            for (String include : StringHelper.tokenizeToStringArray(includes, ",")) {
                if (new AntPathMatcher().match(include.replace('\\', '/'), templateFile))
                    return false;
            }
            return true;
        }

        private static boolean isFoundInsertLocation(GeneratorControl gg, Template template,
                                                     Map<String, Object> model, File outputFile,
                                                     StringWriter newFileContent)
                throws IOException, TemplateException {
            @Cleanup
            LineNumberReader reader = new LineNumberReader(new FileReader(outputFile));
            String line = null;
            boolean isFoundInsertLocation = false;

            //FIXME 持续性的重复生成会导致out of memory
            @Cleanup
            PrintWriter writer = new PrintWriter(newFileContent);
            while ((line = reader.readLine()) != null) {
                writer.println(line);
                // only insert once
                if (!isFoundInsertLocation && line.indexOf(gg.getMergeLocation()) >= 0) {
                    template.process(model, writer);
                    writer.println();
                    isFoundInsertLocation = true;
                }
            }

            return isFoundInsertLocation;
        }

        public static Configuration newFreeMarkerConfiguration(List<File> templateRootDirs,
                                                               String defaultEncoding,
                                                               String templateName)
                throws IOException {
            Configuration conf = new Configuration();
            int templateRootDirsSize = templateRootDirs.size();
            FileTemplateLoader[] templateLoaders = new FileTemplateLoader[templateRootDirsSize];
            for (int i = 0; i < templateRootDirsSize; i++) {
                templateLoaders[i] = new FileTemplateLoader((File) templateRootDirs.get(i));
            }
            MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);

            conf.setTemplateLoader(multiTemplateLoader);
            conf.setNumberFormat("###############");
            conf.setBooleanFormat("true,false");
            conf.setDefaultEncoding(defaultEncoding);

            final String macro_include_ftl ="macro.include.ftl";
            String autoIncludes = new File(new File(templateName).getParent(),macro_include_ftl).getPath();
            List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(conf, Arrays.asList(macro_include_ftl,autoIncludes));
            conf.setAutoIncludes(availableAutoInclude);
            GLogger.info("[set Freemarker.autoIncludes]"+availableAutoInclude+" for templateName:"+templateName);

//            GLogger.info(templateName);
//            List<String> autoIncludes = getParentPaths(templateName, "macro.include.ftl");
//            GLogger.info(autoIncludes.toString());
//            List<String> availableAutoInclude = TemplateHelpers.getAvailableAutoInclude(
//                conf, autoIncludes);
            conf.setAutoIncludes(availableAutoInclude);

            GLogger.trace("set Freemarker.autoIncludes:" + availableAutoInclude
                    + " for templateName:" + templateName + " autoIncludes:" + autoIncludes);
            return conf;
        }

        public static List<String> getParentPaths(String templateName, String suffix) {
            String array[] = StringHelper.tokenizeToStringArray(templateName, "\\/");
            List<String> list = new ArrayList<String>();
            list.add(suffix);
            list.add(File.separator + suffix);
            String path = "";
            for (int i = 0; i < array.length; i++) {
                path = path + File.separator + array[i];
                list.add(path + File.separator + suffix);
            }
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    public static class GeneratorModel implements java.io.Serializable {
        private static final long serialVersionUID = -6430787906037836995L;
        /** 用于存放'模板'可以引用的变量 */
        public Map<String, Object>                templateModel    = new HashMap<>();
        /** 用于存放'文件路径'可以引用的变量 */
        public Map<String, Object>                filePathModel    = new HashMap<>();

        public GeneratorModel() {
        }

        public GeneratorModel(Map<String, Object> templateModel, Map<String, Object> filePathModel) {
            this.templateModel = templateModel;
            this.filePathModel = filePathModel;
        }

    }
}
