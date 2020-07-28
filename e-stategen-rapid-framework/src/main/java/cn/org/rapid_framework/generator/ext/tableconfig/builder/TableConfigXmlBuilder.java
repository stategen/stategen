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
package cn.org.rapid_framework.generator.ext.tableconfig.builder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig.ColumnConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig.OperationConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig.ParamConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig.ResultMapConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig.SqlConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.XMLHelper;
import cn.org.rapid_framework.generator.util.XMLHelper.NodeData;

public class TableConfigXmlBuilder {

    public static TableConfigSet parseFromXML(String _package,File basedir) {
        String[] tableConfigFilesArray = basedir.list();
        TableConfigSet tableConfigSet = parseFromXML(basedir,_package, Arrays.asList(tableConfigFilesArray));
        return tableConfigSet;
    }
    
    public static TableConfigSet parseFromXML(String _package,File basedir,String tableConfigFiles) {
        String[] tableConfigFilesArray = StringHelper.tokenizeToStringArray(tableConfigFiles, ", \t\n\r\f");
        TableConfigSet tableConfigSet = parseFromXML(basedir,_package, Arrays.asList(tableConfigFilesArray));
        return tableConfigSet;
    }
    
    public static TableConfigSet parseFromXML(File basedir,String _package,List<String> tableConfigFiles) {
        return parseFromXMLDelay(basedir, _package, tableConfigFiles, false);
    }
    
    public static TableConfigSet parseFromXMLDelay(File basedir,String _package,List<String> tableConfigFiles,boolean isDelay) {
        TableConfigSet result = new TableConfigSet();
        result.setDelay(isDelay);
        result.setPackage(_package);
        for(String filepath : tableConfigFiles ) {
            if(filepath.endsWith(".xml")) {
                File file = new File(basedir,filepath);
                if (isDelay) {
                   Map<String, File> sqlNameXmlFileMap = result.getSqlNameXmlFileMap();
                   sqlNameXmlFileMap.put(filepath.substring(0, filepath.length()-4), file);
                } else {
                    TableConfig tableConfig = parseTableConfig(file);
                    result.addTableConfig(tableConfig);
                }
            }
        }
        return result;
    }

    public static TableConfig parseTableConfig(File file) {
        TableConfig tableConfig = null;
        try {
            tableConfig = parseFromXML(file);
        }catch(Throwable e) {
            GLogger.error("parse file:"+file.getAbsolutePath()+" occer error",e);
            throw new RuntimeException("parse file:"+file.getAbsolutePath()+" occer error",e);
        }
        return tableConfig;
    }

    
    public static TableConfig parseFromXML(File file) throws SAXException, IOException {
        NodeData nodeData = new XMLHelper().parseXML(file);
        TableConfig config = new TableConfig();
        
        // table sqlName remarks className isSimple
        BeanHelper.copyProperties(config, nodeData.attributes,true);
        
        for(NodeData child : nodeData.childs) {
            // table/operation
            if("operation".equals(child.nodeName)) {
                OperationConfig target = new OperationConfig();
                BeanHelper.copyProperties(target, child.attributes,true);
                for(NodeData opChild : child.childs) {
                    // table/operation/extraparams
                    if("extraparams".equals(opChild.nodeName)) {
                        // table/operation/extraparams/param
                        for(NodeData paramNode : opChild.childs) {
                            ParamConfig mp = new ParamConfig();
                            BeanHelper.copyProperties(mp, paramNode.attributes,true);
                            target.extraparams.add(mp);
                        }
                    }else {
                        BeanHelper.setProperty(target, opChild.nodeName, getNodeValue(opChild));
                    }
                }
                //后来的覆盖之前的，让用户可以在自动生成的基础上自定义覆盖
                LinkedHashMap<String, OperationConfig> operationMap = config.getOperationMap();
                String opName = target.name;
                if (operationMap.containsKey(opName)) {
                    //warn颜色是黄色，不够醒目
                    GLogger.warn(opName+" 覆盖了之前的 operation");
                }
                operationMap.put(opName, target);
            }
            // table/column
            if("column".equals(child.nodeName)) {
                ColumnConfig target = new ColumnConfig();
                BeanHelper.copyProperties(target, child.attributes,true);
                config.columns.add(target);
            }
            // table/sql
            if("sql".equals(child.nodeName)) {
                SqlConfig target = new SqlConfig();
                BeanHelper.copyProperties(target, child.attributes,true);
                target.setSql(getNodeValue(child));
                config.addSqlConfig(target);
            }
            // table/resultmap
            if("resultmap".equals(child.nodeName)) {
                ResultMapConfig target = new ResultMapConfig();
                BeanHelper.copyProperties(target, child.attributes,true);
                // table/resultmap/column
                for(NodeData c : child.childs) {
                    if("column".equals(c.nodeName)) {
                        ColumnConfig column = new ColumnConfig();
                        BeanHelper.copyProperties(column, c.attributes,true);
                        target.getColumns().add(column);
                    }
                }
                config.resultMaps.add(target);
            }
        }
        return config;
    }
    
    public static String getNodeValue(NodeData v) {
        if(GeneratorProperties.getBoolean(GeneratorConstants.USE_INNER_XML_FOR_XML_PARSING)) {
            return v.innerXML;
        }else {
            return v.nodeValue;
        }
    }
    
}
