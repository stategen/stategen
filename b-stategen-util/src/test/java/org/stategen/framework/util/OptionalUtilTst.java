package org.stategen.framework.util;

import org.junit.Test;
import org.stategen.framework.util.OptionalUtil;
import org.stategen.framework.util.CollectionUtilTst.User;

public class OptionalUtilTst {
    
    
    @Test
    public void testOpt(){
        User user0=null;
        User user1 = new User(1L, "张三", "班级1");
        
        String userName = OptionalUtil.getOrNull(user0, User::getUserName);
        System.out.println("userName<===========>:" + userName);
        
        userName = OptionalUtil.getOrNull(user1, User::getUserName);
        System.out.println("userName<===========>:" + userName);
    }

}
