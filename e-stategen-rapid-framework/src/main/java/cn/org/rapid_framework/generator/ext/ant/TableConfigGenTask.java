package cn.org.rapid_framework.generator.ext.ant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

public class TableConfigGenTask extends BaseTableConfigSetTask {
	private String tableSqlName;
	
	@Override
    protected List<Map> getGeneratorContexts() {
        if("*".equals(tableSqlName)) {
            return toMaps(tableConfigSet.getTableConfigs());
        }else {
            TableConfig tableConfig = tableConfigSet.getBySqlName(tableSqlName);
            if(tableConfig == null) {
                log("根据表名"+tableSqlName+"没有找到配置文件");
                return null;
            }
            Map map = toMap(tableConfig);
            return Arrays.asList(map);
        }
    }

    private List<Map> toMaps(Collection<TableConfig> tableConfigs) {
        List<Map> result = new ArrayList();
        for(TableConfig c : tableConfigs) {
            result.add(toMap(c));
        }
        return result;
    }
    
    private Map toMap(TableConfig tableConfig) {
        Map map = new HashMap();
        map.putAll(BeanHelper.describe(tableConfig,"sqls"));
        map.put("tableConfig", tableConfig);
        return map;
    }
    
    public void setTableSqlName(String tableSqlName) {
        this.tableSqlName = tableSqlName;
    }
	
}
