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
package cn.org.rapid_framework.generator.util.sqlparse;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.stategen.framework.util.Setting;

import cn.org.rapid_framework.generator.provider.db.sql.model.SqlParameter;
import cn.org.rapid_framework.generator.util.StringHelper;


public class StatementCreatorUtils {
    //FIXME scale还未使用
    public static Object getRandomValue(int sqlType,Integer scale)  {
        switch(sqlType) {
            case Types.BIT:;
            case Types.TINYINT:;
            case Types.SMALLINT:;
                return (byte)randomNumber();
            case Types.INTEGER:;
            case Types.BIGINT:;
            case Types.FLOAT:;
            case Types.REAL:;
            case Types.DOUBLE:;
            case Types.NUMERIC:;
            case Types.DECIMAL:;
                return (int)randomNumber();
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return now();
            case Types.VARCHAR:
            case Types.CHAR:
                return randomString(scale);
            case Types.LONGVARCHAR:
            case Types.CLOB:
                return null;
            case -9 :  //Types.NVARCHAR
            case -15:  //Types.NCHAR
            case 2011: //Types.NCLOB
                return null;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return null;
            case Types.NULL:
            case Types.OTHER:
            case Types.BLOB:
                return null;
            case Types.BOOLEAN:
                return false;
            case -10: //CURSOR
            case Integer.MIN_VALUE + 1000: //UNDEFINED
                return null;                
            default:
                return null;
        }
    }
    
    static Random random = new Random(System.currentTimeMillis());
    public static int randomNumber() {
        return Math.abs(random.nextInt());
    }
    
    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    public static String randomString(Integer count) {
        if(count == null || count <= 0) {
            count = 1;
        }
        String result = "";
        for(int i = 0; i < count; i++) {
            result += ""+(Math.abs(randomNumber()) % 9);
        }
        return result;
    }
    
    public static void setParameterRandomValue(PreparedStatement ps, int parameterIndex,int sqlType, Integer scale,String paramName,Map<String, Object> randomValues) throws SQLException { 
        Object v=null;
        String lowerCaseName = paramName.toLowerCase();
         v= getRandomValue(sqlType, scale);
        if(v == null) {
            ps.setNull(parameterIndex, Types.NULL);
        }
        else if (v instanceof Date){
            ps.setObject(parameterIndex, v);
        }
        else {
            if ((sqlType==Types.BIGINT) && (Setting.date_long_field_to_date) &&(lowerCaseName.contains("date") || lowerCaseName.contains("time"))){
                v=new Date();
                ps.setObject(parameterIndex, v);
            } else {
                v=v.toString();
                ps.setString(parameterIndex, (String)v);
            }
        }
//        }else if(v instanceof String) {
//            ps.setString(parameterIndex, (String)v);
//        }else if(v instanceof Timestamp) {
//            ps.setTimestamp(parameterIndex, (Timestamp)v);
//        }else if(v instanceof Number) {
//            ps.setByte(parameterIndex, ((Number)v).byteValue());
//        }else {
//            ps.setObject(parameterIndex, v);
//        }
        randomValues.put(paramName, v);
    }
    
