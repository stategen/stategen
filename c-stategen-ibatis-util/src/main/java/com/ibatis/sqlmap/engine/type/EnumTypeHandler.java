package com.ibatis.sqlmap.engine.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.stategen.framework.lite.ValuedEnum;
import org.stategen.framework.util.EnumUtil;

/**
 * String implementation of TypeHandler
 * 该类用于覆盖ibatis中原有的EnumTypeHandler,当值为非string时，如1,2,3,ibatis无法获取和存取枚举，需要用该类来转换.
 */
public class EnumTypeHandler<T extends Enum<T>> extends BaseTypeHandler implements TypeHandler {

    private Class<T> type;
    
    public EnumTypeHandler(Class<T> type) {
        this.type = type;
    }

    public void setParameter(PreparedStatement ps, int i, Object parameter, String jdbcType) throws SQLException {
        if (ValuedEnum.class.isAssignableFrom(type)) {
            Object value = ((ValuedEnum<?>) parameter).getValue();
            ps.setObject(i, value);
            return;
        }
        ps.setString(i, parameter.toString());
    }

    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        Object s = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return getResultFormValuedCache(s);
        }
    }

    public Object getResultFormValuedCache(Object s) {
        return EnumUtil.valueOf(type, (String) s);
    }

    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        Object s = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return getResultFormValuedCache(s);
        }
    }

    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object s = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return Enum.valueOf(type, (String) s);
        }
    }

    public Object valueOf(String s) {
        return Enum.valueOf(type, (String) s);
    }
    /*
     * 这个给spring调用，将EnumTypeHandler最先加载
     */
    public void setCall(String call){
        //skip 
    }

}