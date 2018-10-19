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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/** 
 * RedisTemplate操作工具类 
 *  
 * @author lh 
 * @version 3.0 
 * @since 2016-8-29 
 * 
 */  
public final class RedisTemplateUtil {  
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RedisTemplateUtil.class);
  
    private static RedisTemplate<String, Object> redisTemplate=null ;
    
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisTemplateUtil.redisTemplate = redisTemplate;
    }    
  
    /** 
     * 写入缓存 
     * @param key 
     * @param value 
     * @param expire 
     */  
    public static Boolean put(final String key, final Object value, long expireSeconds) {  
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.error("向redis内放置object出错，请检查是否实现了接口Serializable?", e);
            return false;
        }  
    }  
    
  
    /** 
     * 读取缓存 ,如果类型不正确，则返回null
     * @param key 
     * @param clazz 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T get(final String key) {  
        T value=null;
        try {
            //反序列化出错
            value = (T) redisTemplate.boundValueOps(key).get();
        } catch (Exception e) {
            logger.error(
                new StringBuffer("redis读取 key\"").append(key).append("\"出错，错误信息：").append(e.getMessage()).append(" \n").toString(), e);
        }
        return  value;
    }  
    
    @SuppressWarnings("unchecked")  
    public static <T> T getAndCheck(final String key,T...valueCheck) {  
        T value =get(key);
        if (value!=null && CollectionUtil.isNotEmpty(valueCheck)){
            try {
                //利用java编译器将可变参数自动转换为array[T]进行校验
                valueCheck[0]=value;
            } catch (Exception e) {
                logger.error("类型转换错误", e);
                return null;
            }
        }
        return value;
    }      
    
    public static <T> T getAndCheck(final String key,Class<T> typeCheck) {  
        
        T value =get(key);
        if (value!=null){
            Assert.isTrue(typeCheck.isInstance(value),"类型转换错误");
        }
        return value;
    }    
    
    public static  long getExpireSeconds(final String key) {  
        return redisTemplate.boundValueOps(key).getExpire();
    }    
    
    @SuppressWarnings("unchecked")
    public static <V>BoundValueOperations<String,V> boundValueOps(final String key) {  
        return (BoundValueOperations<String, V>) redisTemplate.boundValueOps(key);
    }    
    
    public static <T> T pop(final String key) {  
      T value = get(key);
      redisTemplate.delete(key);  
      return value;
    }      
  
    /** 
     * 删除，根据key精确匹配 
     * @param key 
     */  
    public static void del(final String... key) {  
        redisTemplate.delete(Arrays.asList(key));  
    }  
  
    /** 
     * 批量删除，根据key模糊匹配 
     * @param pattern 
     */  
    public static void delPattern(final String... pattern) {  
        for (String kp : pattern) {  
            redisTemplate.delete(redisTemplate.keys(kp + "*"));  
        }  
    }  
  
    /** 
     * key是否存在 
     * @param key 
     */  
    public static boolean exists(final String key) {  
        return redisTemplate.hasKey(key);  
    }  
    
    public static String getJedisKey(String key, String arg){
        return getJedisKey(key, arg, null);
    }
    
    public static String getJedisKey(String key, String arg, String appId){
//      String concat = key.concat("." + arg);
//      if(appId != null && !"".equals(appId)){
//          concat = concat.concat("." + appId);
//      }
//      return concat;
        StringBuffer sb =new StringBuffer();
        sb.append(key).append('.').append(arg);
        if (!StringUtils.isEmpty(appId)){
            sb.append('.').append(appId);
        }
        return sb.toString();
    }

  
}  