    public static void setRandomParamsValueForPreparedStatement(String executeSql, PreparedStatement ps,
                                                                List<SqlParameter> params,Map<String, Object> randomValues)
                                                                                                   throws SQLException {
        //FIXME 在计算?时需要移除sql comments及 CDATA
        int count = StringHelper.containsCount(SqlParseHelper.removeOrders(executeSql), "?");
        if(count == 0) return;
        
        SqlParameter[] sqlParameters = params.toArray(new SqlParameter[0]);
        for(int parameterIndex = 1; parameterIndex <= count; parameterIndex++){
             int index = parameterIndex - 1;
             if(index < sqlParameters.length) {
                 SqlParameter parameter = sqlParameters[index];
                 
                 StatementCreatorUtils.setParameterRandomValue(ps, parameterIndex, parameter.getSqlType(), parameter.getSize(),parameter.getSqlName(),randomValues);
             }else {
                 ps.setObject(parameterIndex, null); //FIXME 使用assert替换，不可能执行到这里
             }
        }
    }
    
//    private static void setParameterValueInternal(PreparedStatement ps, int paramIndex,
//                                                  int sqlType, Integer scale,
//                                                  Object inValue) throws SQLException {
//
//        int sqlTypeToUse = sqlType;
//        Object inValueToUse = inValue;
//
//        GLogger.debug("Setting SQL statement parameter value: column index "
//                     + paramIndex
//                     + ", parameter value ["
//                     + inValueToUse
//                     + "], value class ["
//                     + (inValueToUse != null ? inValueToUse.getClass().getName() : "null")
//                     + "], SQL type "
//                     + (sqlTypeToUse == SqlTypeValue.TYPE_UNKNOWN ? "unknown" : Integer
//                         .toString(sqlTypeToUse)));
//
//        if (inValueToUse == null) {
//            if (sqlTypeToUse == SqlTypeValue.TYPE_UNKNOWN) {
//                boolean useSetObject = false;
//                sqlTypeToUse = Types.NULL;
//                try {
//                    DatabaseMetaData dbmd = ps.getConnection().getMetaData();
//                    String databaseProductName = dbmd.getDatabaseProductName();
//                    String jdbcDriverName = dbmd.getDriverName();
//                    if (databaseProductName.startsWith("Informix")
//                        || jdbcDriverName.startsWith("Microsoft SQL Server")
//                        || jdbcDriverName.startsWith("Apache Derby Embedded")) {
//                        useSetObject = true;
//                    } else if (databaseProductName.startsWith("DB2")
//                               || jdbcDriverName.startsWith("jConnect")
//                               || jdbcDriverName.startsWith("SQLServer")) {
//                        sqlTypeToUse = Types.VARCHAR;
//                    }
//                } catch (Throwable ex) {
//                    GLogger.debug("Could not check database or driver name,cause:"+ ex);
//                }
//                if (useSetObject) {
//                    ps.setObject(paramIndex, null);
//                } else {
//                    ps.setNull(paramIndex, sqlTypeToUse);
//                }
//            } else {
//                ps.setNull(paramIndex, sqlTypeToUse);
//            }
//        }
//
//        else { // inValue != null
//            if (sqlTypeToUse == Types.VARCHAR || sqlTypeToUse == Types.LONGVARCHAR
//                       || (sqlTypeToUse == Types.CLOB && isStringValue(inValueToUse.getClass()))) {
//                ps.setString(paramIndex, inValueToUse.toString());
//            } else if (sqlTypeToUse == Types.DECIMAL || sqlTypeToUse == Types.NUMERIC) {
//                if (inValueToUse instanceof BigDecimal) {
//                    ps.setBigDecimal(paramIndex, (BigDecimal) inValueToUse);
//                } else if (scale != null) {
//                    ps.setObject(paramIndex, inValueToUse, sqlTypeToUse, scale.intValue());
//                } else {
//                    ps.setObject(paramIndex, inValueToUse, sqlTypeToUse);
//                }
//            } else if (sqlTypeToUse == Types.DATE) {
//                if (inValueToUse instanceof java.util.Date) {
//                    if (inValueToUse instanceof java.sql.Date) {
//                        ps.setDate(paramIndex, (java.sql.Date) inValueToUse);
//                    } else {
//                        ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) inValueToUse)
//                            .getTime()));
//                    }
//                } else if (inValueToUse instanceof Calendar) {
//                    Calendar cal = (Calendar) inValueToUse;
//                    ps.setDate(paramIndex, new java.sql.Date(cal.getTime().getTime()), cal);
//                } else {
//                    ps.setObject(paramIndex, inValueToUse, Types.DATE);
//                }
//            } else if (sqlTypeToUse == Types.TIME) {
//                if (inValueToUse instanceof java.util.Date) {
//                    if (inValueToUse instanceof java.sql.Time) {
//                        ps.setTime(paramIndex, (java.sql.Time) inValueToUse);
//                    } else {
//                        ps.setTime(paramIndex, new java.sql.Time(((java.util.Date) inValueToUse)
//                            .getTime()));
//                    }
//                } else if (inValueToUse instanceof Calendar) {
//                    Calendar cal = (Calendar) inValueToUse;
//                    ps.setTime(paramIndex, new java.sql.Time(cal.getTime().getTime()), cal);
//                } else {
//                    ps.setObject(paramIndex, inValueToUse, Types.TIME);
//                }
//            } else if (sqlTypeToUse == Types.TIMESTAMP) {
//                if (inValueToUse instanceof java.util.Date) {
//                    if (inValueToUse instanceof java.sql.Timestamp) {
//                        ps.setTimestamp(paramIndex, (java.sql.Timestamp) inValueToUse);
//                    } else {
//                        ps.setTimestamp(paramIndex, new java.sql.Timestamp(
//                            ((java.util.Date) inValueToUse).getTime()));
//                    }
//                } else if (inValueToUse instanceof Calendar) {
//                    Calendar cal = (Calendar) inValueToUse;
//                    ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()),
//                        cal);
//                } else {
//                    ps.setObject(paramIndex, inValueToUse, Types.TIMESTAMP);
//                }
//            } else if (sqlTypeToUse == SqlTypeValue.TYPE_UNKNOWN) {
//                if (isStringValue(inValueToUse.getClass())) {
//                    ps.setString(paramIndex, inValueToUse.toString());
//                } else if (isDateValue(inValueToUse.getClass())) {
//                    ps.setTimestamp(paramIndex, new java.sql.Timestamp(
//                        ((java.util.Date) inValueToUse).getTime()));
//                } else if (inValueToUse instanceof Calendar) {
//                    Calendar cal = (Calendar) inValueToUse;
//                    ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()));
//                } else {
//                    // Fall back to generic setObject call without SQL type
//                    // specified.
//                    ps.setObject(paramIndex, inValueToUse);
//                }
//            } else {
//                // Fall back to generic setObject call with SQL type specified.
//                ps.setObject(paramIndex, inValueToUse, sqlTypeToUse);
//            }
//        }
//    }

//    /**
//     * Check whether the given value can be treated as a String value.
//     */
//    private static boolean isStringValue(Class<?> inValueType) {
//        // Consider any CharSequence (including JDK 1.5's StringBuilder) as String.
//        return (CharSequence.class.isAssignableFrom(inValueType) ||
//                StringWriter.class.isAssignableFrom(inValueType));
//    }
//
//    /**
//     * Check whether the given value is a <code>java.util.Date</code>
//     * (but not one of the JDBC-specific subclasses).
//     */
//    private static boolean isDateValue(Class<?> inValueType) {
//        return (java.util.Date.class.isAssignableFrom(inValueType) &&
//                !(java.sql.Date.class.isAssignableFrom(inValueType) ||
//                        java.sql.Time.class.isAssignableFrom(inValueType) ||
//                        java.sql.Timestamp.class.isAssignableFrom(inValueType)));
//    }
}
