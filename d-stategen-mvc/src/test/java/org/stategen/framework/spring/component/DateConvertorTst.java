package org.stategen.framework.spring.component;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.stategen.framework.spring.mvc.DateConvertor;

public class DateConvertorTst {
    
    
    @Test
    public void testTime(){
        DateConvertor dateConvertor = new DateConvertor();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS"); 
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26")));
        System.out.println(sdf.format(dateConvertor.convert("2018/01/26")));
        System.out.println(sdf.format(dateConvertor.convert("20180126")));
        System.out.println(sdf.format(dateConvertor.convert("2018.01.26")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03:20:16")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03-20-16")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03/20/16")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03:20:16.555")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03:20:16555")));
        System.out.println(sdf.format(dateConvertor.convert("2018-01-26 03:20:16 555")));
        System.out.println(sdf.format(dateConvertor.convert("1473048265")));
        System.out.println(sdf.format(dateConvertor.convert("1473048265123")));
    }

}
