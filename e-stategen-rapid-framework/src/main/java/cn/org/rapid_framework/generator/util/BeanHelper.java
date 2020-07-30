package cn.org.rapid_framework.generator.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class BeanHelper {
	/**
	 * @see #org.apache.commons.beanutils.PropertyUtils.describe(obj)
	 */ 
	@SuppressWarnings("unchecked")
    public static Map<String, Object> describe(Object obj,String... ignoreProperties) {
		if (obj instanceof Map) {
			return (Map<String, Object>) obj;
		}
		Map<String, Object> map = new HashMap<>();
		PropertyDescriptor[] descriptors = getPropertyDescriptors(obj.getClass());
		for(int i = 0; i < descriptors.length; i++ ) {
			String name = descriptors[i].getName();
			if(contains(ignoreProperties,name)) {
				continue;
			}
			
            Method readMethod = descriptors[i].getReadMethod();
			if (readMethod != null) {
				try {
					map.put(name, readMethod.invoke(obj, new Object[]{}));
				}catch(Exception e){
					GLogger.warn("error get property value,name:"+name+" on bean:"+obj,e);
				}
            }
		}
		return map;
	}

   private static boolean contains(String[] array, String str) {
	   if(array == null) {
		   return false;
	   }
	   if(str == null) {
		   return false;
	   }
	   for(String ignore : array) {
		   if(str.equals(ignore)) {
			   return true;
		   }
	   }
	   return false;
   }

   public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass,String propertyName,boolean ignoreCase) {
        for(PropertyDescriptor pd : getPropertyDescriptors(beanClass)) {
        	if(ignoreCase) {
        		if(pd.getName().equalsIgnoreCase(propertyName)) {
        			return pd;
        		}
        	}else {
	            if(pd.getName().equals(propertyName)) {
	                return pd;
	            }
        	}
        }
        return null;
   }
	   
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			return (new PropertyDescriptor[0]);
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors == null) {
			descriptors = new PropertyDescriptor[0];
		}
		return descriptors;
	}
	
    public static void copyProperties(Object target, Object source)  {
        copyProperties(target,source,false);
    }

    public static void copyProperties(Object target, Object source,boolean ignoreCase)  {
        copyProperties(target,source,ignoreCase,null);
    }
    
    public static void copyProperties(Object target, Map<String, Object> source)  {
        copyProperties(target,source,false);
    }
    
    public static void copyProperties(Object target, Map<String, Object> source,boolean ignoreCase)  {
        for (Entry<String, Object> entry :source.entrySet()){
            String key =entry.getKey();
            if ("xml:base".equalsIgnoreCase(key)){
                continue;
            }
        	PropertyDescriptor pd = getPropertyDescriptor(target.getClass(), key, ignoreCase);
        	if(pd == null) {
        		throw new IllegalArgumentException("not found property:'"+key+"' on class:"+target.getClass());
        	}
        	setProperty(target, pd, entry.getValue());
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void copyProperties(Object target, Object source,boolean ignoreCase,String[] ignoreProperties)  {
        if(target instanceof Map) {
            throw new UnsupportedOperationException("target is Map<String, Object> unsuported");
        }
        
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (int i = 0; i < targetPds.length; i++) {
            PropertyDescriptor targetPd = targetPds[i];
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                try {
                    if(source instanceof Map) {
                        Map<String, Object> map = (Map)source;
                        if(MapHelper.containsKey(map, targetPd.getName(), ignoreCase)) {
                        	Object value = MapHelper.getValue(map, targetPd.getName(), ignoreCase);
                        	setProperty(target, targetPd, value);
                        }
                    }else {
                        PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName(),ignoreCase);
                        if (sourcePd != null && sourcePd.getReadMethod() != null) {
                            Object value = getProperty(source, sourcePd);
                            setProperty(target, targetPd, value);
                        }
                    }
                } catch (Throwable ex) {
                    throw new IllegalArgumentException("Could not copy properties on:"+targetPd.getDisplayName(),ex);
                }
            }
        }
    }
    
    static class MapHelper {
	    public static Object getValue(Map<String, Object> map,String property, boolean ignoreCase) {
	    	if(ignoreCase) {
	    	    property=property.toLowerCase();
				for(String key : map.keySet()) {
				        String lowKey =key.toLowerCase();
						if(property.equals(lowKey)) {
							return map.get(key);
						}
				}
				return null;
	    	}else {
	    		return map.get(property);
	    	}
		}
	    
	    public static boolean containsKey(Map<String, Object> map,String property, boolean ignoreCase) {
	    	if(ignoreCase) {
                property=property.toLowerCase();
                for(String key : map.keySet()) {
                        String lowKey =key.toLowerCase();
                        if(property.equals(lowKey)) {
                            return true;
                        }
                }
				return false;
	    	}else {
	    		return map.containsKey(property);
	    	}
		}
    }

	private static Object getProperty(Object source, PropertyDescriptor sourcePd)throws IllegalAccessException,InvocationTargetException {
        Method readMethod = sourcePd.getReadMethod();
        if (!Modifier.isPublic(readMethod.getDeclaringClass()
            .getModifiers())) {
            readMethod.setAccessible(true);
        }
        Object value = readMethod.invoke(source, new Object[0]);
        return value;
    }

    public static void setProperty(Object target, String propertyName, Object value)  {
        PropertyDescriptor pd = getPropertyDescriptor(target.getClass(),propertyName,false);
        if(pd == null) throw new IllegalArgumentException("not found property:"+propertyName+" on class:"+target.getClass());
        setProperty(target, pd, value);
    }

    public static void setProperty(Object target, String propertyName, Object value,boolean ignoreCase)  {
        PropertyDescriptor pd = getPropertyDescriptor(target.getClass(),propertyName,ignoreCase);
        if(pd == null) throw new IllegalArgumentException("not found property:"+propertyName+" on class:"+target.getClass());
        setProperty(target, pd, value);
    }
    
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void setProperty(Object target, PropertyDescriptor propertyDescriptor, Object value)  {
    	if(propertyDescriptor == null) {
        	throw new IllegalArgumentException("propertyDescriptor must be not null");
        }
    	if(target == null) {
        	throw new IllegalArgumentException("target must be not null");
        }
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if(writeMethod == null) {
        	throw new IllegalArgumentException("not found write method for property:"+propertyDescriptor.getName()+" on class:"+target.getClass());
        }
        if (!Modifier.isPublic(writeMethod.getDeclaringClass()
            .getModifiers())) {
            writeMethod.setAccessible(true);
        }
        try {
            writeMethod.invoke(target, new Object[] { convert(value,writeMethod.getParameterTypes()[0]) });
        }catch(Exception e) {
            throw new RuntimeException("error set property:"+propertyDescriptor.getName()+" on class:"+target.getClass(),e);
        }
    }

    private static Object convert(Object value, Class<?> targetType) {
        if(value == null) return null;
        if(targetType == String.class) {
            return value.toString();
        }else {
            return convert(value.toString(),targetType);
        }
    }

    private static Object convert(String value,Class<?> targetType) {
        if(targetType == Byte.class || targetType == byte.class) {
            return new Byte(value);
        }
        if(targetType == Short.class || targetType == short.class) {
            return new Short(value);
        }
        if(targetType == Integer.class || targetType == int.class) {
            return new Integer(value);
        }
        if(targetType == Long.class || targetType == long.class) {
            return new Long(value);
        }
        if(targetType == Float.class || targetType == float.class) {
            return new Float(value);
        }
        if(targetType == Double.class || targetType == double.class) {
            return new Double(value);
        }
        if(targetType == BigDecimal.class) {
            return new BigDecimal(value);
        }
        if(targetType == BigInteger.class) {
            return BigInteger.valueOf(Long.parseLong(value));
        }
        if(targetType == Boolean.class || targetType == boolean.class) {
            return new Boolean(value);
        }
        if(targetType == boolean.class) {
            return new Boolean(value);
        }
        if(targetType == char.class) {
            return value.charAt(0);
        }
        if(DateHelper.isDateType(targetType)) {
            return DateHelper.parseDate(value,targetType,"yyyyMMdd","yyyy-MM-dd","yyyyMMddHHmmSS","yyyy-MM-dd HH:mm:ss","HH:mm:ss");
        }

        throw new IllegalArgumentException("cannot convert value:"+value +" to targetType:"+targetType);
    }
}
