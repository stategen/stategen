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

import org.stategen.framework.generator.util.DaoType;
import org.stategen.framework.generator.util.FileHelpers;
import org.stategen.framework.generator.util.GenProperties;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.Consts;
import org.stategen.framework.util.Setting;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.GenUtils;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.Helper;
import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.GLogger;

public class BaseTargets extends HashMap<String, Object> {
    
    private static final long serialVersionUID = 1L;
    
    private String dir_table_configs;
    
    private String dir_templates_root;
    
    private String packageName;
    
    private String cmdPath;
    
    private String tablesPath;
    
    private String pojo_module_name;
    
    private String dto_module_name;
    
    private String dao_module_name;
    
    private String facade_module_name;
    
    private String service_module_name;
    
    private String controller_module_name;
    
    private String dir_dal_output_root = dao_module_name;                       //projectsPath+"/"+pts.getProperty(Const.dir_dal_output_root);
    
    private String dir_tmpl_share = dir_templates_root + "/java/share/dal";
    
    public void makeConst() {
        Properties pts = GeneratorProperties.getProperties();
        pts.putAll(System.getenv());
        dir_table_configs  = pts.getProperty(Consts.dir_table_configs);
        dir_templates_root = pts.getProperty(Consts.dir_templates_root);
        packageName        = pts.getProperty(Consts.packageName);
        cmdPath            = pts.getProperty(Consts.cmdPath);
        
        tablesPath = GenProperties.getProjectsPath() + '/' + dir_table_configs;
        
        pojo_module_name       = genDir(GenProperties.getProjectsPath(), pts, Consts.pojo_module_name);
        dto_module_name        = genDir(GenProperties.getProjectsPath(), pts, Consts.dto_module_name);
        dao_module_name        = genDir(GenProperties.getProjectsPath(), pts, Consts.dao_module_name);
        facade_module_name     = genDir(GenProperties.getProjectsPath(), pts, Consts.facade_module_name);
        service_module_name    = genDir(GenProperties.getProjectsPath(), pts, Consts.service_module_name);
        controller_module_name = genDir(GenProperties.getProjectsPath(), pts, Consts.controller_module_name);
        
        dir_dal_output_root = dao_module_name;                       //projectsPath+"/"+pts.getProperty(Const.dir_dal_output_root);
        dir_tmpl_share      = dir_templates_root + "/java/share/dal";
    }
    
