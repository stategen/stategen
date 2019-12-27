package org.stategen.framework.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.ibatis.type.JdbcType;

public class DateTypeHandler extends org.apache.ibatis.type.DateTypeHandler {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DateTypeHandler.class);
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter,
                                    JdbcType jdbcType) throws SQLException {
        super.setNonNullParameter(ps, i, parameter, JdbcType.TIMESTAMP);
    }
    
    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return super.getNullableResult(rs, columnIndex);
    }
    
    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return super.getNullableResult(cs, columnIndex);
    }
    
    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return super.getNullableResult(rs, columnName);
    }
}
