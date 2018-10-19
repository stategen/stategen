/*
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
package org.stategen.framework.generator.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.stategen.framework.lite.CaseInsensitiveHashMap;
import org.stategen.framework.util.CollectionUtil;


/**
 * The Class CompatibleHelper.
 */
public class CompatibleHelper {

    /**   储存Field与的旧的Pojo中的entity大小写映射关系，也即当旧的entity存在时，大小写参照旧的*/
    final static Map<String, String> OLD_FIELDS = new CaseInsensitiveHashMap<String>();
    
    /**存储复杂参数与简单参数类型的对照关系*/
    final static Map<String, String> JAVA_TYPE_SIMPLE =new HashMap<String, String>();

    /**
     * Gets the column name.
     *
     * @param columnName the column name
     * @return the column name
     */
    public static String getColumnName(String columnName) {
        String result = null;
        if (CollectionUtil.isNotEmpty(OLD_FIELDS)) {
            result = OLD_FIELDS.get(columnName);
        }
        if (result == null || result.isEmpty()) {
            result = columnName;
        }
        return result;
    }

    /**
     * Gets the first upper column name.
     *
     * @param columnName the column name
     * @return the first upper column name
     */
    public static String getFirstUpperColumnName(String columnName) {
        String result = getColumnName(columnName);
        if (result == null || result.isEmpty()) {
            return result;
        }

        if (!Character.isUpperCase(result.charAt(0))) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }
        return result;
    }

    public static String replaceAll2(String input, String regex, String replacement) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer(input.length()+10);
        boolean result = m.find();
        while (result) {
            m.appendReplacement(sb, replacement);
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String replaceFields(String sql) {
        if (CollectionUtil.isNotEmpty(OLD_FIELDS)) {
            for (String fieldName : OLD_FIELDS.values()) {
                String fName = "#" + fieldName + "#";
                sql = replaceAll2(sql, fName, fName);
            }
        }
        return sql;
    }

    public static String replaceProperties(String sql) {
        if (CollectionUtil.isNotEmpty(OLD_FIELDS)) {
            for (String fieldName : OLD_FIELDS.values()) {
                String fName = "\"" + fieldName + "\"";
                sql = replaceAll2(sql, fName, fName);
            }
        }
        return sql;
    }
    public static String replaceColumnCase(String sql) {
        if (CollectionUtil.isNotEmpty(OLD_FIELDS)) {
            for (String fieldName : OLD_FIELDS.values()) {
                String fName = "\"" + fieldName + "\"";
                sql = replaceAll2(sql, fName, fName);
                fName = "#" + fieldName + "#";
                sql = replaceAll2(sql, fName, fName);
            }
        }
        return sql;
    }
    

    public static String reaplce2_(String name) {
        StringBuffer sb = new StringBuffer(name.length() + 10);
        boolean lastUpper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0 && !lastUpper) {
                    sb.append('_');
                }
                lastUpper = true;
            } else {
                lastUpper = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    
    
    
    public static String type(String orgType){
        String result =JAVA_TYPE_SIMPLE.get(orgType);
        if (result==null){
            result =orgType;
        }
        return result ;
    }
    
    public static void regType(String orgType,String simpleType){
        JAVA_TYPE_SIMPLE.put(orgType, simpleType);
    }

}
