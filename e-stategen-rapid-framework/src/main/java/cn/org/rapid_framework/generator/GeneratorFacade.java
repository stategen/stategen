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

import static cn.org.rapid_framework.generator.GeneratorConstants.GENERATOR_TOOLS_CLASS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.Generator.GeneratorModel;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.provider.db.table.TableFactory;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.provider.java.model.JavaClass;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.ClassHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.GeneratorException;

import lombok.Cleanup;

/**
 * 代码生成器的主要入口类,包装相关方法供外部生成代码使用 使用GeneratorFacade之前，需要设置Generator的相关属性
 * 
 * @author badqiu
 */
@SuppressWarnings("all")
public class GeneratorFacade {
    private Generator generator = new Generator();

    public GeneratorFacade() {
        if (StringUtil.isNotBlank(GeneratorProperties.getProperty("outRoot"))) {
            generator.setOutRootDir(GeneratorProperties.getProperty("outRoot"));
        }
    }

    public static void printAllTableNames() throws Exception {
        PrintUtils.printAllTableNames(TableFactory.getInstance().getAllTables());
    }

    public void deleteOutRootDir() throws IOException {
        generator.deleteOutRootDir();
    }

    /**
     * 自定义变量，生成文件,文件路径与模板引用的变量相同
     * 
     * @throws Exception
     */
    public void generateByMap(Map... maps) throws Exception {
        for (Map<String, Object> map : maps) {
            new ProcessUtils().processByMap(map, false);
        }
    }

    /**
     * 自定义变量，删除生成的文件,文件路径与模板引用的变量相同
     * 
     * @throws Exception
     */
    public void deleteByMap(Map... maps) throws Exception {
        for (Map<String, Object> map : maps) {
            new ProcessUtils().processByMap(map, true);
        }
    }

    /**
     * 自定义变量，生成文件,可以自定义文件路径与模板引用的变量
     * 
     * @throws Exception
     */
    public void generateBy(GeneratorModel... models) throws Exception {
        for (GeneratorModel model : models) {
            new ProcessUtils().processByGeneratorModel(model, false);
        }
    }

    /**
     * 自定义变量，删除生成的文件,可以自定义文件路径与模板引用的变量
     * 
     * @throws Exception
     */
    public void deleteBy(GeneratorModel... models) throws Exception {
        for (GeneratorModel model : models) {
            new ProcessUtils().processByGeneratorModel(model, true);
        }
    }

    /**
     * 根据Table生成文件,模板引用的变量名称为: table, 实体类为: @see
     * cn.org.rapid_framework.generator.provider.db.table.model.Table
     * 
     * @throws Exception
     */
    public void generateByTable(TableConfigSet tableConfigSet,String... tableNames) throws Exception {
        for (String tableName : tableNames) {
            new ProcessUtils().processByTable(tableName, false,tableConfigSet);
        }
    }

    /**
     * 根据Table删除生成的文件,模板引用的变量名称为: table 实体类为:
     * cn.org.rapid_framework.generator.provider.db.table.model.Table
     * 
     * @throws Exception
     */
    public void deleteByTable(TableConfigSet tableConfigSet,String... tableNames) throws Exception {
        for (String tableName : tableNames) {
            new ProcessUtils().processByTable(tableName, true,tableConfigSet);
        }
    }

    /**
     * 根据Class生成文件,模板引用的变量名称为: clazz 实体类为:
     * cn.org.rapid_framework.generator.provider.java.model.JavaClass
     */
    public void generateByClass(Class... clazzes) throws Exception {
        for (Class<?> clazz : clazzes) {
            new ProcessUtils().processByClass(clazz, false);
        }
    }

    /**
     * 根据Class删除生成的文件,模板引用的变量名称为: clazz 实体类为:
     * cn.org.rapid_framework.generator.provider.java.model.JavaClass
     */
    public void deleteByClass(Class... clazzes) throws Exception {
        for (Class<?> clazz : clazzes) {
            new ProcessUtils().processByClass(clazz, true);
        }
    }

