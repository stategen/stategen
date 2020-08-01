package cn.org.rapid_framework.generator.ext.ant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.GLogger;

public class TableConfigGenTask extends BaseTableConfigSetTask {
	private String tableSqlName;
	
	@Override
    protected List<Map<String, Object>> getGeneratorContexts() {
        if("*".equals(tableSqlName)) {
            return toMaps(tableConfigSet.getTableConfigs());
        }else {
            TableConfig tableConfig;
            try {
                tableConfig = tableConfigSet.getBySqlName(tableSqlName);
            } catch (Exception e) {
                GLogger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            if(tableConfig == null) {
                log("根据表名"+tableSqlName+"没有找到配置文件");
                return null;
            }
            Map<String, Object> map = toMap(tableConfig);
            return Arrays.asList(map);
        }
    }

    private List<Map<String, Object>> toMaps(Collection<TableConfig> tableConfigs) {
        List<Map<String, Object>> result = new ArrayList<>();
        for(TableConfig c : tableConfigs) {
            result.add(toMap(c));
        }
        return result;
    }
    
    private Map<String, Object> toMap(TableConfig tableConfig) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(BeanHelper.describe(tableConfig,"sqls"));
        map.put("tableConfig", tableConfig);
        return map;
    }
    
    public void setTableSqlName(String tableSqlName) {
        this.tableSqlName = tableSqlName;
    }
	
}
