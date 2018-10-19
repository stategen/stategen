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
 * 通用string 比较器
 * @version $Id: StringComparetor.java, v 0.1 2015-12-28 20:15:28 xiazhengsheng Exp $
 */
public class StringComparetor  implements Comparator<String>{

    /** 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(String str1, String str2) {
        if (StringUtil.isEmpty(str1)) {
            if (StringUtil.isEmpty(str2)){
                return 0;
            } else {
                return -1;
            }
        } else {
            if (StringUtil.isEmpty(str2)){
                return -1;
            } else {
                return str1.compareTo(str2);
            }
        }
    }
}