    /**
     * 根据Sql生成文件,模板引用的变量名称为: sql
     */
    public void generateBySql(Sql... sqls) throws Exception {
        for (Sql sql : sqls) {
            new ProcessUtils().processBySql(sql, false);
        }
    }

    /**
     * 根据Sql删除生成的文件,模板引用的变量名称为: sql
     */
    public void deleteBySql(Sql... sqls) throws Exception {
        for (Sql sql : sqls) {
            new ProcessUtils().processBySql(sql, true);
        }
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public class ProcessUtils {

        public void processByGeneratorModel(GeneratorModel model, boolean isDelete)
                throws Exception, FileNotFoundException {
            Generator g = getGenerator();

            GeneratorModel targetModel = GeneratorModelUtils.newDefaultGeneratorModel();
            targetModel.filePathModel.putAll(model.filePathModel);
            targetModel.templateModel.putAll(model.templateModel);
            processByGeneratorModel(isDelete, g, targetModel);
        }

        public void processByMap(Map<String, Object> params, boolean isDelete) throws Exception,
                FileNotFoundException {
            Generator g = getGenerator();
            GeneratorModel m = GeneratorModelUtils.newFromMap(params);
            processByGeneratorModel(isDelete, g, m);
        }

        public void processBySql(Sql sql, boolean isDelete) throws Exception {
            Generator g = getGenerator();
            GeneratorModel m = GeneratorModelUtils.newGeneratorModel("sql", sql);
            PrintUtils.printBeginProcess("sql:" + sql.getSourceSql(), isDelete);
            processByGeneratorModel(isDelete, g, m);
        }

        public void processByClass(Class<?> clazz, boolean isDelete) throws Exception,
                FileNotFoundException {
            Generator g = getGenerator();
            GeneratorModel m = GeneratorModelUtils.newGeneratorModel("clazz", new JavaClass(clazz));
            PrintUtils.printBeginProcess("JavaClass:" + clazz.getSimpleName(), isDelete);
            processByGeneratorModel(isDelete, g, m);
        }

        private void processByGeneratorModel(boolean isDelete, Generator g, GeneratorModel m)
                throws Exception, FileNotFoundException {
            try {
                generator.processTemplateRootDirs(m.templateModel, m.filePathModel, isDelete,false);
            } catch (GeneratorException ge) {
                PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator().getOutRootDir(),
                        ge.getExceptions());
                throw ge;
            }
        }

        public void processByTable(String tableName, boolean isDelete,TableConfigSet tableConfigSet) throws Exception {
            if ("*".equals(tableName)) {
                new ProcessUtils().processByAllTable(isDelete,tableConfigSet);
                return;
            }
            Generator g = getGenerator();
            Table table = TableFactory.getInstance().getTable(tableName);
            try {
                processByTable(g, table, isDelete,tableConfigSet);
            } catch (GeneratorException ge) {
                PrintUtils.printExceptionsSumary(ge.getMessage(), getGenerator().getOutRootDir(),
                        ge.getExceptions());
                throw ge;
            }
        }

        public void processByAllTable(boolean isDelete,TableConfigSet tableConfigSet) throws Exception {
            List<Table> tables = TableFactory.getInstance().getAllTables();
            List<Exception> exceptions = new ArrayList<>();
            int tablesSize = tables.size();
            for (int i = 0; i < tablesSize; i++) {
                try {
                    processByTable(getGenerator(), tables.get(i), isDelete,tableConfigSet);
                } catch (GeneratorException ge) {
                    exceptions.addAll(ge.getExceptions());
                }
            }
            PrintUtils.printExceptionsSumary("", getGenerator().getOutRootDir(), exceptions);
            if (!exceptions.isEmpty()) {
                throw new GeneratorException("batch generate by all table occer error", exceptions);
            }

        }

        public void processByTable(Generator g, Table table, boolean isDelete,TableConfigSet tableConfigSet) throws Exception {
            GeneratorModel m = GeneratorModelUtils.newGeneratorModel("table", table);
            PrintUtils.printBeginProcess(table.getSqlName() + " => " + table.getClassName(),
                    isDelete);
            m.templateModel.put("tableConfigSet", tableConfigSet);
            generator.processTemplateRootDirs(m.templateModel, m.filePathModel, isDelete,true);
        }
    }

