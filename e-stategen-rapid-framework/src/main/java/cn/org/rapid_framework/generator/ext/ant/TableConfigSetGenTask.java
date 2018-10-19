package cn.org.rapid_framework.generator.ext.ant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfigSet;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

public class TableConfigSetGenTask extends BaseTableConfigSetTask {
	
	@Override
    protected List<Map> getGeneratorContexts() {
        Map map = new HashMap();
        map.putAll(BeanHelper.describe(tableConfigSet));
        map.put("tableConfigSet", tableConfigSet);
        return Arrays.asList(map);
    }

}
