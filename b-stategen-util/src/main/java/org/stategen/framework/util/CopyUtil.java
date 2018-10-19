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
package org.stategen.framework.util;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The Class CopyUtil.
 */
public class CopyUtil {

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CopyUtil.class);
    
    public static <T> T copy(Object source,Class<T> targetCls, String... ignoreProperties) {
        if(source == null){
            return null;
        }
        T targetObj = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source,targetObj,ignoreProperties);
        return targetObj;
    }

    public static <T> List<T> copy(List<?> sourceList, Class<T> targetCls, String... ignoreProperties) {
        if(sourceList == null){
            return null;
        }
        
        if(CollectionUtil.isNotEmpty(sourceList)){
            List<T> targetList = new ArrayList<T>(sourceList.size());
            for(Object o:sourceList){
                T targetObj = BeanUtils.instantiate(targetCls);
                BeanUtils.copyProperties(o,targetObj,ignoreProperties);
                targetList.add(targetObj);
            }
            return targetList;
        }
        return new ArrayList<T>(0);
    }

    public static <T> T deepCopy(T dest) {
        T result =null;
        result = KryoDeepCopy(dest);
        return result;
    }
    

    @SuppressWarnings("unchecked")
    public static <T> T KryoDeepCopy(T dest) {
        if (dest == null) {
            return null;
        }
        
        Kryo kryo =new Kryo();

        T result = null;
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        Output objectOutput = new Output(fileOut);
        kryo.writeObject(objectOutput, dest);
        objectOutput.close();
        
        byte[] buf = fileOut.toByteArray();
        Input input = new Input(buf);
        result = (T) kryo.readObject(input, dest.getClass());
        input.close();
        return result;
    }    

    @SuppressWarnings("unchecked")
    public static <T> T jdkDeepCopy(T dest) {
        ByteArrayOutputStream baos = null;  
        ObjectOutputStream oos = null;  
          
        ByteArrayInputStream bais = null;  
        ObjectInputStream ois = null;  
          
        T result = null;  
        
        
        //如果子类没有继承Serializable接口，这一步会报错  
        try {  
            baos = new ByteArrayOutputStream();  
            oos = new ObjectOutputStream(baos);  
            oos.writeObject(dest);  
            byte[] buf = baos.toByteArray();
            oos.close();
            
            bais = new ByteArrayInputStream(buf);  
            ois = new ObjectInputStream(bais);  
            result = (T) ois.readObject();  
            ois.close();
        } catch (Exception e) {  
           logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:")
               .append(e.getMessage()).append(" \n").toString(),
            e);
        } 
        
        return result;
    }
    
    public static <T> T merge(T target,Object source, String... ignoreProperties){
        mergeProperties(target,source, null, ignoreProperties);
        return target;
    }
    
    public static <K,T> void merge(List<T> targets,Function<? super T, K> tagetKeyMapper,Map<K,?> sourceMap,  String... ignoreProperties){
        Assert.notNull(sourceMap, "Source must not be null");
        Assert.notNull(targets, "Target must not be null");
        targets.stream().forEach((target)->{
            if (target!=null){
                K key = tagetKeyMapper.apply(target);
                if (key!=null){
                    Object source = sourceMap.get(key);
                    if (source!=null){
                        mergeProperties(target,source, null, ignoreProperties);
                    }
                }
            }
        });
        
    }
    
    
    
    private static void mergeProperties(Object target,Object source, Class<?> editable, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method targetWriteMethod = targetPd.getWriteMethod();
            if (targetWriteMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method sourceReadMethod = sourcePd.getReadMethod();
                    if (sourceReadMethod != null &&
                            ClassUtils.isAssignable(targetWriteMethod.getParameterTypes()[0], sourceReadMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(sourceReadMethod.getDeclaringClass().getModifiers())) {
                                sourceReadMethod.setAccessible(true);
                            }
                            Object value = sourceReadMethod.invoke(source);
                            if (!Modifier.isPublic(targetWriteMethod.getDeclaringClass().getModifiers())) {
                                targetWriteMethod.setAccessible(true);
                            }
                            //只有当值不为空时，才copy
                            if (value!=null){
                                targetWriteMethod.invoke(target, value);
                            }
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}
