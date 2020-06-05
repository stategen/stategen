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
package cn.org.rapid_framework.generator.provider.db.sql.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.ext.tableconfig.model.TableConfig;
import cn.org.rapid_framework.generator.provider.db.sql.SqlFactory;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.ColumnSet;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.sqlparse.SqlParseHelper;
import cn.org.rapid_framework.generator.util.sqlparse.SqlParseHelper.NameWithAlias;
import cn.org.rapid_framework.generator.util.sqlparse.SqlTypeChecker;
import cn.org.rapid_framework.generator.util.typemapping.JavaPrimitiveTypeMapping;

/**
 * 用于生成代码的Sql对象.对应数据库的sql语句
 * 使用SqlFactory.parseSql()生成 <br />
 * 
 * SQL参数同时支持以下几种语法
 * <pre>
 * hibernate: :username,
 * ibatis2: #username#,$usename$,
 * mybatis(or mybatis): #{username},${username}
 * </pre>
 * SQL对象创建示例：
 * <pre>
 * Sql sql = new SqlFactory().parseSql("select * from user_info where username=#username# and password=#password#");
 * </pre>
 * 
 * @see SqlFactory
 * @author badqiu
 *
 */
public class Sql {
	public static String MULTIPLICITY_ONE = "one"; // select查询回一条记录, selectOne()
	public static String MULTIPLICITY_MANY = "many"; // select查询回一个List, selectList()
	public static String MULTIPLICITY_PAGING = "paging"; // 分页查询
	
	public static String PARAMTYPE_PRIMITIVE = "primitive";
	public static String PARAMTYPE_OBJECT = "object";
	public static String SERVICE_FACADE = "facade";
	
	String operation = null;  //这段 sql相对应的操作名称.
	String resultClass; // select查询返回的结果集的class
	String parameterClass; //参数代表的parameterClass
	String remarks; //注释
	
	
	
	String                      multiplicity        = MULTIPLICITY_ONE;                 /* many or one or paging */
//    boolean                     paging              = false;                            // 是否分页查询
    boolean                     countService     =false;
    
    
    String                      sqlmap;                                                 /* for ibatis and mybatis */
    String                      resultMap           = null;                             /* for ibatis and mybatis */
	
    /** 代表一条select查询回来的结果列 */
	LinkedHashSet<Column> columns = new LinkedHashSet<Column>();
	/** 代表一条sql 查询有参数列表  */
	LinkedHashSet<SqlParameter> params = new LinkedHashSet<SqlParameter>();
	
	String sourceSql; // source sql
	String executeSql; //代表在数据库执行的sql
	boolean facade =false;
	private String              paramType           = PARAMTYPE_PRIMITIVE;                      /* primitive or object */
	
	/** 代表一段SQL include 其它的sql片段. 如ibatis中的 <include refid='User.Where'/> */
	private List<SqlSegment> sqlSegments = new ArrayList<SqlSegment>();
	TableConfig tableConfig=null;
	
	public Sql(TableConfig tableConfig) {
	    this.tableConfig=tableConfig;
	}
	
	public TableConfig getTable() {
        return tableConfig;
    }
    
    public boolean isFullColumns(){
        boolean result =isColumnsInSameTable();
        if (result){
            Collection<NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(executeSql);
            if (tableNames.size() > 1) {
                return false;
            }

            Table t = SqlFactory.getTableFromCache(tableNames.iterator().next().getName());
            LinkedHashSet<Column> allColumns = t.getColumns();
            if (CollectionUtil.isNotEmpty(allColumns) && allColumns.size() != columns.size())
                return false;
        }
        return result;
    }

	/** 判断select查询回来的列是否是同一张表的字段 */
	public boolean isColumnsInSameTable() {
		// FIXME 还要增加表的列数与columns是否相等,才可以为select 生成 include语句
		if(columns == null || columns.isEmpty()) return false;
		
		Collection<NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(executeSql);
		if(tableNames.size() > 1) {
		    return false;
		}
        Table t = SqlFactory.getTableFromCache(tableNames.iterator().next().getName());
        for(Column c : columns) {
            Column fromTableColumn = new ColumnSet(t.getColumns()).getBySqlName(c.getSqlName());
            if(fromTableColumn == null) {
                return false;
            }
        }
        
//		Column firstTable = columns.iterator().next();
//		if(columns.size() == 1) return true;
//		if(firstTable.getTable() == null) {
//			return false;
//		}
//		
//		String preTableName = firstTable.getTable().getSqlName();
//		for(Column c :columns) {
//			Table table = c.getTable();
//			if(table == null) {
//				return false;
//			}
//			if(preTableName.equalsIgnoreCase(table.getSqlName())) {
//				continue;
//			}else {
//			    return false;
//			}
//		}
		return true;
	}

	
    /**
     * 得到select查询返回的resultClass,可以通过setResultClass()自定义，如果没有自定义则为你自动生成<br />
     * resultClass可以为com.company.User的完全路径
     * 示例:
     * <pre>
     * select count(*) from user, 返回值为: Long
     * select * from user 返回值为: User
     * select count(*) cnt, sum(age) sum_age 返回值为: getOperation()+"Result";
     * </pre>
     * @return
     */
	public String getResultClass() {
		String resultClass = _getResultClass();
		if(isPaging() || MULTIPLICITY_MANY.equals(multiplicity)) {
		    return JavaPrimitiveTypeMapping.getWrapperType(resultClass);
		}else {
		    return resultClass;
		}
	}

