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
import java.util.Arrays;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.PropertiesHelpers;
import org.stategen.framework.spring.mvc.ResponseBodyAdviceWrapper;
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
        packageName =packageName+".controller";
        
        String dir_templates_root = dalgenHome + "/templates/" + root.getProperty("dao_type");
        
        
        Properties appProperties = PropertiesHelpers.load(projectRootPath+"/src/main/resources/application.properties");
        
        Object projectName = appProperties.get("project.name");
        root.put("projectName", projectName);
        root.putAll(GenContext.Properties);
        
        GenContext.tempRootPath=dir_templates_root;
        GenContext.projectRootPath=projectRootPath;
        
        if (CollectionUtil.isNotEmpty(GenContext.customVirables)){
            root.putAll(GenContext.customVirables); 
        }
        
        Set<String> packageNames =new HashSet<String>(Arrays.asList(packageName));
        if (CollectionUtil.isNotEmpty(GenContext.extPackageNames)){
            packageNames.addAll(GenContext.extPackageNames);
        }
        ResponseBodyAdviceWrapper.packages=packageNames;
   
          
        Set<Class<?>> allcClasses =new HashSet<Class<?>>();
        for (String pkgName : packageNames) {
            List<Class<?>> classes = ClassHelpers.getClasses(pkgName);
            allcClasses.addAll(classes);
        }
        
        if (CollectionUtil.isNotEmpty(GenContext.staticUtils)){
            for (Class<?> utilClazz : GenContext.staticUtils) {
                root.put(utilClazz.getSimpleName(), BeanUtils.instantiate(utilClazz));
            }
        }
        facadeGenerator.genFacades(allcClasses, root);
    }
    


}
