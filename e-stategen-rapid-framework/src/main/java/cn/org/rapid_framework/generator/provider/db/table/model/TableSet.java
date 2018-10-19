package cn.org.rapid_framework.generator.provider.db.table.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含一组 Table对象的容器类
 * @author badqiu
 *
 */
public class TableSet implements java.io.Serializable{
	private static final long serialVersionUID = -6500047411657968878L;
	
	private List<Table> tables = new ArrayList<Table>();

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public void addTable(Table c) {
		tables.add(c);
	}

	public Table getBySqlName(String name) {
		for(Table c : tables) {
			if(name.equalsIgnoreCase(c.getSqlName())) {
				return c;
			}
		}
		return null;
	}
	
	public Table getByClassName(String name) {
		for(Table c : tables) {
			if(name.equals(c.getClassName())) {
				return c;
			}
		}
		return null;
	}
	
}
