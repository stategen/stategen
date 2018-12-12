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
package org.stategen.framework.generator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.stategen.framework.lite.CaseInsensitiveHashMap;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.Consts;
import org.stategen.framework.util.PropUtil;
import org.stategen.framework.util.Setting;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.GenUtils;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.provider.db.sql.model.SqlParameter;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.util.GLogger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The Class FmHelper.
 */
@SuppressWarnings("all")
public class FmHelper {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FmHelper.class);

    /**
     * Gets the available auto include.
     *
     * @param conf the conf
     * @param autoIncludes the auto includes
     * @return the available auto include
     */


    protected static JavaType getJavaType(String outFileName) {
        if (Consts.dao.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getDaoSuffixJava())) {
            return JavaType.isDao;
        }

        if (Consts.dao.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getDaoImplSuffixJava())) {
            return JavaType.isDaoImpl;
        }

        if (Consts.service.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getServiceSuffixJava())) {
            return JavaType.isService;
        }
        
        if (Consts.service.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getServiceSuffixInternalJava())) {
            return JavaType.isServiceInternal;
        }        
        
        if (Consts.service.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getServiceImplSuffixJava())) {
            return JavaType.isServiceImpl;
        }


        if (Consts.controller.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getControllerSuffixJava())) {
            return JavaType.isController;
        }
        

        if (Consts.controller.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getControllerSuffixBaseJava())) {
            return JavaType.isControllerBase;
        }        

        if (Consts.pojo.equals(Setting.current_gen_name)){
            return JavaType.isEntry;
        }
        
        
        return JavaType.isEntry;
    }

    public static void readTxtFile(String filePath) {
        try {
            File file = new File(filePath);
            //判断文件是否存在
            if (file.isFile() && file.exists()) {
                //考虑到编码格式
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), StringUtil.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    GLogger.info(lineTxt);
                }
                read.close();
            } else {
                GLogger.error("找不到指定的文件");
            }
        } catch (Exception e) {
            GLogger.error("读取文件内容出错");
            e.printStackTrace();
        }
    }
    
    protected static void changeCaseColumnName(LinkedHashSet<Column> columns,Map<String, String> oldFieldMap){
        if (CollectionUtil.isEmpty(columns)){
            return;
        }
        
        Map<String, Column> newFieldMap =new CaseInsensitiveHashMap<Column>();
        
        for (Column column : columns) {
            String destColumnName =column.getColumnName();
            newFieldMap.put(destColumnName, column);
        }
        
        
        //替换collumnName的大小写
        if (CollectionUtil.isNotEmpty(oldFieldMap)){
            for (Entry<String, String> entry :oldFieldMap.entrySet()){
                String newColumnName = entry.getValue();
                Column column =newFieldMap.get(newColumnName);
                if (column!=null){
                    column.setColumnName(newColumnName);
                }
            }
        }
    }

    /**
     * Process template.
     *
     * @param template the template
     * @param model the model
     * @param outputFile the output file
     * @param encoding the encoding
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TemplateException the template exception
     * @throws InstantiationException the instantiation exception
     * @throws IllegalAccessException the illegal access exception
     * @throws ParseException 
     */
    public static void processTemplate(Template template, Map model, File outputFile,
                                       String encoding,boolean isTable) throws IOException, TemplateException,
                                                       InstantiationException,
                                                       IllegalAccessException, ParseException {

        //可以同时用来判断是否需要做java文件
        CompilationUnit lastCompilationUnit = null;
        JavaType javaType = null;
        String outFileName = outputFile.getName();
        File canonicalFile;
        try {
            canonicalFile = outputFile.getCanonicalFile();
        } catch (Exception e1) {
            GLogger.error(new StringBuffer("如果参数内设置 add_illegal_prefix 那么生成的 className 将有一个 '?'字符以阻止dal层生成,目的是让你先检测类名是否符合要求,\n")
            .append("比如驼峰写法，比如可以避免windows不区分文件名的大小写的麻烦,\n请先检查tables内相应的xml文件:\n").append(e1.getMessage()).append(" \n")
                .toString(), e1);
            throw e1;
        }
        boolean notReplacefile = "false".equals(GeneratorProperties.getProperties().get(
            "replace_file"));
        boolean isFileExits=false;
        if (canonicalFile != null && canonicalFile.exists() && canonicalFile.isFile()) {
            isFileExits=true;
            if (notReplacefile) {
                GLogger.info(">>>>>>>>>>>>>文件已存在，不再次生成:" + canonicalFile.getCanonicalFile());
                CharArrayWriter charArrayWriter = new CharArrayWriter(1024 * 1024);
                template.process(model, charArrayWriter);
                GLogger.info(""+charArrayWriter);
                return;
            }
            //如果文件存在，并且是DTO.java,将不覆盖
            if (outFileName.endsWith(PropUtil.getDTOSuffixJava())) {
                GLogger.info("DTO文件已存在，将不会覆盖:" + canonicalFile);
                return;
            }

            if (outFileName.endsWith(".java")) {
                javaType = getJavaType(outFileName);
                GLogger.info(javaType+"<===========>:" + outFileName);
                try {
                    lastCompilationUnit = JavaParser.parse(canonicalFile, Charset.forName(StringUtil.UTF_8));
                } catch (Exception e) {
                    String javaFileText = IOHelpers.readFile(canonicalFile,StringUtil.UTF_8,true);
                    GLogger.error(
                        new StringBuffer("java文件:").append(canonicalFile).append(" \n").append(javaFileText).toString(),
                        e);
                    throw e;
                }
            }
        }

        CharArrayWriter charArrayWriter = new CharArrayWriter(1024 * 1024);
        BufferedWriter bufferedWriter =null;
        boolean newFileError = false;
        if (JavaType.isEntry.equals(javaType)) {
            if (lastCompilationUnit != null && notReplacefile) {
                return;
            }

            Map<String, String> oldFieldMap = ASTHelper.getFieldMap(lastCompilationUnit);
            CompatibleHelper.OLD_FIELDS.clear();
            if (CollectionUtil.isNotEmpty(oldFieldMap)) {
                CompatibleHelper.OLD_FIELDS.putAll(oldFieldMap);
            }
            
            //替换为pojo的大小写格式
            Table table = GenUtils.globalTableConfig.getTable();
            LinkedHashSet<Column> columns = table.getColumns();
            changeCaseColumnName(columns, oldFieldMap);
            
            List<Sql> sqls = GenUtils.globalTableConfig.getSqls();
            //替换参数的大小写
            if (CollectionUtil.isNotEmpty(sqls)){
                for (Sql sql : sqls) {
                    changeCaseColumnName(sql.getColumns(), oldFieldMap);
                    LinkedHashSet<SqlParameter> params = sql.getParams(); 
                    
                    if (CollectionUtil.isNotEmpty(params)){
                        for (SqlParameter sqlParameter : params) {
                            String destColumnName =sqlParameter.getParamName();
                            String newColumnName = oldFieldMap.get(destColumnName);
                            if (newColumnName!=null){
                                sqlParameter.setParamName(newColumnName);
                            }
                        }
                    }
                }
            }
        }

        try {
            //如果是java文件,将与原来的java文件对比，如果有旧的方法及属性将保留
            try {
                template.process(model, charArrayWriter);
                if (lastCompilationUnit != null) {
                    ASTHelper.replaceJava(lastCompilationUnit, javaType, charArrayWriter,canonicalFile.getName());
                }
            } catch (ParseException e) {
                GLogger.error(
                    "将要生成的文件: " + canonicalFile.getName() + "........有个解析错误，文件将不会被覆盖，除非你删除或修复该文件"
                            + "\n" + charArrayWriter.toString(), e);
                newFileError = true;
                return;
            }
            
            
            if (lastCompilationUnit == null || !newFileError) {
                //如果是table xml文件，如果文件已存在
               
                if (isTable && isFileExits && StringUtil.endsWithIgnoreCase(outFileName, ".xml")) {
                        GLogger.info("====>table文件已存在，将不会覆盖，下面是table的xml文件，如果需要，请自行拷贝,\n"+canonicalFile);
                        GLogger.info(charArrayWriter.toString());
                    return;
                }     
                
                if (isFileExits){
                    String fileText =IOHelpers.readFile(canonicalFile,StringUtil.UTF_8,false);
                    String newText =charArrayWriter.toString();
                    if (fileText.equals(newText)){
                        GLogger.info("智能忽略重写入----------------->:"+canonicalFile.getName());
                        return;
                    }
                }
                
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    canonicalFile), encoding));
                charArrayWriter.writeTo(bufferedWriter);
            }
            
        } finally {
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            charArrayWriter.close();
        }
    }
    

    /**
     * Process template string.
     *
     * @param templateString the template string
     * @param model the model
     * @param conf the conf
     * @return the string
     */
    public static String processTemplateString(String templateString, Map model, Configuration conf) {
        StringWriter out = new StringWriter();
        try {
            Template template = new Template("templateString...", new StringReader(templateString),
                conf);
            template.process(model, out);
            return out.toString();
        } catch (Exception e) {
            throw new IllegalStateException("cannot process templateString:" + templateString
                                            + " cause:" + e, e);
        }
    }
}
