package org.stategen.framework.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ReflectTst<E> {

//    List<User> getItems() {
//        return null;
//    }

    E getUser() {
        return null;
    }

    public static void main(String[] args) {
        Class<?> clz = ReflectTst.class;
        Method[] declaredMethods = clz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                System.out.println("name<===========>:" + methodName);
                Type genericReturnType2 = method.getGenericReturnType();
                if (genericReturnType2 instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericReturnType2;
                    Type type = parameterizedType.getActualTypeArguments()[0];
                    if (type instanceof TypeVariable) {
                        TypeVariable<?> typeVariable = (TypeVariable<?>) type;
                        String name = typeVariable.getName();
                        System.out.println("name<===========>:" + name);
                    }
                }
            }
        }
    }

}
