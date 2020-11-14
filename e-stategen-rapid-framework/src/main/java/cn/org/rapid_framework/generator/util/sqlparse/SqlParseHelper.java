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

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.provider.db.table.TableFactory.DatabaseMetaDataUtils;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.IOHelper;
import cn.org.rapid_framework.generator.util.StringHelper;

public class SqlParseHelper {

    public static class NameWithAlias {
        private String name;
        private String alias;

        public NameWithAlias(String name, String alias) {
            if (name == null)
                throw new IllegalArgumentException("name must be not null");
            if (name.trim().indexOf(' ') >= 0)
                throw new IllegalArgumentException("error name:" + name);
            if (alias != null && alias.trim().indexOf(' ') >= 0)
                throw new IllegalArgumentException("error alias:" + alias);
            this.name = name.trim();
            this.alias = alias == null ? null : alias.trim();
        }

        public String getName() {
            return name;
        }

        public String getAlias() {
            return StringUtil.isBlank(alias) ? getName() : alias;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NameWithAlias other = (NameWithAlias) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        public String toString() {
            return StringUtil.isBlank(alias) ? name : name + " as " + alias;
        }
    }

    static Pattern fromRegex = Pattern.compile("(\\sfrom\\s+)([,\\w]+)", Pattern.CASE_INSENSITIVE);
    static Pattern join = Pattern.compile("(join\\s+)(\\w+)(as)?(\\w*)", Pattern.CASE_INSENSITIVE);
    static Pattern update = Pattern.compile("(\\s*update\\s+)(\\w+)", Pattern.CASE_INSENSITIVE);
    public static Pattern insert = Pattern.compile("(\\s*insert\\s+into\\s+)(\\w+)", Pattern.CASE_INSENSITIVE);

    public final static String common_operator = "[=<>!]{0,}";

    /** 从一条sql中解释中包含的表  */
    public static Set<NameWithAlias> getTableNamesByQuery(String sql) {
        sql = removeSqlComments(StringHelper.removeXMLCdataTag(sql)).trim();
        Set<NameWithAlias> result = new LinkedHashSet<NameWithAlias>();
        Matcher m = null;

        if (SqlTypeChecker.isUpdateSql(sql)) {
            m = update.matcher(sql);
            if (m.find()) {
                result.add(new NameWithAlias(m.group(2), null));
                return result;
            }
        }

        if (SqlTypeChecker.isInsertSql(sql)) {
            m = insert.matcher(sql);
            if (m.find()) {
                result.add(new NameWithAlias(m.group(2), null));
                return result;
            }
        }

        //最后做 from
        m = fromRegex.matcher(sql);
        if (m.find()) {
            String from = getFromClauses(sql);
            if (from.matches("(?ims).*\\sfrom\\s.*")) {
                return getTableNamesByQuery(from);
            }
            if (from.indexOf(',') >= 0) {
                //逗号分隔的多表
                String[] array = StringHelper.tokenizeToStringArray(from, ",");
                for (String s : array) {
                    result.add(parseTableSqlAlias(s));
                }
            } else if (StringHelper.indexOfByRegex(from.toLowerCase(), "\\sjoin\\s") >= 0) {
                //join的多表
                String removedFrom = StringHelper.removeMany(from.toLowerCase(), "inner", "full", "left", "right", "outer");
                String[] joins = removedFrom.split("join");
                for (String s : joins) {
                    result.add(parseTableSqlAlias(s));
                }
            } else {
                //单表
                result.add(parseTableSqlAlias(from));
            }
        }

        return result;
    }

