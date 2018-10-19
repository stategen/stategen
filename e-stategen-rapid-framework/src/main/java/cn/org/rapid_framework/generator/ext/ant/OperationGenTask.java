package cn.org.rapid_framework.generator.ext.ant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

public class OperationGenTask extends BaseTableConfigSetTask {
    private String tableSqlName;
    
    @Override
    protected List<Map> getGeneratorContexts() throws SQLException, Exception {
        if("*".equals(tableSqlName)) {
            List<Map> result = new ArrayList();
            for(TableConfig tableConfig : tableConfigSet.getTableConfigs()) {
                result.addAll(toMaps(tableConfig));
            }
            return result;
        }else {
            TableConfig tableConfig = tableConfigSet.getBySqlName(tableSqlName);
            if(tableConfig == null) {
                log("根据表名"+tableSqlName+"没有找到配置文件");
                return null;
            }
            List<Map> result = toMaps(tableConfig);
            return result;
        }
    }

    private List<Map> toMaps(TableConfig tableConfig) throws SQLException, Exception {
        List<Map> result = new ArrayList();
        for(Sql sql : tableConfig.getSqls()) {
            Map operationMap = new HashMap();
            operationMap.putAll(BeanHelper.describe(sql));
            operationMap.put("sql", sql);
            operationMap.put("basepackage", tableConfig.getBasepackage());
            operationMap.put("tableConfig", tableConfig);
            result.add(operationMap);
        }
        return result;
    }

    
    public void setTableSqlName(String tableSqlName) {
        this.tableSqlName = tableSqlName;
    }
    
}
