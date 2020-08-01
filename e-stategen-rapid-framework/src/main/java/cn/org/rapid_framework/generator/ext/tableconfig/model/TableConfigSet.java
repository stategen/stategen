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
package cn.org.rapid_framework.generator.ext.tableconfig.model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.stategen.framework.lite.CaseInsensitiveHashMap;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.ext.tableconfig.builder.TableConfigXmlBuilder;
import cn.org.rapid_framework.generator.provider.db.table.TableFactory;
import cn.org.rapid_framework.generator.provider.db.table.TableFactoryListener;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.util.GLogger;

public class TableConfigSet implements Iterable<TableConfig>, TableFactoryListener {

    private Set<TableConfig> tableConfigs = new LinkedHashSet<TableConfig>();

    private Map<String, TableConfig> sqlNameMap = new CaseInsensitiveHashMap<>();
    private Map<String, TableConfig> classNameMap = new HashMap<>();
    private Map<String, File> sqlNameXmlFileMap = new HashMap<>();
    
    private Set<String> sequences = new HashSet<String>();

    private String _package;
    
    private boolean isDelay=false;

    public TableConfigSet() {

        //增加监听器,用于table的自定义className
        TableFactory tf = TableFactory.getInstance();
        tf.addTableFactoryListener(this);
    }
    
    
    public boolean isDelay() {
        return isDelay;
    }
    
    
    public void setDelay(boolean isDelay) {
        this.isDelay = isDelay;
    }
    
    public Map<String, File> getSqlNameXmlFileMap() {
        return sqlNameXmlFileMap;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
        setPackageForTableConfigs();
    }

    public Set<String> getSequences() {
        return sequences;
    }

    public void addTableConfig(TableConfig tableConfig) {
        setPackageIfBlank(tableConfig);
        tableConfigs.add(tableConfig);
        this.addToCacheMaps(tableConfig);
    }

    public Set<TableConfig> getTableConfigs() {
        return tableConfigs;
    }

    public void setTableConfigs(Set<TableConfig> tableConfigs) {
        this.tableConfigs = tableConfigs;
        setPackageForTableConfigs();
        this.classNameMap.clear();
        this.sqlNameMap.clear();
        this.sequences.clear();
        
        for (TableConfig tableConfig : tableConfigs) {
            addToCacheMaps(tableConfig);
        }
        
    }

    private void addToCacheMaps(TableConfig tableConfig) {
        this.sqlNameMap.put(tableConfig.getSqlName(), tableConfig);
        this.classNameMap.put(tableConfig.getClassName(), tableConfig);
        String sequence = tableConfig.getSequence();
        if (StringUtil.isNotBlank(sequence)) {
            this.sequences.add(sequence);
        }
    }

    private void setPackageForTableConfigs() {
        for (TableConfig t : this.tableConfigs) {
            setPackageIfBlank(t); //FIXME 需要检查,如果 tableConfig.package为空,才可以设置,不然会覆盖 tableConfig已有的值
        }
    }

    private void setPackageIfBlank(TableConfig t) {
        if (StringUtil.isBlank(t.getPackage())) {
            t.setPackage(getPackage());
        }
    }

    public TableConfig getRequiredBySqlName(String sqlName) {
        TableConfig tc;
        try {
            tc = getBySqlName(sqlName);
        } catch (Exception e) {
            GLogger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (tc == null) {
            throw new IllegalArgumentException("not found TableConfig on TableConfigSet by sqlName:" + sqlName);
        }
        return tc;
    }

    public TableConfig getBySqlName(String sqlName) throws Exception {
        //延时加载sqlname会放这里
        File file = this.sqlNameXmlFileMap.remove(sqlName);
        if (file!=null) {
            TableConfig tableConfig = TableConfigXmlBuilder.parseTableConfig(file); 
            this.addTableConfig(tableConfig);
        }
        return sqlNameMap.get(sqlName);
    }

    public TableConfig getByClassName(String className) {
        return classNameMap.get(className);
    }

    public Iterator<TableConfig> iterator() {
        return tableConfigs.iterator();
    }

    public void onTableCreated(Table table) {
        TableConfig tc =null;
        try {
            tc = getBySqlName(table.getSqlName());
        } catch (Exception e) {
            //文件解析不出来，xml.xhtml没有创建,这个在制作xml.xhtml时没有关系
            GLogger.error(e.getMessage());
        }
        if (tc == null) {
            return;
        }
        tc.customTable(table);
        //        table.setClassName(tc.getClassName());
        //        if(StringUtil.isNotBlank(table.getRemarks())) {
        //            table.setTableAlias(table.getRemarks());
        //        }

        //FIXME 还没有考虑TableConfigSet 的listener清除问题,
        //TODO 增加单元测试
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        GLogger.warn("没有手工清除TableFactoryListener for TableConfigSet");
        TableFactory.getInstance().removeTableFactoryListener(this);
    }
}