    /** 解析sql的别名,如 user as u,将返回 user及u */
    public static NameWithAlias parseTableSqlAlias(String str) {
        try {
            str = str.trim();
            String[] array = str.split("\\sas\\s");
            if (array.length >= 2 && str.matches("^[\\w_]+\\s+as\\s+[_\\w]+.*")) {
                return new NameWithAlias(array[0], StringHelper.tokenizeToStringArray(array[1], " \n\t")[0]);
            }
            array = StringHelper.tokenizeToStringArray(str, " \n\t");
            if (array.length >= 2 && str.matches("^[\\w_]+\\s+[_\\w]+.*")) {
                return new NameWithAlias(array[0], array[1]);
            }
            return new NameWithAlias(StringHelper.getByRegex(str.trim(), "^[\\w_]+"),
                StringHelper.getByRegex(str.trim(), "^[\\w_]+\\s+([\\w_]+)", 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("parseTableSqlAlias error,str:" + str, e);
        }
    }

    //	static Pattern p = Pattern.compile("(:)(\\w+)(\\|?)([\\w.]+)");
    public static String getParameterClassName(String sql, String paramName) {
        Pattern p = Pattern.compile("(:)(" + paramName + ")(\\|?)([\\w.]+)");
        Matcher m = p.matcher(sql);
        if (m.find()) {
            return m.group(4);
        }
        return null;
    }

    public static String getColumnNameByRightCondition(String sql, String column) {
        //		String operator = "[=<>!]{1,2}";
        String operator = "\\s*[=<>!]{1,2}\\s*";
        String result = getColumnNameByRightCondition(sql, column, operator);

        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+?like\\s+(.(?!like)){1,}?");
        }

        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+like\\s+");
        }

        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+between\\s+");
        }
        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+between\\s.+\\sand\\s+");
        }
        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+not\\s+in\\s*\\(\\s+");
        }
        if (result == null) {
            result = getColumnNameByRightCondition(sql, column, "\\s+in\\s*\\(\\s+");
        }
        if (result == null) {
            result = getColumnNameByRightConditionWithFunction(sql, column, operator);
        }

