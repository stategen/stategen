/**
 * Copyright (C) 2021  niaoge<78493244@qq.com>
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

import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;

/**
 * @author niaoge
 * @version $Id: FoldNumberType.java, v 0.1 2021年1月11日 下午11:22:39 XiaZhengsheng Exp $
 */
public enum FoldNumberType {
    /***1-trade-pojo*/
    numHeader,
    
    /***trade-1-pojo*/
    numMid(),
    
    /***trade-pojo*/
    numNone();

    private static FoldNumberType globFoldNumberType = numHeader;

    public static FoldNumberType getGlobFoldNumberType() {
        AssertUtil.mustNotNull(globFoldNumberType);
        return globFoldNumberType;
    }

    public static void convertToGlobFoldNumberType(String foldNumberType) {
        if (StringUtil.isNotEmpty(foldNumberType)) {
            foldNumberType =foldNumberType.toLowerCase();
            for (FoldNumberType type :values()) {
                if (type.name().toLowerCase().equals(foldNumberType)) {
                    FoldNumberType.globFoldNumberType =type;
                    return;
                }
            }
            
        }
    }
}
