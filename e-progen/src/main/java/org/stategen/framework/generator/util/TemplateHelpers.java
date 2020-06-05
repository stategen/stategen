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

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stategen.framework.util.StringUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * The Class TemplateHelpers.
 */
public class TemplateHelpers {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TemplateHelpers.class);
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<String> getAvailableAutoInclude(Configuration conf, List<String> autoIncludes) {
        List<String> results = new ArrayList();
        for (String autoInclude : autoIncludes) {
            autoInclude =autoInclude.replace(StringUtil.BACK_SLASH, StringUtil.SLASH);
            try {
                Template t = new Template("__auto_include_test__", new StringReader("1"), conf);
                conf.setAutoIncludes(Arrays.asList(new String[] { autoInclude }));
                t.process(new HashMap(), new StringWriter());
                results.add(autoInclude);
            } catch (Exception e) {

            }
        }
        return results;
    }


    public static Configuration getConfiguration(String... tempRoots) throws IOException {
        List<File> templateRootDirs = new ArrayList<File>(tempRoots.length);
        for (String fileName : tempRoots) {
            File file = FileHelpers.getFile(fileName);
            if (file.exists() && file.isDirectory()){
              templateRootDirs.add(file);
            }
        }

        Configuration conf = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        FileTemplateLoader[] templateLoaders = new FileTemplateLoader[templateRootDirs.size()];
        for (int i = 0; i < templateRootDirs.size(); i++) {
            templateLoaders[i] = new FileTemplateLoader((File) templateRootDirs.get(i));
        }
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);

        conf.setTemplateLoader(multiTemplateLoader);

        return conf;
    }

    public static String processTemplitePath(Map<Object, Object> root, String dest) throws IOException, TemplateException {
        Configuration conf =new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        final String stringTemplate = dest;
        dest=dest.replace(StringUtil.BACK_SLASH, StringUtil.SLASH);
        dest=dest.replace("[", "<");
        dest=dest.replace("]", ">");
        dest=dest.replace("<%", "</");
        dest=dest.replace("^", "?");
        dest=dest.replace("${..}", "..");
        dest=dest.replace("${.}", "");
        //可以用 ^ 代替
        dest=dest.replace("${q}", "?");
        
        /* 在整个应用的生命周期中，这个工作你应该只做一次。 */
        /* 创建和调整配置。 */
        
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(stringTemplate, dest);
        conf.setTemplateLoader(stringLoader);
        /* 创建数据模型 */
        Writer out=null;
        String result =null;
        try {
            Template temp = conf.getTemplate(stringTemplate, StringUtil.UTF_8);
            out = new CharArrayWriter(2048);
            temp.process(root, out);
            out.flush();
            result =out.toString();
        } catch (Exception e) {
            logger.error(
                new StringBuilder("解析字符串出错:").append(dest).append("\n")
                .append(e.getMessage()).append(" \n").toString(), e);

            throw e;
        }
        result =StringUtil.trimRight(result,".ftl");
        return result;
    }

}
