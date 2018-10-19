package org.stategen.framework.util;

import static org.stategen.framework.util.NumberUtil.isEqual;
import static org.stategen.framework.util.NumberUtil.isGreatOrEqualZero;
import static org.stategen.framework.util.NumberUtil.isGreatZero;
import static org.stategen.framework.util.NumberUtil.isNotZero;
import static org.stategen.framework.util.NumberUtil.isNullOrLessEqualZero;
import static org.stategen.framework.util.NumberUtil.isNullOrZero;

import org.junit.Test;

public class NumberUtilTst {
    
    @Test
    public void testNumber(){
        System.out.println(" isNotZero(1)<===========>:" + isNotZero(1));
        System.out.println("isNotZero(0)<===========>:" + isNotZero(0));
        System.out.println("isNotZero(null)<===========>:" + isNotZero(null));
        /***为空或小于0*/
        System.out.println("isNullOrZero(null)<===========>:" + isNullOrZero(null));
        System.out.println("isNullOrZero(0)<===========>:" + isNullOrZero(0));
        System.out.println("isNullOrZero(1)<===========>:" + isNullOrZero(1));
        
        /***不为空且大于0*/
        System.out.println("isGreatZero(-1)<===========>:" + isGreatZero(-1));
        System.out.println(" isGreatZero(1)<===========>:" + isGreatZero(1));
        System.out.println("isGreatZero(0)<===========>:" + isGreatZero(0));
        
        /***不为空且大于等于0*/
        System.out.println("isGreatOrEqualZero(null)<===========>:" + isGreatOrEqualZero(null));
        System.out.println("isGreatOrEqualZero(0)<===========>:" + isGreatOrEqualZero(0));
        System.out.println(" isGreatOrEqualZero(-1)<===========>:" + isGreatOrEqualZero(-1));
        System.out.println("isGreatOrEqualZero(1)<===========>:" + isGreatOrEqualZero(1));
        
        /***为空或小于等于0*/
        System.out.println("isNullOrLessEqualZero(null)<===========>:" + isNullOrLessEqualZero(null));
        System.out.println("isNullOrLessEqualZero(0)<===========>:" + isNullOrLessEqualZero(0));
        System.out.println("isNullOrLessEqualZero(1)<===========>:" + isNullOrLessEqualZero(1));
        System.out.println("isNullOrLessEqualZero(-1)<===========>:" + isNullOrLessEqualZero(-1));
        
        /***两于不为空且相等*/
        System.out.println("isEqual(1,1L)<===========>:" + isEqual(1,1L));
        System.out.println("isEqual(1,0)<===========>:" + isEqual(1,0));
        System.out.println("isEqual(1,null)<===========>:" + isEqual(1,null));
        System.out.println("isEqual(0.5,5f)<===========>:" + isEqual(0.5,0.5f));
    }

}