    private String _getResultClass() {
        if(StringUtil.isNotBlank(resultClass)) {
            return resultClass;
        }
        
		if(columns.size() == 1) {
			Column column = columns.iterator().next();
			String javaType =column.getJavaType();
			if (javaType.startsWith("java.lang.")){
			    return column.getSimpleJavaType();
			}
			return javaType;
		}
		
		if(isColumnsInSameTable()) {
		    Collection<NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(executeSql);
		    Table t = SqlFactory.getTableFromCache(tableNames.iterator().next().getName()); //FIXME 自定义的className将不会起作用,因为不是从同一个cache取的对象
		    String result = t.getClassName();
		    return result;
		}else {
			if(operation == null) {
			    return null;
			}
			String result= StringHelper.makeAllWordFirstLetterUpperCase(
			    StringHelper.toUnderscoreName(operation))+GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_SQL_RESULTCLASS_SUFFIX);
			return result;
		}
    }    
	
	public void setResultClass(String queryResultClass) {
		this.resultClass = queryResultClass;
	}
	
	public boolean isHasCustomResultClass() {
	    return StringUtil.isNotBlank(this.resultClass);
	}

	public boolean isHasResultMap() {
        return StringUtil.isNotBlank(this.resultMap);
    }
	
    /**
     * 返回getResultClass()的类名称 <br />
     * 示例: <br />
     * 如getResultClass()=com.company.User,将返回User
     */	
	public String getResultClassName() {
		int lastIndexOf = getResultClass().lastIndexOf(".");
		return lastIndexOf >= 0 ? getResultClass().substring(lastIndexOf+1) : getResultClass();
	}

    /**
     * SQL参数过多时用于封装为一个ParameterObject的class<br />
     * <pre>
     * 可以通过setParameterClass()自定义
     * 没有自定义则:
     * 如果是select查询,返回 operation+"Query"
     * 其它则返回operation+"Parameter"
     * <pre>
     */	
	public String getParameterClass() {
		if(StringUtil.isNotBlank(parameterClass)) return parameterClass;
		if(StringUtil.isBlank(operation)) return null;
		if(isSelectSql()) {
			return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(operation))+"Query";
		}else {
			return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(operation))+"Parameter";
		}
	}
	
	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}

    /**
     * 返回getParameterClass()的类名称 <br />
     * 示例: <br />
     * 如getParameterClass()=com.company.UserQuery,将返回UserQuery
     */		
	public String getParameterClassName() {
		int lastIndexOf = getParameterClass().lastIndexOf(".");
		return lastIndexOf >= 0 ? getParameterClass().substring(lastIndexOf+1) : getParameterClass();
	}
	
	// TODO columnsSize大于二并且不是在同一张表中,将创建一个QueryResultClassName类,同一张表中也要考虑创建类
	public int getColumnsCount() {
		return columns.size();
	}
	public void addColumn(Column c) {
		columns.add(c);
	}

    /**
     * 得到该sql方法相对应的操作名称,模板中的使用方式为: public List ${operation}(),示例值: findByUsername
     * @return
     */
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperationFirstUpper() {
		return StringHelper.capitalize(getOperation());
	}

    /**
     * 用于控制查询结果,固定值为:one,many
     * @return
     */
	public String getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(String multiplicity) {
		// TODO 是否要增加验证数据为 one,many
		this.multiplicity = multiplicity;
	}

    /**
     * 得到sqlect 查询的列对象(column),如果是insert,delete,update语句,则返回empty Set.<br />
     * 示例:
     * <pre>
     * SQL : select count(*) cnt, sum(age) sum_age from user_info
     * columns: cnt,sum_age
     * </pre>
     * @return
     */
	public LinkedHashSet<Column> getColumns() {
		return columns;
	}
	public void setColumns(LinkedHashSet<Column> columns) {
		this.columns = columns;
	}

    /**
     * 得到SQL的参数对象<br />
     * 示例:
     * <pre>
     * SQL : select * from user_info where username=:user and password=:pwd limit :offset,:limit
     * params: user,pwd,offset,limit
     * </pre>
     * @return
     */
	public LinkedHashSet<SqlParameter> getParams() {
		return params;
	}
	public void setParams(LinkedHashSet<SqlParameter> params) {
		this.params = params;
	}
	public SqlParameter getParam(String paramName) {
		for(SqlParameter p : getParams()) {
			if(p.getParamName().equals(paramName)) {
				return p;
			}
		}
		return null;
	}

    /**
     * 得到SQL原始语句
     * @return
     */
	public String getSourceSql() {
		return sourceSql;
	}
	public void setSourceSql(String sourceSql) {
		this.sourceSql = sourceSql;
	}
	
	public String getSqlmap() {
		return getSqlmap(getParamNames());
	}
	
	public void setSqlmap(String sqlmap) {
	    if(StringUtil.isNotBlank(sqlmap)) {
	        sqlmap = StringHelper.replace(sqlmap, "${cdata-start}", "<![CDATA[");
	        sqlmap = StringHelper.replace(sqlmap, "${cdata-end}", "]]>");
	    }
	    this.sqlmap = sqlmap;
	}

    private List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        for(SqlParameter p : params) {
            paramNames.add(p.getParamName());
        }
        return paramNames;
    }
	   
    private String getSqlmap(List<String> params) {
        if (params == null || params.size() == 0) {
            return sqlmap;
        }

        String result = sqlmap;

        if (params.size() == 1) {
        	//FIXME: 与dalgen相比,修正是否将 ${param1} 的替换值是: value
            return StringHelper.replace(result, "${param1}", "value");
        } else {
            for (int i = 0; i < params.size(); i++) {
                result = StringHelper.replace(result, "${param" + (i + 1) + "}", params.get(i));
            }
        }

        return result;
    }
	
	public boolean isHasSqlMap() {
		return StringUtil.isNotBlank(sqlmap);
	}
	
	public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    //	public String replaceParamsWith(String prefix,String suffix) {
