package org.stategen.framework.ibatis.util;

import java.util.List;

/***为了兼容ibatis和mybatis*/
public interface SqlDaoSupport {

    public <T> T selectOne(String statementName, Object params);

    public <T> List<T> selectList(String statementName, Object params);
    
}
