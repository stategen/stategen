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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.stategen.framework.util.CollectionUtil;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.util.BeanHelper;

public class GenUtils {
    public static TableConfig globalTableConfig;
    
    public static void genByTableConfigSet(GeneratorFacade generatorFacade, TableConfigSet tableConfigSet) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Set<TableConfig> tempTableConfigs = tableConfigSet.getTableConfigs();
        if (CollectionUtil.isNotEmpty(tempTableConfigs)) {
            ArrayList<TableConfig> sortedTableConfigs = new ArrayList<TableConfig>(tempTableConfigs);
            sortedTableConfigs.sort(new Comparator<TableConfig>() {
                @Override
                public int compare(TableConfig o1, TableConfig o2) {
                    return o1.getClassName().compareTo(o2.getClassName());
                }
            });
            
            LinkedHashSet<TableConfig> linkedHashSet =new LinkedHashSet<TableConfig>(sortedTableConfigs);
            tableConfigSet.setTableConfigs(linkedHashSet);
        }
        map.putAll(BeanHelper.describe(tableConfigSet));
        map.put("tableConfigSet", tableConfigSet);
        generatorFacade.generateByMap(map);
    }

    public static void genByTableConfig(GeneratorFacade generatorFacade, TableConfigSet tableConfigSet, String tableSqlName) throws Exception {

        Collection<TableConfig> tableConfigs = Helper.getTableConfigs(tableConfigSet, tableSqlName);
        for (TableConfig tableConfig : tableConfigs) {
            Map<String, Object> map = new HashMap<>();
            String[] ignoreProperties = { "sqls" };
            map.putAll(BeanHelper.describe(tableConfig, ignoreProperties));
            map.put("tableConfig", tableConfig);
            globalTableConfig=tableConfig;
            generatorFacade.generateByMap(map);
        }
    }

    public static void genByOperation(GeneratorFacade generatorFacade, TableConfigSet tableConfigSet, String tableSqlName) throws Exception {
        Collection<TableConfig> tableConfigs = Helper.getTableConfigs(tableConfigSet, tableSqlName);
        for (TableConfig tableConfig : tableConfigs) {
            for (Sql sql : tableConfig.getSqls()) {
                Map<String, Object> operationMap = new HashMap<>();
                operationMap.putAll(BeanHelper.describe(sql));
                operationMap.put("sql", sql);
                operationMap.put("tableConfig", tableConfig);
                globalTableConfig=tableConfig;
                operationMap.put("basepackage", tableConfig.getBasepackage());
                generatorFacade.generateByMap(operationMap);
            }
        }
    }

    public static void genByTable(GeneratorFacade generatorFacade, TableConfigSet tableConfigSet, String tableSqlName) throws Exception {
        generatorFacade.generateByTable(tableConfigSet,tableSqlName);
    }

}