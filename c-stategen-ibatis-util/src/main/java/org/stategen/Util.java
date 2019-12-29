package org.stategen;

import java.lang.reflect.Array;
import java.util.Collection;

/***
 * 该类为了mybatis 对空的判断
 * 
 * @author XiaZhengsheng
 * @version $Id: Util.java, v 0.1 2019年12月28日 下午10:54:53 XiaZhengsheng Exp $
 */
public class Util {
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof Collection) {
            return ((Collection) value).isEmpty();
        } else if (value instanceof String) {
            return ((String) value).isEmpty();
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        } else {
            return value.toString().isEmpty();
        }
    }

    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

}
