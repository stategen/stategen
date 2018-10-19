package org.stategen.framework.util;

import org.junit.Test;
import org.stategen.framework.util.StringUtil;

public class StringUtilTst {
    
    @Test
    public void testMobile(){
        String mobile  ="13370073033";
        String newMobile=StringUtil.desensitizeMobile(mobile);
        System.out.println(mobile+"mobile<===========>:" + newMobile);
        
        String mobile7  ="1234567";
        String newMobile7=StringUtil.desensitizeMobile(mobile7);
        System.out.println(mobile7+"mobile<===========>:" + newMobile7);
        
        String mobile4  ="1234";
        String newMobile4=StringUtil.desensitizeMobile(mobile4);
        System.out.println(mobile4+"mobile<===========>:" + newMobile4);
        
        String text ="这是一个浪漫的故事";
        text= StringUtil.abbr(text, '.',text.length()-4,3, text.length());
        System.out.println(text+"mobile<===========>:" + text);
    }

}
