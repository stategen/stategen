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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.servlet.ModelAndView;
import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.PropertiesHelpers;
import org.stategen.framework.util.BusinessAssert;
import org.stategen.framework.util.CollectionUtil;

import freemarker.template.TemplateException;

/**
 * The Class BaseGenFacadeProcessor.
 */
public class BaseGenFacadeProcessor {
    final static Logger logger =  LoggerFactory.getLogger(BaseGenFacadeProcessor.class);
    
    protected FacadeGenerator facadeGenerator = new FacadeGenerator();
    
    public BaseGenFacadeProcessor(){
        GenContext.registSimpleClz(List.class, "");
        GenContext.registSimpleClz(Map.class, "any");
        
        GenContext.registSimpleClz(Void.class, "void");
        GenContext.registSimpleClz(String.class, "string");
        
        GenContext.registSimpleClz(Number.class, "number");
        
        GenContext.registSimpleClz(Long.class, "number");
        GenContext.registSimpleClz(long.class, "number");
        
        GenContext.registSimpleClz(Integer.class, "number");
        GenContext.registSimpleClz(int.class, "number");
        
        GenContext.registSimpleClz(Short.class, "number");
        GenContext.registSimpleClz(short.class, "number");
        
        GenContext.registSimpleClz(Byte.class, "String");
        GenContext.registSimpleClz(byte.class, "String");
        
        GenContext.registSimpleClz(Character.class, "String");
        GenContext.registSimpleClz(char.class, "String");
        
        GenContext.registSimpleClz(Float.class, "number");
        GenContext.registSimpleClz(float.class, "number");
        
        GenContext.registSimpleClz(Double.class, "number");
        GenContext.registSimpleClz(double.class, "number");
        
        GenContext.registSimpleClz(BigDecimal.class, "number");

        GenContext.registSimpleClz(boolean.class, "boolean");
        GenContext.registSimpleClz(Boolean.class, "boolean");
        
        GenContext.registSimpleClz(Date.class, "Date");
        GenContext.registSimpleClz(Object.class, "any");
        
        
        GenContext.registIgnoreParamClz(HttpServletRequest.class);
        GenContext.registIgnoreParamClz(HttpServletResponse.class);
        GenContext.registIgnoreParamClz(Model.class);
        GenContext.registIgnoreParamClz(ModelAndView.class);
        
        GenContext.registIgnoreParamAnnotationClz(CookieValue.class);
    }
    
    
    public void scanControllerAndGenFacade() throws InvalidPropertiesFormatException, IOException, TemplateException {
        
        Map<String, String> environments = System.getenv();
        String DALGENX_HOME="DALGENX_HOME";
        String dalgenHome = environments.get(DALGENX_HOME);
        BusinessAssert.mustNotBlank(dalgenHome, DALGENX_HOME+" 环境变量没有设!");
        
            
        String genXml = dalgenHome + "/gen.xml";
        String testClassPath = BaseGenFacadeProcessor.class.getResource("/").toString();
        logger.info("testClassPath<===========>:" + testClassPath + "../../");
        
        String projectRootPath = FileHelpers.getCanonicalPath(testClassPath + "../../");

        logger.info("projectRootPath<===========>:" + projectRootPath);
        
        String genConfigXml = FileHelpers.getCanonicalPath(projectRootPath + "/../gen_config.xml");
        
        Properties root = new Properties();
        
        root.putAll(environments);
        
        Properties systemProperties = System.getProperties();
        root.putAll(systemProperties);
        
        Properties genXmlProperties = PropertiesHelpers.load(genXml);
        root.putAll(genXmlProperties);
        
        Properties genConfigXmlProperties = PropertiesHelpers.load(genConfigXml);
        root.putAll(genConfigXmlProperties);
        
        String packageName = root.getProperty("packageName");
        String pkgName = packageName;//+"."+controller_dir_name;
        
        
        String dir_templates_root = dalgenHome + "/templates/" + root.getProperty("dao_type");
        
        
        Properties appProperties = PropertiesHelpers.load(projectRootPath+"/src/main/resources/application.properties");
        
        Object projectName = appProperties.get("project.name");
        root.put("projectName", projectName);
        root.putAll(GenContext.Properties);
        
        GenContext.tempRootPath=dir_templates_root;
        GenContext.projectRootPath=projectRootPath;
        
        List<Class<?>> classes = ClassHelpers.getClasses(pkgName);
        if (CollectionUtil.isNotEmpty(GenContext.staticUtils)){
            for (Class<?> utilClazz : GenContext.staticUtils) {
                root.put(utilClazz.getSimpleName(), BeanUtils.instantiate(utilClazz));
            }
        }
        facadeGenerator.genFacades(classes, root);
    }
    


}
