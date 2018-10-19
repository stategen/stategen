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
import java.util.HashMap;
import java.util.Properties;

import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.Consts;
import org.stategen.framework.util.Setting;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.GenUtils;
import cn.org.rapid_framework.generator.GeneratorFacade;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.Helper;
import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.FileHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.PropertiesHelper;

public class BaseTargets extends HashMap<String, Object> {
    
    private static final long serialVersionUID = 1L;
    
    private Properties pts= GeneratorProperties.getProperties();
    
    private String dalgen_dir =pts.getProperty(Consts.dalgen_dir);
    private String dir_table_configs =pts.getProperty(Consts.dir_table_configs);
    private String dir_templates_root =pts.getProperty(Consts.dir_templates_root);
    private String packageName =pts.getProperty(Consts.packageName);
    private String genInputCmd =pts.getProperty(Consts.genInputCmd);
    private String cmdPath =pts.getProperty(Consts.cmdPath);
    
    private String tablesPath=cmdPath+"/"+dir_table_configs;
    
    private String pojo_module_name =genDir(cmdPath,pts,Consts.pojo_module_name);
    private String dto_module_name =genDir(cmdPath,pts,Consts.dto_module_name);
    private String dao_module_name =genDir(cmdPath,pts,Consts.dao_module_name);
    private String facade_module_name =genDir(cmdPath,pts,Consts.facade_module_name);
    private String service_module_name =genDir(cmdPath,pts,Consts.service_module_name);
    private String controller_module_name =genDir(cmdPath,pts,Consts.controller_module_name);
    
    private String dir_dal_output_root =dao_module_name;//cmdPath+"/"+pts.getProperty(Const.dir_dal_output_root);
    private String dir_tmpl_share=dir_templates_root+"/java/share/dal";
    
    private static String genDir(String cmdPath,Properties pts,String key){
        String value =pts.getProperty(key);
        if (StringUtil.isBlank(value)){
            return null;
        }
        if (value.equals(".")){
            value="";
        }
        return cmdPath+"/"+value;
    }
    
    public BaseTargets(Object global){
        super();
        Setting.date_long_field_to_date ="true".equals( pts.getProperty(Consts.date_long_field_to_date));
        
        Setting.gen_select_create_date_field ="true".equals( pts.getProperty(Consts.gen_select_create_date_field));
        Setting.gen_select_update_date_field ="true".equals( pts.getProperty(Consts.gen_select_update_date_field));
        Setting.gen_select_delete_flag_field ="true".equals( pts.getProperty(Consts.gen_select_delete_flag_field));

        String updated_date_field =pts.getProperty(Consts.updated_date_field);
        String created_date_field =pts.getProperty(Consts.created_date_field);
        String soft_delete_field =pts.getProperty(Consts.soft_delete_field);

        Setting.updated_date_fields= CollectionUtil.toUpCaseMap(updated_date_field);
        Setting.created_date_fields=CollectionUtil.toUpCaseMap(created_date_field);
        Setting.soft_delete_fields=CollectionUtil.toUpCaseMap(soft_delete_field);
    }

    public void dal() throws Exception {
        File tablesDirFile=new File(tablesPath);
        TableConfigSet tableConfigSet = new TableConfigXmlBuilder().parseFromXML(tablesDirFile,packageName,Helper.getTableConfigFiles(tablesDirFile));

        GLogger.info("pojo_module_name<===========>:" +pojo_module_name);
        
        //POJO ，这个必须在最前面，并且执行
        if (StringUtil.isNotBlank(pojo_module_name)){
            Setting.current_gen_name=Consts.pojo;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(pojo_module_name,dir_templates_root+"/java/1-pojo/dal",dir_tmpl_share),tableConfigSet,genInputCmd);
        }

