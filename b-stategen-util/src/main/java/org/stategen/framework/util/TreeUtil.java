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

    public static <T, K> List<T> makeTree(List<T> source, Function<? super T, K> idGetMethod, Function<? super T, K> pidGetMethod,
                                   BiConsumer<T, T> addChildMethod) {
        if (CollectionUtil.isNotEmpty(source)) {
            Map<K, T> idMap = CollectionUtil.toMap(source,idGetMethod);
            List<T> tree = makeTree(idMap, idGetMethod, pidGetMethod, addChildMethod);
            if (tree==null){
                return source;
            }
            return tree;
        }
        return source;
    }
    
    public static <T, K> List<T> makeTree(Map<K,T> source, Function<? super T, K> idGetMethod, Function<? super T, K> pidGetMethod,
                                          BiConsumer<T, T> addChildMethod) {
        if (CollectionUtil.isNotEmpty(source) && idGetMethod != null && pidGetMethod != null && addChildMethod!=null) {
            List<T> result = new ArrayList<T>();
            for (T t : source.values()) {
                K pid = pidGetMethod.apply(t);
                T parent = null;
                if (pid != null) {
                    parent = source.get(pid);
                    if (parent != null) {
                        addChildMethod.accept(parent,t);
                        continue;
                    }
                }
                result.add(t);
            }
            return result;
        }
        return null;
    }
    
    
    public static <T> T getRoot(T dest, Function<? super T, T> parentGetMethod){
        if (dest==null){
            return null;
        }
        
        T parent =dest;
        while (true) {
            T parentOfParent =parentGetMethod.apply(parent);  
            if (parentOfParent!=null){
                parent=parentOfParent; 
            } else {
                break;
            }
            
        }
        return parent;
    }
    
    public static <K,T>List<T> getWithParent(Map<K, T> sources,K id,Function<? super T, K> getParentIdMethod){
        if (sources==null){
            return null;
        }
        if (id==null){
            return null;
        }
        List<T> result =new ArrayList<T>();
        T t = sources.get(id);
        while (t!=null){
            result.add(0,t);
            id = getParentIdMethod.apply(t);
            t =sources.get(id);
        }
        return result;
    }
    
    public static <K,T> List<T> getWithParent(List<T> sources,K id,Function<? super T, K> getIdMethod,Function<? super T, K> getParentIdMethod){
        if (sources==null){
            return null;
        }
        Map<K, T> map = CollectionUtil.toMap(sources, getIdMethod);
        return getWithParent(map, id, getParentIdMethod);
    }
    
    

}