    private static String genDir(String projectsPath, Properties pts, String key) {
        String value = pts.getProperty(key);
        if (StringUtil.isBlank(value)) {
            return null;
        }
        if (value.equals(".")) {
            value = "";
        }
        try {
            return FileHelpers.getCanonicalPath(projectsPath, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public BaseTargets(Object global) {
        super();
        makeConst();
        
        //groovy loadDefaultGeneratorProperties() 已經執行
        
        Properties pts = GeneratorProperties.getProperties();
        
        String  dao_type = pts.getProperty("dao_type");
        DaoType daoType  = DaoType.valueOf(dao_type);
        
        GenProperties.daoType = daoType != null ? daoType : GenProperties.daoType;
        
        Setting.date_long_field_to_date = "true".equals(pts.getProperty(Consts.date_long_field_to_date));
        
        Setting.gen_select_create_date_field = "true".equals(pts.getProperty(Consts.gen_select_create_date_field));
        Setting.gen_select_update_date_field = "true".equals(pts.getProperty(Consts.gen_select_update_date_field));
        Setting.gen_select_delete_flag_field = "true".equals(pts.getProperty(Consts.gen_select_delete_flag_field));
        
        String updated_date_field = pts.getProperty(Consts.updated_date_field);
        String created_date_field = pts.getProperty(Consts.created_date_field);
        String soft_delete_field  = pts.getProperty(Consts.soft_delete_field);
        
        Setting.updated_date_fields = CollectionUtil.toUpCaseMap(updated_date_field);
        Setting.created_date_fields = CollectionUtil.toUpCaseMap(created_date_field);
        Setting.soft_delete_fields  = CollectionUtil.toUpCaseMap(soft_delete_field);
    }
    
    public void dal() throws Exception {
        File           tablesDirFile  = new File(tablesPath);
        TableConfigSet tableConfigSet = TableConfigXmlBuilder.parseFromXML(tablesDirFile, packageName,
                Helper.getTableConfigFiles(tablesDirFile));
        
        GLogger.info("pojo_module_name<===========>:" + pojo_module_name);
        
        //POJO ，这个必须在最前面，并且执行
        if (StringUtil.isNotBlank(pojo_module_name)) {
            Setting.current_gen_name = Consts.pojo;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(pojo_module_name, dir_templates_root + "/java/dal/1-pojo/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(pojo_module_name, dir_templates_root + "/java/set/1-pojo/", dir_tmpl_share),
                    tableConfigSet);
        }
        
        //DTO
        if (StringUtil.isNotBlank(dto_module_name)) {
            Setting.current_gen_name = Consts.dto;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(dto_module_name, dir_templates_root + "/java/dal/1-dto/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(dto_module_name, dir_templates_root + "/java/set/1-dto/", dir_tmpl_share),
                    tableConfigSet);
        }
        
        //facade 
        if (StringUtil.isNotBlank(facade_module_name)) {
            Setting.current_gen_name = Consts.service;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(facade_module_name, dir_templates_root + "/java/dal/2-facade/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(facade_module_name, dir_templates_root + "/java/set/2-facade/", dir_tmpl_share),
                    tableConfigSet);
        }
        
        //DAO
        if (StringUtil.isNotBlank(dao_module_name)) {
            Setting.current_gen_name = Consts.dao;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(dao_module_name, dir_templates_root + "/java/dal/4-dao/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(dao_module_name, dir_templates_root + "/java/set/4-dao/", dir_tmpl_share),
                    tableConfigSet);
            Setting.current_gen_name = null;
        }
        
        //Service
        if (StringUtil.isNotBlank(service_module_name)) {
            Setting.current_gen_name = Consts.service;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(service_module_name, dir_templates_root + "/java/dal/5-service/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(service_module_name, dir_templates_root + "/java/set/5-service/", dir_tmpl_share),
                    tableConfigSet);
        }
        
        //controller 
        if (StringUtil.isNotBlank(controller_module_name)) {
            Setting.current_gen_name = Consts.controller;
            GenUtils.genByTableConfig(
                    Helper.createGeneratorFacade(controller_module_name, dir_templates_root + "/java/dal/6-controller/", dir_tmpl_share),
                    tableConfigSet, GenProperties.tableName);
            GeneratorProperties.getProperties().put("basepackage", packageName);
            GenUtils.genByTableConfigSet(
                    Helper.createGeneratorFacade(controller_module_name, dir_templates_root + "/java/set/6-controller/", dir_tmpl_share),
                    tableConfigSet);
        }
    }
    
    public void table() throws Exception {
        Setting.current_gen_name = Consts.table;
        
        File           tablesDirFile  = new File(tablesPath);
        TableConfigSet tableConfigSet = TableConfigXmlBuilder.parseFromXMLDelay(tablesDirFile, packageName,
                Helper.getTableConfigFiles(tablesDirFile), true);
        
        GenUtils.genByTable(
                Helper.createGeneratorFacade(tablesPath, dir_templates_root + "/java/table/init", dir_tmpl_share), tableConfigSet,
                GenProperties.tableName);
    }
    
    public void api() throws Exception {
        File tablesDirFile = new File(tablesPath);
        
        TableConfigSet tableConfigSet = TableConfigXmlBuilder.parseFromXML(tablesDirFile, packageName,
                Helper.getTableConfigFiles(tablesDirFile));
        
        GLogger.info("pojo_module_name<===========>:" + "api");

        Properties pts        = GeneratorProperties.getProperties();
        GenProperties.checkCmdIn7(pts,"api");
        
        String controllerTempPath = FileHelpers.getCanonicalPath(dir_templates_root + "/java/api/dal");
        GLogger.info(controllerTempPath);
        
        //api ，这个必须在最前面，并且执行
        Setting.current_gen_name = Consts.controller;
        GLogger.info(controllerTempPath);
        GLogger.info(dir_tmpl_share);
        GenUtils.genByTableConfig(Helper.createGeneratorFacade(cmdPath, controllerTempPath, dir_tmpl_share),
                tableConfigSet, GenProperties.tableName);
    }
    
    public void seq() throws Exception {
        TableConfigSet tableConfigSet = TableConfigXmlBuilder.parseFromXML(
                new File(GenProperties.dalgenHome, dir_table_configs), packageName,
                Helper.getTableConfigFiles(new File(GenProperties.dalgenHome, dir_table_configs)));
        GeneratorProperties.getProperties().put("basepackage", packageName);
        GenUtils.genByTableConfigSet(Helper.createGeneratorFacade(dir_dal_output_root,
                dir_templates_root + "/java/table_config_set/sequence", dir_tmpl_share), tableConfigSet);
    }
    
    //    public void crud() throws Exception {
    //        GeneratorFacade gf = Helper.createGeneratorFacade(dir_dal_output_root, dir_templates_root + "/share/basic",
    //            dir_templates_root + "/table/dao_hibernate", dir_templates_root + "/table/dao_hibernate_annotation",
    //            dir_templates_root + "/table/service_no_interface", dir_templates_root + "/table/web_struts2");
    //        GenUtils.genByTable(gf, GenProperties.tableName);
    //    }
    
}
