package org.stategen.framework.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestGeneric<X>{
    X user;
    
    private static final long serialVersionUID = -1375958143091889386L;
 
    public static void main(String args[]) throws Exception{
        //获取Test类的父类ArrayList<ItemVo>的Type
        Type t = TestGeneric.class.getGenericSuperclass();
        
        ParameterizedType pt = (ParameterizedType) t;
        Type[] ts = pt.getActualTypeArguments();//这样就获取了ArrayList<ItemVo>中的泛型
        for(int i = 0; i < ts.length; ++ i){
            System.out.println(i + " 父类中的泛型为：" + ts[i]);
//            Class<?> c = (Class<?>) ts[i];//如果需要使用这个类型 进行强转即可
//            System.out.println(i + " 强转后类型为：" + c);
        }
    }
}
