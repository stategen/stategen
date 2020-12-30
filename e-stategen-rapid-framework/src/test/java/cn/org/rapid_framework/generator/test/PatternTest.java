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
package cn.org.rapid_framework.generator.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.StringHelper;

public class PatternTest {

    //@Test
    public void testSqlOperator() {
        Pattern p = Pattern.compile("(\\w+)\\s*[=<>!]{0,}\\s*\\?", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        String sql = "  begin_expire_time=UNIX_TIMESTAMP(?)*1000";
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String segment = m.group(0);
            String columnSqlName = m.group(1);

            String paramName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(columnSqlName));

            String replacedSegment = segment.replace("?", "#" + paramName + "#");
            m.appendReplacement(sb, replacedSegment);
        }
        m.appendTail(sb);
        GLogger.info(sb.toString());
    }


//    @Test
    public void testJavaClazz() {
        StringBuilder sb = new StringBuilder();
//        String clzz = "java.util.Map<java.lang.String, java.util.List<java.lang.String>>";
        String clzz = "java.util.List<String>";
        String[] namespaces = clzz.split("<");
        for (int i = 0; i < namespaces.length; i++) {
            String namespace =namespaces[i];
            if (i>0){
                sb.append('<');
            }
            String[] subnames = namespace.split(",");
            for (int j = 0; j < subnames.length; j++) {
                String subname =subnames[j];
                if (j>0){
                    sb.append(',');
                }
                sb.append(StringHelper.getJavaClassSimpleName(subname));
            }
        }
        GLogger.info(sb.toString());
    }
//    @Test
    public void testMax(){
        String maxAnno="abc//    @Id\r\n" + 
                " cde //    @Max(64)\r\n" + 
                "efg!@Max\r\n" + 
                "abc !@Id\r\n" +
                "";
                
        Pattern compile = Pattern.compile("//\\s*@Max",   Pattern.DOTALL);
        Matcher matcher = compile.matcher(maxAnno);
        GLogger.info("matcher.end()<===========>:" + matcher.find());
        
        compile = Pattern.compile("//\\s*@Id",   Pattern.DOTALL);
        matcher = compile.matcher(maxAnno);
        GLogger.info("matcher.end()<===========>:" + matcher.find());
        
        compile = Pattern.compile("!@Id",   Pattern.DOTALL);
        matcher = compile.matcher(maxAnno);
        GLogger.info("matcher.end()<===========>:" + matcher.find());
        
        compile = Pattern.compile("!@Max",   Pattern.DOTALL);
        matcher = compile.matcher(maxAnno);
        GLogger.info("matcher.end()<===========>:" + matcher.find());
        
    }
    
    @Test
    public void testStr() {
        String str ="abcdesfg";
        StringBuffer sb1=new StringBuffer().append(str.substring(1));
        StringBuffer sb2=new StringBuffer().append(str,1,str.length());
        GLogger.info(sb1.toString()+"<===========>:" + sb2.toString());
    }
}