        return result;
    }

    private static String getColumnNameByRightCondition(String sql, String column, String operator) {
        Pattern p = Pattern.compile("(\\w+)" + operator + "[:#$&\\{]?" + column + "[\\}#$]?", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    //有函数的表达式提取
    private static String getColumnNameByRightConditionWithFunction(String sql, String column, String operator) {
        Pattern p = Pattern.compile("(\\w+)\\s*" + operator + "\\s*\\w+\\([,\\w]*[:#$&\\{]?" + column + "[\\}#$]?[,\\w]*\\)",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static String convert2NamedParametersSql(String sql, String prefix, String suffix) {
        return new NamedSqlConverter(prefix, suffix).convert2NamedParametersSql(sql);
    }
    
    

    public static String replaceInWithIterateLabel(String sql) {
//        Pattern p = Pattern.compile("(\\w+)\\s*" + "in\\s+\\?|(#w+#)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Pattern p = Pattern.compile("(\\w+)\\s+" + "in\\s+\\?", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String segment = m.group(0);
            String columnSqlName = m.group(1);

            String paramName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(columnSqlName));
            String listParamName =paramName+"s";
            
            surroundWithIterate(m, sb, segment, listParamName);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void surroundWithIterate(Matcher m, StringBuffer sb, String segment, String listParamName) {
        String replacedSegment = segment.replaceAll("\\?|#\\w+#", "\n            <iterate property=\""+listParamName+"\" conjunction=\",\" open=\"(\" close=\")\">\n            #"+ listParamName +"[]#\n            </iterate>");
        m.appendReplacement(sb, replacedSegment);
    }
    
    
    
    public static String replaceInWithIterateLabelHasParamName(String sql) {
        Pattern p = Pattern.compile("(\\w+)\\s+" + "in\\s+#\\w+#", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String segment = m.group(0); //inter_code in #interCodeList#
            Pattern paramPattern =Pattern.compile("#\\w+#", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher paramMather =paramPattern.matcher(segment);
            String listParamName =null;
            if (paramMather.find()) {
                listParamName =paramMather.group(0);
            }
            
            if (StringUtil.isNotEmpty(listParamName)) {
                listParamName =listParamName.substring(1,listParamName.length()-1);
                surroundWithIterate(m, sb, segment, listParamName);
            }
        
        }
        m.appendTail(sb);
        String result = sb.toString();
        return result;
    }

    /**
    * 将sql从占位符转换为命名参数,如 select * from user where id =? ,将返回: select * from user where id = #id#
    * @param includeSqls
    * @param prefix 命名参数的前缀
    * @param suffix 命名参数的后缀
    * @return
    */
    public static class NamedSqlConverter {
        private String prefix;
        private String suffix;

        public NamedSqlConverter(String prefix, String suffix) {
            if (prefix == null)
                throw new NullPointerException("'prefix' must be not null");
            if (suffix == null)
                throw new NullPointerException("'suffix' must be not null");
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String convert2NamedParametersSql(String sql) {
            if (sql.trim().toLowerCase().matches("(?is)\\s*insert\\s+into\\s+.*")) {
                return replace2NamedParameters(replaceInsertSql2NamedParameters(sql));
            } else {
                return replace2NamedParameters(sql);
            }
        }

        private String replace2NamedParameters(String sql) {
            //String replacedSql = replace2NamedParametersByOperator(sql,"[(=<>!]{1,2}"); //缺少oracle的^=运算符:  !=,<>,^=:不等于 
                                                                       //"[=<>!]{0,}"
            String replacedSql = replace2NamedParametersByOperator(sql, common_operator); //缺少oracle的^=运算符:  !=,<>,^=:不等于 
            replacedSql = replace2NamedParametersByOperator(replacedSql, "\\s+like\\s+"); // like

            //            replacedSql = replace2NamedParametersByOperator(replacedSql,"\\s+not\\s+in\\s+\\("); // not in
            //            replacedSql = replace2NamedParametersByOperator(replacedSql,"\\s+in\\s+\\("); // in
            return replacedSql;
        }

        /***
         * 因为dalgen 解析insert的ibatis很弱，这里临时做一下动态解析
         * 
         * @param array
         * @param seperator
         * @return
         */
        public String join(String[] array, String seperator) {
            if (array == null)
                return null;
            StringBuilder result = new StringBuilder();
            int i = -1;
            boolean hasLt = false;
            while (i < array.length - 1) {
                i += 1;
                String value = array[i];
                if (!hasLt) {
                    hasLt = value.indexOf('>') > 0;
                }
                if ("prepend=\"".equals(value) && ((i + 1 <= array.length - 1) && "\"".equals(array[i + 1]))) {
                    result.append("prepend=\",\" ");
                    i = i + 1;
                    continue;
                }
                addBreak(hasLt, array, result, i, value);
                result.append(value);

                if (i != array.length - 1) {
                    if (value.indexOf('<') > -1 || value.indexOf('>') > -1 || value.indexOf('#') > -1) {
                        result.append(" ");
                        continue;
                    }

                    if ((i + 1 <= array.length - 1) && array[i + 1].indexOf("<") > -1) {
                        result.append(" ");
                        continue;
                    }
                    result.append(seperator);
                }
            }
            return result.toString();
        }

        private boolean addBreak(boolean hasLt, String[] array, StringBuilder result, int i, String value) {
            if (i > 0) {
                value = value.trim();
                if (value.charAt(0) == '<') {
                    String lastValue = array[i - 1].trim();
                    if (!hasLt || lastValue.charAt(lastValue.length() - 1) == '>') {
                        result.append("\n            ");
                        return true;
                    }
                }
            }
            return false;
        }

        private String replaceInsertSql2NamedParameters(String sql) {
            if (sql.matches("(?is)\\s*insert\\s+into\\s+\\w+\\s+values\\s*\\(.*\\).*")) {
                if (sql.indexOf("?") >= 0) {
                    throw new IllegalArgumentException("无法解析的insert sql:" + sql + ",values()段没有包含疑问号?");
                } else {
                    return sql;
                }
            }
            //FIXME: insert into user_info(user,pwd) values (length(?),?); 将没有办法解析,因为正则表达式由于length()函数匹配错误.
            //需要处理 <selectKey>问题
            String selectKeyPattern = "<selectKey.*";
            String insertPattern = "\\s*insert\\s+into.*\\((.*?)\\).*values.*?\\((.*)\\).*";
            Matcher m = matcher(sql, Pattern.DOTALL | Pattern.CASE_INSENSITIVE, insertPattern + selectKeyPattern, insertPattern);
            if (m != null) {
                //String[] columns = StringHelper.tokenizeToStringArray(m.group(1),", \t\n\r\f");
                String[] columns = StringHelper.tokenizeToStringArray(m.group(1), ", \t\n\r\f");
                //columns =StringHelper.removeBrks(columns);
                String[] values = StringHelper.tokenizeToStringArray(m.group(2), ", \t\n\r\f");
                //values =StringHelper.removeBrks(values);

                if (columns.length != values.length) {
                    throw new IllegalArgumentException("insert 语句的插入列与值列数目不相等,sql:" + sql + " \ncolumns:" + StringHelper.join(columns, ",")
                                                       + " \nvalues:" + StringHelper.join(values, ","));
                }

                for (int i = 0; i < columns.length; i++) {
                    String column = columns[i];
                    String paranName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(column));
                    values[i] = values[i].replace("?", prefix + paranName + suffix);
                    ;
                }

                //因为dalgen 解析insert的ibatis很弱，这里临时做一下动态解析
                String resultSql = StringHelper.replace(m.start(2), m.end(2), sql, join(values, ","));
                return resultSql;
            }
            throw new IllegalArgumentException("无法解析的sql:" + sql + ",不匹配正则表达式:" + insertPattern);
        }

        private static Matcher matcher(String input, int flags, String... regexArray) {
            for (String regex : regexArray) {
                Pattern p = Pattern.compile(regex, flags);
                Matcher m = p.matcher(input);
                if (m.find()) {
                    return m;
                }
            }
            return null;
        }

        private String replace2NamedParametersByOperator(String sql, String operator) {
            Pattern p = Pattern.compile("(\\w+)\\s*" + operator + "\\s*\\?", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(sql);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String segment = m.group(0);
                String columnSqlName = m.group(1);

                String paramName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(columnSqlName));

                String replacedSegment = segment.replace("?", prefix + paramName + suffix);
                m.appendReplacement(sb, replacedSegment);
            }
            m.appendTail(sb);
            return sb.toString();
        }
    }

    /**
     * 美化sql
     * 
     * @param sql
     * @return
     */
    public static String getPrettySql(String sql) {
        try {
            if (IOHelper.readLines(new StringReader(sql)).size() > 1) {
                return sql;
            } else {
                return StringHelper.replace(StringHelper.replace(sql, "from", "\n\tfrom"), "where", "\n\twhere");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 去除select 子句，未考虑union的情况
     * 
     * @param sql
     * @return
     */
    public static String removeSelect(String sql) {
        if (StringUtil.isBlank(sql))
            throw new IllegalArgumentException("sql must be not empty");
        int beginPos = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
        if (beginPos == -1)
            throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
        return sql.substring(beginPos);
    }

    public static String toCountSqlForPaging(String sql, String countQueryPrefix) {
        if (StringUtil.isBlank(sql))
            throw new IllegalArgumentException("sql must be not empty");
        if (StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sgroup\\s+by\\s") >= 0) {
            return countQueryPrefix + " from (" + sql + " ) forGroupByCountTable";
        } else {
            int selectBeginOps = StringHelper.indexOfByRegex(sql.toLowerCase(), "select\\s");
            int fromBeingOps = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
            if (fromBeingOps == -1)
                throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
            return sql.substring(0, selectBeginOps) + countQueryPrefix + sql.substring(fromBeingOps);
        }
    }

    public static String getSelect(String sql) {
        if (StringUtil.isBlank(sql))
            throw new IllegalArgumentException("sql must be not empty");
        int beginPos = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
        if (beginPos == -1)
            throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
        return sql.substring(0, beginPos);
    }

    /** 得到sql的from子句 */
    public static String getFromClauses(String sql) {
        String lowerSql = sql.toLowerCase();
        int fromBegin = StringHelper.indexOfByRegex(lowerSql, "\\sfrom\\s");
        if (fromBegin <= 0)
            throw new IllegalArgumentException("error from begin:" + fromBegin);

        int fromEnd = lowerSql.indexOf("where");
        if (fromEnd == -1) {
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sgroup\\s+by\\s");
        }
        if (fromEnd == -1) {
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\shaving\\s");
        }
        if (fromEnd == -1) {
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sorder\\s+by\\s");
        }
        if (fromEnd == -1) {
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sunion\\s");
            ;
        }
        if (fromEnd == -1) {
            //SELECT Date FROM Store_Information
            //INTERSECT
            //SELECT Date FROM Internet_Sales
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sintersect\\s");
        }
        if (fromEnd == -1) {
            //SELECT Date FROM Store_Information
            //MINUS
            //SELECT Date FROM Internet_Sales
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sminus\\s");
        }
        if (fromEnd == -1) {
            //SELECT Date FROM Store_Information
            //EXCEPT
            //SELECT Date FROM Internet_Sales			
            fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sexcept\\s");
        }
        if (fromEnd == -1) {
            fromEnd = sql.length();
        }
        return sql.substring(fromBegin + " from ".length(), fromEnd);
    }

    public static String removeSqlComments(String sql) {
        if (sql == null)
            return null;
        return sql.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("--.*", "");
    }

    /**
     * 去除orderby 子句
     * @param sql
     * @return
     */
    public static String removeOrders(String sql) {
        return sql.replaceAll("(?is)order\\s+by[\\w|\\W|\\s|\\S]*", "");
    }

    public static String replaceWhere(String sql) {
        return sql.toString().replaceAll("(?i)\\swhere\\s+(and|or)\\s", " WHERE ");
    }

    public static long startTimes = System.currentTimeMillis();

    public static void setRandomParamsValueForPreparedStatement(String sql, PreparedStatement ps) throws SQLException {
        int count = StringHelper.containsCount(sql, "?");
        if (DatabaseMetaDataUtils.isOracleDataBase(ps.getConnection().getMetaData())) {
            if (SqlTypeChecker.isSelectSql(sql)) {
                for (int i = 1; i <= count; i++) {
                    ps.setObject(i, null);
                }
                return;
            }
        }
        for (int i = 1; i <= count; i++) {
            long random = new Random(System.currentTimeMillis() + startTimes++).nextInt() * 30 + System.currentTimeMillis() + startTimes;
            try {
                ps.setLong(i, random);
            } catch (SQLException e) {
                try {
                    ps.setInt(i, (int) random % Integer.MAX_VALUE);
                } catch (SQLException e1) {
                    try {
                        ps.setString(i, "" + random);
                    } catch (SQLException e2) {
                        try {
                            ps.setTimestamp(i, new java.sql.Timestamp(random));
                        } catch (SQLException e3) {
                            try {
                                ps.setDate(i, new java.sql.Date(random));
                            } catch (SQLException e6) {
                                try {
                                    ps.setString(i, "" + (int) random);
                                } catch (SQLException e4) {
                                    try {
                                        ps.setString(i, "" + (short) random);
                                    } catch (SQLException e82) {
                                        try {
                                            ps.setString(i, "" + (byte) random);
                                        } catch (SQLException e32) {
                                            try {
                                                ps.setNull(i, java.sql.Types.OTHER);
                                            } catch (SQLException error) {
                                                warn(sql, i, error);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private static void warn(String sql, int i, SQLException error) {
        GLogger.warn("error on set parametet index:" + i + " cause:" + error + " sql:" + sql);
    }
}
