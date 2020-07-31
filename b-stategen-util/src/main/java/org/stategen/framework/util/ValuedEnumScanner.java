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

import java.util.List;

import org.stategen.framework.lite.ClassUtil;
import org.stategen.framework.lite.ValuedEnum;


/**
 * The Class ValuedEnumScanner.
 * 该类用来配合ibatis将数字int 或其它类型转换为java 枚举，该类可用spring 注册，也可以实例化后调用 setPackages
 * @author Xia Zhengsheng
 */
public class ValuedEnumScanner {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public  void setPackages(List<String> packages){
        if (CollectionUtil.isNotEmpty(packages)){
            for (String packageName : packages) {
                List<Class<?>> classes = ClassUtil.getClasses(packageName);
                for (Class<?> clz : classes) {
                    if (ValuedEnum.class.isAssignableFrom(clz)){
                        Class<? extends ValuedEnum> veClass =(Class<? extends ValuedEnum>) clz;
                        EnumUtil.registValuedEnum(veClass);
                    }
                }
            }
        }
    }


}
