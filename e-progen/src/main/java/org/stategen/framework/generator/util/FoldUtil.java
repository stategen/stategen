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

package org.stategen.framework.generator.util;

import org.stategen.framework.util.StringUtil;

/**
 * The Class FoldUtil.
 *
 * @author niaoge
 * @version $Id: FoldUtil.java, v 0.1 2021年1月12日 上午1:16:22 XiaZhengsheng Exp $$
 */
public class FoldUtil {
    
    public static String get(Integer num, String systemName) {
        systemName = StringUtil.uncapfirst(systemName);
        FoldNumberType globFoldNumberType = FoldNumberType.getGlobFoldNumberType();
        
        if (globFoldNumberType == FoldNumberType.numHeader) {
            return String.format("%d-%s", num, systemName);
        }
        
        if (globFoldNumberType == FoldNumberType.numMid) {
            return String.format("%s-%d", systemName, num);
        }
        
        return systemName;
        
    }
}