    public static class GeneratorModelUtils {

        public static GeneratorModel newGeneratorModel(String key, Object valueObject) {
            GeneratorModel gm = newDefaultGeneratorModel();
            gm.templateModel.put(key, valueObject);
            gm.filePathModel.putAll(BeanHelper.describe(valueObject));
            return gm;
        }

        public static GeneratorModel newFromMap(Map<String, Object> params) {
            GeneratorModel gm = newDefaultGeneratorModel();
            gm.templateModel.putAll(params);
            gm.filePathModel.putAll(params);
            return gm;
        }

        public static GeneratorModel newDefaultGeneratorModel() {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.putAll(getShareVars());

            Map<String, Object> filePathModel = new HashMap<>();
            filePathModel.putAll(getShareVars());
            return new GeneratorModel(templateModel, filePathModel);
        }

        public static Map<String, Object> getShareVars() {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.putAll((Map)System.getProperties());
            templateModel.putAll((Map)GeneratorProperties.getProperties());
            templateModel.put("env", System.getenv());
            templateModel.put("now", new Date());
            templateModel.put(GeneratorConstants.DATABASE_TYPE.code,
                    GeneratorProperties.getDatabaseType(GeneratorConstants.DATABASE_TYPE.code));
            templateModel.putAll(GeneratorContext.getContext());
            templateModel.putAll(getToolsMap());
            return templateModel;
        }

        /** 得到模板可以引用的工具类 */
        private static Map<String, Object> getToolsMap() {
            Map<String, Object> toolsMap = new HashMap<>();
            String[] tools = GeneratorProperties.getStringArray(GENERATOR_TOOLS_CLASS);
            for (String className : tools) {
                try {
                    Object instance = ClassHelper.newInstance(className);
                    toolsMap.put(Class.forName(className).getSimpleName(), instance);
                    GLogger.debug("put tools class:" + className + " with key:"
                            + Class.forName(className).getSimpleName());
                } catch (Exception e) {
                    GLogger.error("cannot load tools by className:" + className + " cause:" + e);
                }
            }
            return toolsMap;
        }

    }

    private static class PrintUtils {

        private static void printExceptionsSumary(String msg, String outRoot,
                                                  List<Exception> exceptions)
                throws FileNotFoundException ,IOException{
            File errorFile = new File(outRoot, "generator_error.log");
            if (CollectionUtil.isNotEmpty(exceptions)) {
                int exceptionsSize = exceptions.size();
                System.err.println("[Generate Error Summary] : " + msg);
                errorFile.getParentFile().mkdirs();
                @Cleanup
                FileOutputStream fileOutputStream = new FileOutputStream(errorFile);
                @Cleanup
                PrintStream output = new PrintStream(fileOutputStream);
                for (int i = 0; i < exceptionsSize; i++) {
                    Exception e = exceptions.get(i);
                    System.err.println("[GENERATE ERROR]:" + e);
                    if (i == 0)
                        e.printStackTrace();
                    e.printStackTrace(output);
                }
                System.err
                        .println("***************************************************************");
                System.err.println("* " + "* 输出目录已经生成generator_error.log用于查看错误 ");
                System.err
                        .println("***************************************************************");
            }
        }

        private static void printBeginProcess(String displayText, boolean isDatele) {
            GLogger.println("***************************************************************");
            GLogger.println("* BEGIN " + (isDatele ? " delete by " : " generate by ") + displayText);
            GLogger.println("***************************************************************");
        }

        public static void printAllTableNames(List<Table> tables) throws Exception {
            GLogger.println("\n----All TableNames BEGIN----");
            int tablesSize = tables.size();
            for (int i = 0; i < tablesSize; i++) {
                String sqlName = ((Table) tables.get(i)).getSqlName();
                GLogger.println(sqlName);
            }
            GLogger.println("----All TableNames END----");
        }
    }

}
