package cn.org.rapid_framework.generator.provider.db.table.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import cn.org.rapid_framework.generator.util.StringHelper;
/**
 * 包含一组Column对象的容器类
 * @author badqiu
 *
 */
public class ColumnSet implements java.io.Serializable{
	private static final long serialVersionUID = -6500047411657968878L;
	
	private LinkedHashSet<Column> columns = new LinkedHashSet<Column>();

	public ColumnSet(){
	}
	
	public ColumnSet(Collection<? extends Column> columns) {
        super();
        this.columns = new LinkedHashSet(columns);
    }

    public LinkedHashSet<Column> getColumns() {
		return columns;
	}

	public void setColumns(LinkedHashSet<Column> columns) {
		this.columns = columns;
	}
	
	public void addColumn(Column c) {
		columns.add(c);
	}

	public Column getBySqlName(String name,int sqlType) {
		for(Column c : columns) {
			if(name.equalsIgnoreCase(c.getSqlName()) && c.getSqlType() == sqlType) {
				return c;
			}
		}
		return null;
	}
	
	public Column getBySqlName(String name) {
	    if(name == null) return null;
	    
		for(Column c : columns) {
			if(name.equalsIgnoreCase(c.getSqlName())) {
				return c;
			}
		}
		return null;
	}
	
	public Column getByName(String name) {
	    if(name == null) return null;
	    
        Column c = getBySqlName(name);
        if(c == null) {
            c = getBySqlName(StringHelper.toUnderscoreName(name));
        }
        return c;
	}

	public Column getByName(String name,int sqlType) {
        Column c = getBySqlName(name,sqlType);
        if(c == null) {
            c = getBySqlName(StringHelper.toUnderscoreName(name),sqlType);
        }
        return c;
	}
	
    public Column getByColumnName(String name) {
        if(name == null) return null;
        
        for(Column c : columns) {
            if(name.equals(c.getColumnName())) {
                return c;
            }
        }
        return null;
    }
	/**
	 * 得到是主键的全部column
	 * @return
	 */	
	public List<Column> getPkColumns() {
		List results = new ArrayList();
		for(Column c : getColumns()) {
			if(c.isPk())
				results.add(c);
		}
		return results;
	}
	
	/**
	 * 得到不是主键的全部column
	 * @return
	 */
	public List<Column> getNotPkColumns() {
		List results = new ArrayList();
		for(Column c : getColumns()) {
			if(!c.isPk())
				results.add(c);
		}
		return results;
	}
	
	/**
	 * 得到主键总数
	 * @return
	 */
	public int getPkCount() {
		int pkCount = 0;
		for(Column c : columns){
			if(c.isPk()) {
				pkCount ++;
			}
		}
		return pkCount;
	}
	
	/** 得到单主键，等价于getPkColumns().get(0)  */
	public Column getPkColumn() {
		if(getPkColumns().isEmpty()) {
			return null;
		}
		return getPkColumns().get(0);
	}
	
	public List<Column> getEnumColumns() {
        List results = new ArrayList();
        for(Column c : getColumns()) {
            if(!c.isEnumColumn())
                results.add(c);
        }
        return results;	    
	}
}
