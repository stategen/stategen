/**
 * Copyright (C) 2021  StateGen.org niaoge<78493244@qq.com>
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

package util;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * *
 * 该类为了mybatis 对空的判断.
 *
 * @author XiaZhengsheng
 * @version $Id: Util.java, v 0.1 2019年12月28日 下午10:54:53 XiaZhengsheng Exp $
 */
public class Check {
    
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        
        if (value instanceof String) {
            return ((String) value).isEmpty();
        }
        
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        
        return value.toString().isEmpty();
    }
    
    public static boolean isNotEmpty(Object value) {
        return isEmpty(value) == false;
    }
    
}
