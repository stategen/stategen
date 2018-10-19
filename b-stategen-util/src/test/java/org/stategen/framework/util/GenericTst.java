package org.stategen.framework.util;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.stategen.framework.lite.PageList;


/**
 * 通过反射获取泛型信息
 * @author Administrator
 *
 */
public class GenericTst {
    class User {
        String name;
        Integer age;
        String nickName;
    }
    /**
     * 方法一
     * @param map
     * @param list
     */
//    public static void test01(Map<String,User>map,List<User>list){
//        System.out.println("Generic.test01()");
//    }
    public static void test01(Map<String,User>map){
        System.out.println("Generic.test01()");
    }
    public static void test01(PageList<User> pageList){
        System.out.println("Generic.test01()");
    }
    /**
     * 方法二
     * @return
     */
    public Map<Integer,User>test02(){
        System.out.println("Generic.test02()");
        return null;
    }
    /**
     * 通过反射机制获取泛型
     * #java.util.Map<java.lang.String, com.dasenlin.reflectionconstractor.User>
     * 泛型类型class java.lang.String
     * 泛型类型class com.dasenlin.reflectionconstractor.User
     * #java.util.List<com.dasenlin.reflectionconstractor.User>
     * 泛型类型class com.dasenlin.reflectionconstractor.User
     * 返回值，泛型类型class java.lang.Integer
     * 返回值，泛型类型class com.dasenlin.reflectionconstractor.User 
     * @param args
     */
    public static void main(String[] args) {
        try {

            Method m = GenericTst.class.getMethod("test01", Map.class);
            Parameter[] parameters = m.getParameters();
            for (Parameter parameter : parameters) {
                Type parameterizedType = parameter.getParameterizedType();
                System.out.println("parameterizedType<===========>:" + parameterizedType);
                Type[]genericTypes = ((ParameterizedType)parameterizedType).getActualTypeArguments();
                for(Type genericType:genericTypes){
                    System.out.println("泛型类型"+genericType);
                }
            }
            
//            Type [] types = m.getGenericParameterTypes();//获取参数泛型
//            for(Type paramType:types){
//                System.out.println("#"+paramType);
//                if(paramType instanceof ParameterizedType){
//                    Type[]genericTypes = ((ParameterizedType)paramType).getActualTypeArguments();
//                    for(Type genericType:genericTypes){
//                        System.out.println("泛型类型"+genericType);
//                    }
//                }
//            }

//            Method m2 =Generic.class.getMethod("test02", null);
//            Type returnType = m2.getGenericReturnType();//获取返回类型的泛型
//            if(returnType instanceof ParameterizedType){
//                Type [] genericTypes2 =((ParameterizedType)returnType).getActualTypeArguments();
//                for(Type genericType2:genericTypes2){
//                    System.out.println("返回值，泛型类型"+genericType2);
//                }
//            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}