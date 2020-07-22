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

import java.util.Comparator;

/**
 * The Class StringComparetor.
 * 通用比较器
 */
public class CommonComparetor  implements Comparator<Object>{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public int compare(Object o1, Object o2) {
        if ((o1 instanceof Comparable) && (o2 instanceof Comparable )){
            Comparable c1 =(Comparable)o1;
            Comparable c2 =(Comparable)o2;
            return c1.compareTo(c2);
        } else {
             return o1.toString().compareTo(o2.toString());
        }
    }
}
