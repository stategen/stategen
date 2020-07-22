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
package cn.org.rapid_framework.generator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.stategen.framework.generator.util.CompatibleHelper;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.GeneratorConstants;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.provider.db.table.model.Column.EnumMetaDada;

/**
 * 
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class StringHelper {
    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";


    public static String removeCrlf(String str) {
        if (str == null)
            return null;
        return StringHelper.join(StringHelper.tokenizeToStringArray(str, "\t\n\r\f"), " ");
    }

    private static final Map<String, String> XML = new HashMap<String, String>();
    private static final Map<Character, String> UN_XML = new HashMap<Character, String>();
    static {
        XML.put("apos", "'");
        XML.put("quot", "\"");
        XML.put("amp", "&");
        XML.put("lt", "<");
        XML.put("gt", ">");
        
        for (Entry<String, String> entry :XML.entrySet()){
            UN_XML.put(entry.getValue().charAt(0), "&"+entry.getKey()+";");
        }
    }
    
    public static String toXML(String dest){
        if (StringUtil.isEmpty(dest)){
            return dest;
        }
        StringBuilder sb =new StringBuilder(dest.length()*2);
        for (char c : dest.toCharArray()) {
            String xml = UN_XML.get(c);
            if (xml!=null){
                sb.append(xml);
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    

    /**
     * 生成 路径值,如 pkg=com.company.project 将生成 pkg_dir=com/company/project的值
     * 
     * @param map
     * @return
     */
    public static Map<String, String> getDirValuesMap(Map<String, Object> map) {
        Map<String, String> dirValues = new HashMap<String, String>();
        Set<String> keys = map.keySet();
        for (Object key : keys) {
            Object value = map.get(key);
            if (key instanceof String && value instanceof String) {
                String dirKey = key + "_dir";
                String dirValue = value.toString().replace('.', '/');
                dirValues.put(dirKey, dirValue);
            }
        }
        return dirValues;
    }

    public static String unescapeXml(String str) {
        if (str == null)
            return null;
        for (String key : XML.keySet()) {
            String value = XML.get(key);
            str = StringHelper.replace(str, "&" + key + ";", value);
        }
        return str;
    }

    public static String escapeXml(String str) {
        if (str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String escapedStr = getEscapedStringByChar(c);
            if (escapedStr == null)
                sb.append(c);
            else
                sb.append(escapedStr);
        }
        return sb.toString();
    }

    public static String escapeXml(String str, String escapeChars) {
        if (str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (escapeChars.indexOf(c) < 0) {
                sb.append(c);
                continue;
            }
            String escapedStr = getEscapedStringByChar(c);
            if (escapedStr == null)
                sb.append(c);
            else
                sb.append(escapedStr);
        }
        return sb.toString();
    }

    private static String getEscapedStringByChar(char c) {
        String escapedStr = null;
        for (String key : XML.keySet()) {
            String value = XML.get(key);
            if (c == value.charAt(0)) {
                escapedStr = "&" + key + ";";
            }
        }
        return escapedStr;
    }

    public static String removePrefix(String str, String prefix) {
        return removePrefix(str, prefix, false);
    }

    public static String removePrefix(String str, String prefix, boolean ignoreCase) {
        if (str == null)
            return null;
        if (prefix == null)
            return str;
        if (ignoreCase) {
            if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
                return str.substring(prefix.length());
            }
        } else {
            if (str.startsWith(prefix)) {
                return str.substring(prefix.length());
            }
        }
        return str;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String getExtension(String str) {
        if (str == null)
            return null;
        int i = str.lastIndexOf('.');
        if (i >= 0) {
            return str.substring(i + 1);
        }
        return null;
    }

    public static String insertBefore(String content, String compareToken, String insertString) {
        if (content.indexOf(insertString) >= 0)
            return content;
        int index = content.indexOf(compareToken);
        if (index >= 0) {
            return new StringBuilder(content).insert(index, insertString).toString();
        } else {
            throw new IllegalArgumentException("not found insert location by compareToken:" + compareToken + " content:" + content);
        }
    }

    public static String insertAfter(String content, String compareToken, String insertString) {
        if (content.indexOf(insertString) >= 0)
            return content;
        int index = content.indexOf(compareToken);
        if (index >= 0) {
            return new StringBuilder(content).insert(index + compareToken.length(), insertString).toString();
        } else {
            throw new IllegalArgumentException("not found insert location by compareToken:" + compareToken + " content:" + content);
        }
    }

    /**
     * Count the occurrences of the substring in string s.
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    public static boolean contains(String str, String... keywords) {
        if (str == null)
            return false;
        if (keywords == null)
            throw new IllegalArgumentException("'keywords' must be not null");

        for (String keyword : keywords) {
            if (str.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String defaultString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static String defaultIfEmpty(Object value, String defaultValue) {
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        return value.toString();
    }

//    public static String makeAllWordFirstLetterUpperCase(String sqlName) {
//        sqlName = CompatibleHelper.reaplce2_(sqlName);
//        String[] strs = sqlName.toLowerCase().split("_");
//        String result = "";
//        String preStr = "";
//        for (int i = 0; i < strs.length; i++) {
//            if (preStr.length() == 1) {
//                result += strs[i];
//            } else {
//                result += capitalize(strs[i]);
//            }
//            preStr = strs[i];
//        }
//        return result;
//    }

    public static String makeAllWordFirstLetterUpperCase(String sqlName) {
        sqlName = CompatibleHelper.reaplce2_(sqlName);
        sqlName= sqlName.toLowerCase();
        StringBuilder sb =new StringBuilder(sqlName.length());
        boolean append =false;
        boolean findUnd =true;
        for (int i = 0; i < sqlName.length(); i++) {
            char c = sqlName.charAt(i);
            if (c=='_'){
                if (!append){
                    sb.append(c);
                    append=true;
                } else{
                    findUnd=true;
                }
                continue;
            }

            if (findUnd){
                c =Character.toUpperCase(c);
            }
            findUnd=false;
            append =true;
            sb.append(c);
        }
        String result =sb.toString();
        return  result;
    }

    public static int indexOfByRegex(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.start();
        }
        return -1;
    }

    public static String toJavaVariableName(String str) {
        return uncapitalize(toJavaClassName(str));
    }

    /**
     * 将一个数据库名称转换为 Java 变量名称,如 user_info => userInfo
     * @param sqlName
     * @return
     */
    public static String toJavaClassName(String sqlName) {
        return toJavaClassName(sqlName, GeneratorProperties.getBoolean(GeneratorConstants.TABLE_NAME_SINGULARIZE));
    }

    /**
     * 将一个数据库名称转换为 Java ClassName,如 user_info => UserInfo
     * @param sqlName
     * @param singularize 是否将复数转换为单数,如 customers => customer
     * @return
     */
    public static String toJavaClassName(String sqlName, boolean singularize) {
        String processedSqlName = removeTableSqlNamePrefix(sqlName);
        if (singularize) {
            processedSqlName = singularize(processedSqlName);
        }
        return makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(processedSqlName));
    }

    public static String removeTableSqlNamePrefix(String sqlName) {
        String[] prefixs = GeneratorProperties.getStringArray(GeneratorConstants.TABLE_REMOVE_PREFIXES);
        for (String prefix : prefixs) {
            String removedPrefixSqlName = StringHelper.removePrefix(sqlName, prefix, true);
            if (!removedPrefixSqlName.equals(sqlName)) {
                return removedPrefixSqlName;
            }
        }
        return sqlName;
    }

    public static String getJavaClassSimpleName(String clazz) {
        if (clazz == null)
            return null;
        if (clazz.lastIndexOf(".") >= 0) {
            return clazz.substring(clazz.lastIndexOf(".") + 1);
        }
        return clazz;
    }

    public static String toSimple(String clzz) {
        StringBuilder sb = new StringBuilder();
        String[] namespaces = clzz.split("<");
        for (int i = 0; i < namespaces.length; i++) {
            String namespace = namespaces[i];
            if (i > 0) {
                sb.append('<');
            }
            String[] subnames = namespace.split(",");
            for (int j = 0; j < subnames.length; j++) {
                String subname = subnames[j];
                if (j > 0) {
                    sb.append(',');
                }
                sb.append(StringHelper.getJavaClassSimpleName(subname));
            }
        }
        return sb.toString();
    }

    public static String removeMany(String inString, String... keywords) {
        if (inString == null) {
            return null;
        }
        for (String k : keywords) {
            inString = replace(inString, k, "");
        }
        return inString;
    }

    public static void appendReplacement(Matcher m, StringBuffer sb, String replacement) {
        replacement = StringHelper.replace(replacement, "$", "\\$");
        m.appendReplacement(sb, replacement);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuilder sbuf = new StringBuilder();
        // output StringBuilder we'll build up
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));

        // remember to append any characters to the right of a match
        return sbuf.toString();
    }

    /**����ĸ��copy from spring*/
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**����ĸСдcopy from spring*/
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**copy from spring*/
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    private static final Random RANDOM = new Random();

    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it
                        // in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting
                        // it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     * @param name the string containing original name
     * @return the converted name
     */
    public static String toUnderscoreName(String name) {
        if (name == null)
            return null;

        String filteredName = name;
        if (filteredName.indexOf("_") >= 0 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }
        if (filteredName.indexOf("_") == -1 && filteredName.equals(filteredName.toUpperCase())) {
            filteredName = filteredName.toLowerCase();
        }

        StringBuilder result = new StringBuilder();
        if (filteredName != null && filteredName.length() > 0) {
            result.append(filteredName.substring(0, 1).toLowerCase());
            for (int i = 1; i < filteredName.length(); i++) {
                String preChart = filteredName.substring(i - 1, i);
                String c = filteredName.substring(i, i + 1);
                if (c.equals("_")) {
                    result.append("_");
                    continue;
                }
                if (preChart.equals("_")) {
                    result.append(c.toLowerCase());
                    continue;
                }
                if (c.matches("\\d")) {
                    result.append(c);
                } else if (c.equals(c.toUpperCase())) {
                    result.append("_");
                    result.append(c.toLowerCase());
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    public static String removeEndWiths(String inputString, String... endWiths) {
        for (String endWith : endWiths) {
            inputString = StringUtil.trimSubfix(inputString, endWith);
        }
        return inputString;
    }

    /**
     * 将string转换为List<ColumnEnum> 格式为: "enumAlias(enumKey,enumDesc)"
     */
    static Pattern three = Pattern.compile("(.*)\\((.*),(.*)\\)");
    static Pattern two = Pattern.compile("(.*)\\((.*)\\)");

    public static List<EnumMetaDada> string2EnumMetaData(String data) {
        if (data == null || data.trim().length() == 0)
            return new ArrayList<>();
        //enumAlias(enumKey,enumDesc),enumAlias(enumDesc)

        List<EnumMetaDada> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\w+\\(.*?\\)");
        Matcher m = p.matcher(data);
        while (m.find()) {
            String str = m.group();
            Matcher three_m = three.matcher(str);
            if (three_m.find()) {
                list.add(new EnumMetaDada(three_m.group(1), three_m.group(2), three_m.group(3)));
                continue;
            }
            Matcher two_m = two.matcher(str);
            if (two_m.find()) {
                list.add(new EnumMetaDada(two_m.group(1), two_m.group(1), two_m.group(2)));
                continue;
            }
            throw new IllegalArgumentException("error enumString format:" + data + " expected format:F(1,Female);M(0,Male) or F(Female);M(Male)");
        }
        return list;
    }

    /**
     * Test whether the given string matches the given substring
     * at the given index.
     * @param str the original string (or StringBuilder)
     * @param index the index in the original string to start matching against
     * @param substring the substring to match at the given index
     */
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    public static String[] tokenizeToStringArray(String str, String seperators) {
        if (str == null)
            return new String[0];
        StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
        List<String> result = new ArrayList<>();

        while (tokenlizer.hasMoreElements()) {
            String s = (String) tokenlizer.nextElement();
            result.add(s);
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static String join(List<String> list, String seperator) {
        return join(list.toArray(new Object[0]), seperator);
    }

    public static String replace(int start, int end, String str, String replacement) {
        String before = str.substring(0, start);
        String after = str.substring(end);
        return before + replacement + after;
    }

    public static String join(Object[] array, String seperator) {
        if (array == null)
            return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i != array.length - 1) {
                result.append(seperator);
            }
        }
        return result.toString();
    }

    public static String[] removeBrks(String[] values) {
        List<String> dests = new ArrayList<String>(values.length);
        for (String value : values) {
            dests.add(value.trim());
        }
        String[] result = new String[dests.size()];
        return dests.toArray(result);
    }

    public static int containsCount(String string, String keyword) {
        if (string == null)
            return 0;
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            int indexOf = string.indexOf(keyword, i);
            if (indexOf < 0) {
                break;
            }
            count++;
            i = indexOf;
        }
        return count;
    }

    public static String getByRegex(String str, String regex) {
        return getByRegex(str, regex, 0);
    }

    public static String getByRegex(String str, String regex, int group) {
        if (regex == null)
            throw new NullPointerException();
        if (group < 0)
            throw new IllegalArgumentException();
        if (str == null)
            return null;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(group);
        }
        return null;
    }

    public static String removeIbatisOrderBy(String sql) {
        //	    Pattern p = Pattern.compile("<is\\w+\\s+[\\w\\s='\"]+>\\s*order\\s+by.*?</\\w+>");
        //<is\w+\s+[\w\s='"]+>\s*order\s+by.*?</\w+>
        String orderByRemovedSql = sql.replaceAll("(?si)<\\w+[^>]*?>\\s*order\\s+by\\s+[^<]+?</\\w+>", "")
            .replaceAll("(?i)<\\w+[\\w\\s='\"]+prepend[\\w\\s='\"]*?order\\s+by\\s*['\"][^>]*?>[^<]+</\\w+>", "")
            //.replaceAll("(?i)\\s*order\\s+by\\s+.*", "")
            .replaceAll("(?s)\\s*order\\s+by\\s+.*", "");
        return removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(orderByRemovedSql))));
    }

    public static String removeXMLCdataTag(String str) {
        if (str == null)
            return null;
        str = StringHelper.replace(str, "<![CDATA[", "");
        str = StringHelper.replace(str, "]]>", "");
        return str;
    }

    /**
     * 为 查询sql中的关键字 select后面插入一段值 
     **/
    public static String insertTokenIntoSelectSql(String str, String insertValue) {
        String token = "select\\s";
        int selectBeginPos = indexOfByRegex(str, "(?si)" + token);
        if (selectBeginPos == -1) {
            return str;
        }
        return new StringBuilder(str).insert(selectBeginPos + "select ".length(), insertValue).toString();
    }

    public static String removeXmlTagIfBodyEmpty(String sql) {
        return sql.replaceAll("<\\w+[^>]*?>\\s+</\\w+>", "");
    }

    public static String columnNameToClassName(String dbName) {
        throw new UnsupportedOperationException();
        //	    return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(dbName));
    }

    public static String tableNameToClassName(String dbName) {
        return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(dbName));
    }

    /**
    * 将一个单词从单数转变为复数, 如 customer => customers
    */
    public static String pluralize(String word) {
        return Inflector.getInstance().pluralize(word);
        //        String result = name;
        //        if (name.length() == 1) {
        //            // just append 's'
        //            result += 's';
        //        } else {
        //            if (!seemsPluralised(name)) {
        //                String lower = name.toLowerCase();
        //                char secondLast = lower.charAt(name.length() - 2);
        //                if (!isVowel(secondLast) && lower.endsWith("y")) {
        //                    // city, body etc --> cities, bodies
        //                    result = name.substring(0, name.length() - 1) + "ies";
        //                } else if (lower.endsWith("ch") || lower.endsWith("s")) {
        //                    // switch --> switches or bus --> buses
        //                    result = name + "es";
        //                } else {
        //                    result = name + "s";
        //                }
        //            }
        //        }
        //        return result;
    }

    /**
     * 将一个单词从复数转变为单数, 如 customers => customer
     */
    public static String singularize(String word) {
        return Inflector.getInstance().singularize(word);
        //        String result = name;
        //        if (seemsPluralised(name)) {
        //            String lower = name.toLowerCase();
        //            if (lower.endsWith("ies")) {
        //                // cities --> city
        //                result = name.substring(0, name.length() - 3) + "y";
        //            } else if (lower.endsWith("ches") || lower.endsWith("ses")) {
        //                // switches --> switch or buses --> bus
        //                result = name.substring(0, name.length() - 2);
        //            } else if (lower.endsWith("s")) {
        //                // customers --> customer
        //                result = name.substring(0, name.length() - 1);
        //            }
        //        }
        //        return result;
    }

    //    /**
    //     * Gets the Vowel attribute of the Util object
    //     *
    //     * @todo-javadoc Write javadocs for method parameter
    //     * @param c Describe what the parameter does
    //     * @return The Vowel value
    //     */
    //    private static final boolean isVowel(char c) {
    //        boolean vowel = false;
    //        vowel |= c == 'a';
    //        vowel |= c == 'e';
    //        vowel |= c == 'i';
    //        vowel |= c == 'o';
    //        vowel |= c == 'u';
    //        vowel |= c == 'y';
    //        return vowel;
    //    }
    //
    //    /**
    //     * 是否像一个复数单词
    //     */
    //    private static boolean seemsPluralised(String name) {
    //        name = name.toLowerCase();
    //        boolean pluralised = false;
    //        pluralised |= name.endsWith("es");
    //        pluralised |= name.endsWith("s");
    //        pluralised &= !(name.endsWith("ss") || name.endsWith("us"));
    //        return pluralised;
    //    }

    /**
     * Normalize the path by suppressing sequences like "path/.." and
     * inner simple dots.
     * <p>The result is convenient for path comparison. For other uses,
     * notice that Windows separators ("\") are replaced by simple slashes.
     * @param path the original path
     * @return the normalized path
     */
    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(":");
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            pathToUse = pathToUse.substring(prefixIndex + 1);
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        List<String> pathElements = new LinkedList<String>();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            } else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     * @param coll the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     */
    public static String collectionToDelimitedString(Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    /**
     * Convert a CSV list into an array of Strings.
     * @param str the input String
     * @return an array of Strings, or the empty array in case of empty input
     */
    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will still
     * be considered as single delimiter string, rather than as bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     * @param str the input String
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     * @see #tokenizeToStringArray
     */
    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     * @param coll the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @param prefix the String to start each element with
     * @param suffix the String to end each element with
     * @return the delimited String
     */
    public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will still
     * be considered as single delimiter string, rather than as bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     * @param str the input String
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
     * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
     * @return an array of the tokens in the list
     * @see #tokenizeToStringArray
     */
    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[] { str };
        }
        List<String> result = new ArrayList<String>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    /**
     * Copy the given Collection into a String array.
     * The Collection must contain String elements only.
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the passed-in
     * Collection was <code>null</code>)
     */
    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    /**
     * Delete any character in a given String.
     * @param inString the original String
     * @param charsToDelete a set of characters to delete.
     * E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting String
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Check that the given CharSequence is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
     * <p><pre>
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     * @param str the CharSequence to check (may be <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null and has length
     * @see #hasLength(String)
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given String is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a String that purely consists of whitespace.
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not null and has length
     * @see #hasLength(CharSequence)
     */
    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     *
     * <pre>
     * StringUtils.padding(0, 'e')  = ""
     * StringUtils.padding(3, 'e')  = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of <code>char</code>s to be represented.
     * If you are needing to support full I18N of your applications
     * consider using {@link #repeat(String, int)} instead. 
     * </p>
     *
     * @param repeat  number of times to repeat delim
     * @param padChar  character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        final char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    /**
    * <p>Repeat a String <code>repeat</code> times to form a
    * new String.</p>
    *
    * <pre>
    * StringUtils.repeat(null, 2) = null
    * StringUtils.repeat("", 0)   = ""
    * StringUtils.repeat("", 2)   = ""
    * StringUtils.repeat("a", 3)  = "aaa"
    * StringUtils.repeat("ab", 2) = "abab"
    * StringUtils.repeat("a", -2) = ""
    * </pre>
    *
    * @param str  the String to repeat, may be null
    * @param repeat  number of times to repeat str, negative treated as zero
    * @return a new String consisting of the original String repeated,
    *  <code>null</code> if null String input
    * @since 2.5
    */
    private static final int PAD_LIMIT = 8192;

    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    public static String firstLowerCase(String dest) {
        if (dest.length() > 1) {
            return new StringBuilder().append(Character.toLowerCase(dest.charAt(0))).append(dest.substring(1)).toString();
        }
        return dest.toLowerCase();
    }
    
}
