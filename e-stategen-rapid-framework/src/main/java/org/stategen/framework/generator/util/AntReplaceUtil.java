package org.stategen.framework.generator.util;

import org.apache.tools.ant.util.regexp.Jdk14RegexpRegexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class AntReplaceUtil {
    public static String replaceAll(String input,String regex,String argument,String flags) {
        Jdk14RegexpRegexp jdk14RegexpRegexp = new Jdk14RegexpRegexp();
        int options =RegexpUtil.asOptions(flags);
        jdk14RegexpRegexp.setPattern(regex);
        String result = jdk14RegexpRegexp.substitute(input, argument, options);
        return result;
        
    }
}