        //DTO
        if (StringUtil.isNotBlank(dto_module_name)){
            Setting.current_gen_name=Consts.dto;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(dto_module_name,dir_templates_root+"/java/1-dto/dal",dir_tmpl_share),tableConfigSet,genInputCmd);
        }
        
        //facade 
        if (StringUtil.isNotBlank(facade_module_name)){
            Setting.current_gen_name=Consts.service;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(facade_module_name,dir_templates_root+"/java/2-facade/dal",dir_tmpl_share),tableConfigSet,genInputCmd);
            GenUtils.genByTableConfigSet(Helper.createGeneratorFacade(facade_module_name,dir_templates_root+"/java/2-facade/set/",dir_tmpl_share),tableConfigSet);
        }
        
        //DAO
        if (StringUtil.isNotBlank(dao_module_name)){
            Setting.current_gen_name=Consts.dao;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(dao_module_name,dir_templates_root+"/java/4-dao/dal/",dir_tmpl_share),tableConfigSet,genInputCmd);
            GenUtils.genByTableConfigSet(Helper.createGeneratorFacade(dao_module_name,dir_templates_root+"/java/4-dao/set/",dir_tmpl_share),tableConfigSet);
            Setting.current_gen_name=null;
        }
        
        //Service
        if (StringUtil.isNotBlank(service_module_name)){
            Setting.current_gen_name=Consts.service;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(service_module_name,dir_templates_root+"/java/5-service/dal",dir_tmpl_share),tableConfigSet,genInputCmd);
            GenUtils.genByTableConfigSet(Helper.createGeneratorFacade(service_module_name,dir_templates_root+"/java/5-service/set/",dir_tmpl_share),tableConfigSet);
        }
        
        //controller 
        if (StringUtil.isNotBlank(controller_module_name)){
            Setting.current_gen_name=Consts.controller;
            GenUtils.genByTableConfig(Helper.createGeneratorFacade(controller_module_name,dir_templates_root+"/java/6-controller/dal",dir_tmpl_share),tableConfigSet,genInputCmd);
        }
    }

    public void table() throws Exception {
        Setting.current_gen_name=Consts.table;
        GenUtils.genByTable(Helper.createGeneratorFacade(tablesPath,dir_templates_root+"/java/table/init",dir_tmpl_share),genInputCmd);
    }

    public void seq() throws Exception {
        TableConfigSet tableConfigSet = new TableConfigXmlBuilder().parseFromXML(new File(dalgen_dir,dir_table_configs),packageName,Helper.getTableConfigFiles(new File(dalgen_dir,dir_table_configs)));
        pts.put("basepackage",packageName);
        GenUtils.genByTableConfigSet(Helper.createGeneratorFacade(dir_dal_output_root,dir_templates_root+"/java/table_config_set/sequence",dir_tmpl_share),tableConfigSet);
    }

    public void crud() throws Exception {
        GeneratorFacade gf = Helper.createGeneratorFacade(dir_dal_output_root,
            dir_templates_root+"/share/basic",
            dir_templates_root+"/table/dao_hibernate",
            dir_templates_root+"/table/dao_hibernate_annotation",
            dir_templates_root+"/table/service_no_interface",
            dir_templates_root+"/table/web_struts2");
        GenUtils.genByTable(gf,genInputCmd);
    }
    
    
    
    protected Properties getRootProperties() throws IOException{
        Properties root =new Properties();
        root.put("generator_tools_class","");
        root.put("gg_isOverride","true");

        root.put("generator_sourceEncoding","UTF-8");
        root.put("generator_outputEncoding","UTF-8");
        
        //将表名从复数转换为单数
        root.put("tableNameSingularize","true");
        
        //加载默认配置文件
        String dalgenPath =System.getProperty("dalgenPath");
        String genFileName = FileHelper.getCanonicalPath(dalgenPath+"/gen.xml");
        
        root.putAll(PropertiesHelper.load(genFileName)); 
        
        //加载项目配置文件，如果相同，覆盖默认配置
        root.put("dalgenPath",dalgenPath);
        
        String sysName =System.getProperty("systemName");
        if (StringUtil.isNotBlank(sysName)){
            root.put("systemName",sysName);
        }
        
        String packageName =System.getProperty("packageName");
        if (StringUtil.isNotBlank(packageName)){
            root.put("packageName",packageName);
        }
        
        cmdPath =System.getProperty("cmdPath");
        
        dir_templates_root=dalgenPath+"/templates/"+root.getProperty("dao_type");
        
        return root;
    }
    
}
