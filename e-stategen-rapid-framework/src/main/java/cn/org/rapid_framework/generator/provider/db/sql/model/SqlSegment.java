package cn.org.rapid_framework.generator.provider.db.sql.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.sqlparse.NamedParameterUtils;
import cn.org.rapid_framework.generator.util.sqlparse.ParsedSql;

/**
 *  SQL片段, 代表一句SQL include 其它的sql片段. 如ibatis中的 <include refid='User.Where'/> 
 *  
 *  @see Sql
 **/
public class SqlSegment {
	/** sql segment ID */
	public String id;
	/** 原始的 被 include的一段 SQL */
	public String rawIncludeSql;
	/** 已经经过处理解析的一段SQL */
	public String parsedIncludeSql;
	/** 这段include sql包含的参数列表 */
	public Set<SqlParameter> params;
	
	public SqlSegment(){}
	
	public SqlSegment(String id, String rawIncludeSql, String parsedIncludeSql) {
		super();
		setId(id);
		this.rawIncludeSql = rawIncludeSql;
		this.parsedIncludeSql = parsedIncludeSql;
	}
	
	//TODO 增加如果参数数是1,则不生成  SqlSegemnt,此处也要修改对1的特殊控制
	public Set<SqlParameter> getParams(Sql sql) {
		Set<SqlParameter> result = new LinkedHashSet();
		for(String paramName : getParamNames()) {
			SqlParameter p = sql.getParam(paramName);
			if(p == null) throw new IllegalArgumentException("not found param on sql:"+parsedIncludeSql+" with name:"+paramName+" for sqlSegment:"+id); //是否不该扔异常
			result.add(p);
		}
		return result;
	}
	public List<String> getParamNames() {
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(parsedIncludeSql); // FIXME 没有执行替换?为 :name的动作
		return parsedSql.getParameterNames();
	}
	public String getClassName() {
		return StringHelper.toJavaClassName(id.replace(".", "_").replace("-", "_"));
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		if(StringHelper.isBlank(id)) throw new IllegalArgumentException("id must be not blank");
		this.id = id;
	}
	public String getRawIncludeSql() {
		return rawIncludeSql;
	}
	public void setRawIncludeSql(String rawIncludeSql) {
		this.rawIncludeSql = rawIncludeSql;
	}
	public String getParsedIncludeSql() {
		return parsedIncludeSql;
	}
	public void setParsedIncludeSql(String parsedIncludeSql) {
		this.parsedIncludeSql = parsedIncludeSql;
	}
	public Set<SqlParameter> getParams() {
		return params;
	}
	public void setParams(Set<SqlParameter> params) {
		this.params = params;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean isGenerateParameterObject() {
		if(getParamNames().size() > 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SqlSegment other = (SqlSegment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
