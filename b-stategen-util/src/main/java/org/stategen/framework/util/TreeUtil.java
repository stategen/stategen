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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The Class TreeUtil.
 */
public class TreeUtil {

    public <T, K> List<T> makeTree(List<T> source, Function<? super T, K> idGetMethod, Function<? super T, K> pidGetMethod,
                                   BiConsumer<T, T> setParentMethod) {
        if (CollectionUtil.isNotEmpty(source) && idGetMethod != null && pidGetMethod != null) {
            Map<K, T> idMap = CollectionUtil.toMap(idGetMethod, source);
            List<T> result = new ArrayList<T>();
            for (T t : source) {
                K pid = pidGetMethod.apply(t);
                T parent = null;
                if (pid != null) {
                    parent = idMap.get(pid);
                    if (parent != null) {
                        setParentMethod.accept(t, parent);
                        continue;
                    }
                }
                result.add(t);
            }
            return result;
        }
        return source;
    }

}
