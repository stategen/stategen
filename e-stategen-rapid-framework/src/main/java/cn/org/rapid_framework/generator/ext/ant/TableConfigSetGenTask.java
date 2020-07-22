package cn.org.rapid_framework.generator.ext.ant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.org.rapid_framework.generator.util.BeanHelper;

public class TableConfigSetGenTask extends BaseTableConfigSetTask {
	
	@Override
    protected List<Map<String, Object>> getGeneratorContexts() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(BeanHelper.describe(tableConfigSet));
        map.put("tableConfigSet", tableConfigSet);
        return Arrays.asList(map);
    }

}