//		String sql = sourceSql;
//		List<SqlParameter> sortedParams = new ArrayList(params);
//		Collections.sort(sortedParams,new Comparator<SqlParameter>() {
//			public int compare(SqlParameter o1, SqlParameter o2) {
//				return o2.paramName.length() - o1.paramName.length();
//			}
//		});
    // for(SqlParameter s : sortedParams){ //FIXME 现在只实现了:username参数替换
//			sql = StringHelper.replace(sql,":"+s.getParamName(),prefix+s.getParamName()+suffix);
//		}
//		return sql;
//	}
    /**
     * sourceSql转换为在数据库实际执行的SQL,
     * 示例:
     * <pre>
     * sourceSql: select * from user where username=:username and password=:password
     * executeSql: select * from user where username=? and password=?
     * </pre>
     * @return
     */
	public String getExecuteSql() {
		return executeSql;
	}
	
	public void setExecuteSql(String executeSql) {
		this.executeSql = executeSql;
	}
	
    public String getCountHql() {
        return toCountSqlForPaging(getHql());
    }
	   
	public String getCountSql() {
	    return toCountSqlForPaging(getSql());
	}

    public String getIbatisCountSql() {
        return toCountSqlForPaging(getIbatisSql());
    }
    
    public String getMybatisCountSql() {
        return toCountSqlForPaging(getMybatisSql());
    }

    public String getSqlmapCountSql() {
        return toCountSqlForPaging(getSqlmap());
    }
    
	public String getSql() {
		return replaceWildcardWithColumnsSqlName(sourceSql);
	}
	
	public static String toCountSqlForPaging(String sql) {
	    if(sql == null) return null;
	    if(SqlTypeChecker.isSelectSql(sql)) {
            return SqlParseHelper.toCountSqlForPaging(sql, "select count(*) ");
	    }
	    return sql;
	}
	
	public String getSpringJdbcSql() {
		return SqlParseHelper.convert2NamedParametersSql(getSql(),":","");
	}
	
	public String getHql() {
		return SqlParseHelper.convert2NamedParametersSql(getSql(),":","");
	}
	
	public String getIbatisSql() {
	    return StringUtil.isBlank(ibatisSql) ? SqlParseHelper.convert2NamedParametersSql(getSql(),"#","#") : ibatisSql;
	}
	
	public String getMybatisSql() {
	    return StringUtil.isBlank(mybatisSql) ? SqlParseHelper.convert2NamedParametersSql(getSql(),"#{","}") : mybatisSql;
	}

	public void setIbatisSql(String ibatisSql) {
        this.ibatisSql = ibatisSql;
    }

    public void setMybatisSql(String mybatisSql) {
        this.mybatisSql = mybatisSql;
    }

    private String joinColumnsSqlName() {
        // TODO 未解决 a.*,b.*问题
		StringBuilder sb = new StringBuilder();
		for(Iterator<Column> it = columns.iterator();it.hasNext();) {
			Column c = it.next();
			sb.append(c.getSqlName());
			if(it.hasNext()) sb.append(",");
		}
		return sb.toString();
	}
	
	public String replaceWildcardWithColumnsSqlName(String sql) {
	    String sqlLower =sql.toLowerCase();
		if(SqlTypeChecker.isSelectSql(sql) 
		        && SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sqlLower)).indexOf("*") >= 0 
		        && SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sqlLower)).indexOf("count(") < 0) {
			return SqlParseHelper.getPrettySql("select " + joinColumnsSqlName() + " " + SqlParseHelper.removeSelect(sql));
		}else {
			return sql;
		}
	}

	public List<SqlSegment> getSqlSegments() {
		return sqlSegments;
	}

	public void setSqlSegments(List<SqlSegment> includeSqls) {
		this.sqlSegments = includeSqls;
	}
	
	public SqlSegment getSqlSegment(String id) {
		for(SqlSegment seg : sqlSegments) {
			if(seg.getId().equals(id)) {
				return seg;
			}
		}
		return null;
	}
	
	public List<SqlParameter> getFilterdWithSqlSegmentParams() {
		List<SqlParameter> result = new ArrayList<SqlParameter>();
		for(SqlParameter p : getParams()) {
			if(isSqlSegementContainsParam(p.getParamName())) {
				continue;
			}
			result.add(p);
		}
		return result;
	}
	
    private boolean isSqlSegementContainsParam(String paramName) {
		for(SqlSegment seg : getSqlSegments()) {
			//TODO 增加如果参数数是1,则不生成  SqlSegemnt,此处也要修改对1的特殊控制
			if(seg.getParamNames().contains(paramName)) {
				return true;
			}
		}
		return false;
	}

	/**
     * 当前的sourceSql是否是select语句
     * @return
     */
	public boolean isSelectSql() {
		return SqlTypeChecker.isSelectSql(sourceSql);
	}

    /**
     * 当前的sourceSql是否是update语句
     * @return
     */
	public boolean isUpdateSql() {
		return SqlTypeChecker.isUpdateSql(sourceSql);
	}

    /**
     * 当前的sourceSql是否是delete语句
     * @return
     */
	public boolean isDeleteSql() {
		return SqlTypeChecker.isDeleteSql(sourceSql);
	}

    /**
     * 当前的sourceSql是否是insert语句
     * @return
     */
	public boolean isInsertSql() {
		return SqlTypeChecker.isInsertSql(sourceSql);
	}

    /**
     * 得到备注
     * @return
     */
	public String getRemarks() {
		return remarks;
	}

    public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public void setRemarks(String comments) {
		this.remarks = comments;
	}
	
	public boolean isPaging() {
		return MULTIPLICITY_PAGING.equalsIgnoreCase(multiplicity);
    }

//    public void setPaging(boolean paging) {
//        this.paging = paging;
//    }
    

	public boolean isFacade() {
        return facade;
    }

    public boolean isCountService() {
        return countService;
    }

    public void setCountService(boolean countService) {
        this.countService = countService;
    }

    public void setFacade(boolean facade) {
        this.facade = facade;
    }

    public Column getColumnBySqlName(String sqlName) {
		for(Column c : getColumns()) {
			if(c.getSqlName().equalsIgnoreCase(sqlName)) {
				return c;
			}
		}
		return null;
	}
	
	public Column getColumnByName(String name) {
	    Column c = getColumnBySqlName(name);
	    if(c == null) {
	    	c = getColumnBySqlName(StringHelper.toUnderscoreName(name));
	    }
	    return c;
	}
	
	public void afterPropertiesSet() {
		for(SqlSegment seg : sqlSegments) {
			seg.setParams(seg.getParams(this));
		}
	}
	
	public String toString() {
		return "sourceSql:\n"+sourceSql+"\nsql:"+getSql();
	}
	
	private String ibatisSql;
	private String mybatisSql;
}