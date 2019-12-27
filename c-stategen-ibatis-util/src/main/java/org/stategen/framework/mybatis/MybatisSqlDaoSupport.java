package org.stategen.framework.mybatis;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.stategen.framework.ibatis.util.SqlDaoSupport;
/**
 * MybatisSqlDaoSupport
 * 该类为兼容ibatis和mybatis,不可以修改
 * </pre>
 */
public class MybatisSqlDaoSupport extends SqlSessionDaoSupport implements SqlDaoSupport {
    
    public <T> int update(String statementName, Object params) {
        int effectCount = getSqlSession().update(statementName, params);
        return effectCount;
    }

    public <T> void insert(String statementName, T t) {
        getSqlSession().insert(statementName, t);
    }

    public <T> T selectOne(String statementName, Object params) {
        return getSqlSession().selectOne(statementName, params);
    }

    public <T> List<T> selectList(String statementName, Object params) {
        return getSqlSession().selectList(statementName, params);
    }

}

