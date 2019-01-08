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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;

/**
 * The Class Collections.
 * <pre>尽量不用commons下的CollectionUtils,因为CollectionUtils会在web容器中引用，
 * 因java1.5 ClassLoader的原因，所引用的jar包版本与web容器中引用的版本不同，
 * 可能加载失败，导致项目不能启动</pre>
 *
 */
public class CollectionUtil {

    static int INDEX_NOT_FOUND = -1;

    /**
     * Checks if is empty.
     *
     * @param collection the collection
     * @return true, if is empty
     */
    public static boolean isEmpty(final Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Checks if is not empty.
     *
     * @param collection the collection
     * @return true, if is not empty
     */
    public static boolean isNotEmpty(final Collection<?> collection) {
        return !(isEmpty(collection));
    }

    /**
     * Gets the one.
     *
     * @param <T> the generic type
     * @param list the list
     * @return the one
     */
    public static <T> T getFirst(final List<T> list) {
        if (isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public static <T> T getFirst(final Set<T> set) {
        if (isNotEmpty(set)) {
            return set.iterator().next();
        }
        return null;
    }

    public static <T> T getFirst(final T[] arr) {
        if (isNotEmpty(arr)) {
            return arr[0];
        }
        return null;
    }

    public static <K, V> V getFirst(Map<K, V> map) {
        if (isNotEmpty(map)) {
            Iterator<V> iterable = map.values().iterator();
            return iterable.next();
        }
        return null;
    }

    public static <K, V> K getFirstKey(Map<K, V> map) {
        if (isNotEmpty(map)) {
            Iterator<K> iterable = map.keySet().iterator();
            return iterable.next();
        }
        return null;
    }

    public static <K, V> V get(Map<K, V> map, K key) {
        if (isNotEmpty(map)) {
            return map.get(key);
        }
        return null;
    }

    public static <K, V> List<K> getKeys(Map<K, V> map) {
        if (isNotEmpty(map)) {
            return new ArrayList<K>(map.keySet());
        }
        return new ArrayList<K>(0);
    }

    public static <K, V> List<V> getValues(Map<K, V> map) {
        if (isNotEmpty(map)) {
            return new ArrayList<V>(map.values());
        }
        return new ArrayList<V>(0);
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Checks if is not empty.
     *
     * @param map the map
     * @return true, if is not empty
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !(isEmpty(map));
    }

    /**
     * Checks if is empty.
     *
     * @param <T> the generic type
     * @param array the array
     * @return true, if is empty
     */
    public static <T> boolean isEmpty(final T[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Checks if is not empty.
     *
     * @param <T> the generic type
     * @param array the array
     * @return true, if is not empty
     */
    @SafeVarargs
    public static <T> boolean isNotEmpty(final T... array) {
        return !(isEmpty(array));
    }

    /**
     * Index of.
     *
     * @param array the array
     * @param objectToFind the object to find
     * @return the int
     */
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) >= 0;
    }

    /**
     * Index of.
     *
     * @param array the array
     * @param objectToFind the object to find
     * @param startIndex the start index
     * @return the int
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Index of ignore case.
     *
     * @param array the array
     * @param stringToFind the string to find
     * @return the int
     */
    public static int indexOfIgnoreCase(String[] array, String stringToFind) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }

        if (stringToFind == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            String strLower = stringToFind.toLowerCase();
            for (int i = 0; i < array.length; i++) {
                if (strLower.equals(StringUtil.toLowerCase(array[i]))) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOneByType(Object[] array, Class<T> typeToFind) {
        if (isEmpty(array)) {
            return null;
        }
        for (Object object : array) {
            if (typeToFind.isInstance(object)) {
                return (T) object;
            }
        }
        return null;
    }

    public static <T> T getLast(List<T> sources) {
        if (CollectionUtil.isEmpty(sources)) {
            return null;
        }
        return sources.get(sources.size() - 1);
    }

    public static <K, V> boolean hasValue(Map<K, V> map, Object key, Object value) {
        return (map != null) && (key != null) && (value != null) && value.equals(map.get(key));
    }

    public static List<String> toList(String ids, String split) {
        List<String> result = null;
        if (StringUtil.isNotBlank(ids)) {
            String[] IdArray = ids.split(split);
            result = new ArrayList<String>();
            for (String id : IdArray) {
                if (StringUtil.isNotBlank(id)) {
                    result.add(id);
                }
            }
        }
        return result;
    }

    public static List<Integer> toIntList(String ids, String split) {
        List<Integer> result = null;
        if (StringUtil.isNotBlank(ids)) {
            String[] IdArray = ids.split(split);
            result = new ArrayList<Integer>();
            for (String id : IdArray) {
                if (StringUtil.isNotBlank(id)) {
                    Integer intId = Integer.parseInt(id);
                    result.add(intId);
                }
            }
        }
        return result;
    }

    /**
     * The Interface KeyCalculator.
     *
     * @param <NewKey> the generic type
     * @param <T> the generic type
     */
    public static interface KeyCalculator<NewKey, T> {
        NewKey calculateKey(T t);
    }

    public static <K, T> Map<K, T> toMap(Map<K, T> dest, Collection<T> items, Function<? super T, K> getMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<K, T> source = items.stream().filter(e -> {
                return e != null;
            }).collect(Collectors.toMap(getMethod, Function.identity(), (key1, key2) -> key2));

            if (dest != null) {
                dest.putAll(source);
                return dest;
            } else {
                return source;
            }
        }

        if (dest != null) {
            return dest;
        }

        return new HashMap<K, T>(0);
    }

    public static <K, T> Map<K, T> toMap(Collection<T> items, Function<? super T, K> getMethod) {
        return toMap(null, items, getMethod);
    }

    //    @SafeVarargs
    //    public static <T> Map<String, T> toMap(Map<String, T> dest, Collection<T> items, Function<? super T, ?>... getMethods) {
    //        if (CollectionUtil.isNotEmpty(items)) {
    //            Map<String, T> result = dest != null ? dest : new HashMap<String, T>(items.size());
    //            for (T t : items) {
    //                if (t != null) {
    //                    StringBuffer sb = new StringBuffer();
    //                    boolean append =false;
    //                    for (Function<? super T, ?> function : getMethods) {
    //                        if (append){
    //                            sb.append('.') ;
    //                        }
    //                        Object key = function.apply(t);
    //                        sb.append(key);
    //                        append=append || true;
    //                    }
    //                    result.put(sb.toString(), t);
    //                }
    //            }
    //            return result;
    //        }
    //
    //        if (dest != null) {
    //            return dest;
    //        }
    //        return new HashMap<String, T>(0);
    //    }

    public static <K, V, T> Map<K, V> toMap(Map<K, V> dest, Collection<T> items, Function<? super T, K> keyGetMethod,
                                            Function<? super T, V> valueGetMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<K, V> result = dest != null ? dest : new HashMap<K, V>(items.size());
            for (T t : items) {
                if (t != null) {
                    K k = keyGetMethod.apply(t);
                    if (k != null) {
                        V v = valueGetMethod.apply(t);
                        result.put(k, v);
                    }
                }
            }
            return result;
        }

        if (dest != null) {
            return dest;
        }
        return new HashMap<K, V>(0);
    }

    public static <K, V, T> Map<K, V> toMap(Collection<T> items, Function<? super T, K> keyGetMethod, Function<? super T, V> valueGetMethod) {
        return toMap(null, items, keyGetMethod, valueGetMethod);
    }

    //    @SafeVarargs
    //    public static <T> Map<String, T> toMap(Collection<T> items, Function<? super T, ?>... getMethods) {
    //        return toMap(null, items, getMethods);
    //    }

    public static <NewKey, K, T> Map<NewKey, T> toMap(Map<NewKey, T> dest, KeyCalculator<NewKey, T> keyCalculator, Collection<T> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<NewKey, T> result = dest != null ? dest : new HashMap<NewKey, T>(items.size());
            for (T t : items) {
                if (t != null) {
                    NewKey key = keyCalculator.calculateKey(t);
                    result.put(key, t);
                }
            }
            return result;
        }

        if (dest != null) {
            return dest;
        }
        return new HashMap<NewKey, T>(0);
    }

    public static <NewKey, K, T> Map<NewKey, T> toMap(KeyCalculator<NewKey, T> keyCalculator, Collection<T> items) {
        return toMap(null, keyCalculator, items);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> toMap(Map<K, V> dest, Function<? super V, K> getMethod, V... items) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<K, V> result = dest != null ? dest : CollectionUtil.newMap(items.length);
            for (V v : items) {
                if (v != null) {
                    K k = getMethod.apply(v);
                    result.put(k, v);
                }
            }
            return dest;
        }
        return CollectionUtil.newEmptyMap();
    }

    @SafeVarargs
    public static <K, V> Map<K, V> toMap(Function<? super V, K> getMethod, V... items) {
        return toMap(null, getMethod, items);
    }

    public static <K, T> Map<K, List<T>> toGroup(Collection<T> items, Function<? super T, K> getMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<K, List<T>> result = new HashMap<K, List<T>>(items.size());
            for (T t : items) {
                if (t != null) {
                    K k = getMethod.apply(t);
                    List<T> list = result.get(k);
                    if (list == null) {
                        list = new ArrayList<T>();
                        result.put(k, list);
                    }
                    list.add(t);
                }
            }
            return result;
        }
        return new HashMap<K, List<T>>(0);
    }

    public static <K, T, V> Map<K, List<V>> toGroup(Collection<T> items, Function<? super T, K> getMethod, Function<? super T, V> getValueMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            Map<K, List<V>> result = new HashMap<K, List<V>>(items.size());
            for (T t : items) {
                if (t != null) {
                    K k = getMethod.apply(t);
                    List<V> list = result.get(k);
                    if (list == null) {
                        list = new ArrayList<V>();
                        result.put(k, list);
                    }
                    list.add(getValueMethod.apply(t));
                }
            }
            return result;
        }
        return new HashMap<K, List<V>>(0);
    }

    public static <V, T> List<V> toList(Collection<T> items, Function<? super T, V> getMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            List<V> result = getNotNullStream(items, getMethod).collect(Collectors.toList());
            return result;
        }
        return new ArrayList<V>(0);
    }

    public static <K, T> List<T> filterListToList(Map<K, T> itemMap, Collection<K> filterValues) {
        List<T> result = new ArrayList<T>();
        if (CollectionUtil.isNotEmpty(itemMap)) {
            for (K k : filterValues) {
                T t = itemMap.get(k);
                if (t != null) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public static <K, T> Map<K, T> filterListToMap(Map<K, T> itemMap, Collection<K> filterValues) {
        Map<K, T> result = new LinkedHashMap<K, T>();
        if (CollectionUtil.isNotEmpty(itemMap)) {
            for (K k : filterValues) {
                T t = itemMap.get(k);
                if (t != null) {
                    result.put(k, t);
                }
            }
        }
        return result;
    }

    public static <V, T> Set<V> toSet(Collection<T> items, Function<? super T, V> getMethod) {
        if (CollectionUtil.isNotEmpty(items)) {
            Set<V> result = getNotNullStream(items, getMethod).collect(Collectors.toSet());
            return result;
        }
        return new HashSet<V>(0);
    }

    public static <V, D> Stream<V> getNotNullStream(Collection<D> items, Function<? super D, V> getMethod) {
        Stream<V> filteredStream = items.stream().filter(d -> {
            if (d != null) {
                return getMethod.apply(d) != null;
            }
            return false;
        }).map(getMethod);
        return filteredStream;
    }

    public static <D, K, S> void setListValue(Collection<D> dests, S value, BiConsumer<D, S> destSetMethod) {
        if (isNotEmpty(dests)) {
            for (D d : dests) {
                if (d != null) {
                    destSetMethod.accept(d, value);
                }
            }
        }
    }

    public static <K, D, S, V> void setValueByMap(Collection<D> dests, Function<? super D, K> destGetMethod, Map<K, S> sourceMap,
                                                  Function<? super S, Object> sourceGetMethod, BiConsumer<D, Object> destSetMethod) {
        if (CollectionUtil.isNotEmpty(dests) && (CollectionUtil.isNotEmpty(sourceMap))) {
            for (D d : dests) {
                if (d != null) {
                    K key = destGetMethod.apply(d);
                    S source = sourceMap.get(key);
                    if (source != null) {
                        Object sourceValue = sourceGetMethod.apply(source);
                        destSetMethod.accept(d, sourceValue);
                    }
                }
            }
        }
    }

    public static <D, K, S> void setModelByMap(Collection<D> dests, Map<K, S> sourceMap, Function<? super D, K> destGetMethod,
                                               BiConsumer<D, S> destSetMethod) {
        if (CollectionUtil.isNotEmpty(dests) && CollectionUtil.isNotEmpty(sourceMap)) {
            for (D d : dests) {
                if (d != null) {
                    K key = destGetMethod.apply(d);
                    if (key != null) {
                        S s = sourceMap.get(key);
                        destSetMethod.accept(d, s);
                    }
                }
            }
        }
    }

    public static <D, K, S> void setModelByList(Collection<D> dests, Collection<S> sources, Function<? super D, K> destGetMethod,
                                                BiConsumer<D, S> destSetMethod, Function<? super S, K> sourceGetMethod) {
        Map<K, S> sourceMap = CollectionUtil.toMap(sources, sourceGetMethod);
        CollectionUtil.setModelByMap(dests, sourceMap, destGetMethod, destSetMethod);
    }

    public static <D, K, S, V> void setFeildToFieldByMap(Collection<D> dests, Map<K, S> sourceMap, Function<? super D, K> destGetMethod,
                                                         Function<? super S, V> sourceGetMethod, BiConsumer<D, V> destSetMethod) {
        if (isNotEmpty(dests)) {
            for (D d : dests) {
                if (d != null) {
                    K key = destGetMethod.apply(d);
                    if (key != null) {
                        V v = null;
                        S s = sourceMap.get(key);
                        if (s != null) {
                            v = sourceGetMethod.apply(s);
                        }

                        if (v != null) {
                            destSetMethod.accept(d, v);
                        }
                    }
                }
            }
        }
    }

    public static <D, K, S> void setListByMap(List<D> dests, Map<K, List<S>> sourceListMap, Function<? super D, K> destGetMethod,
                                              BiConsumer<D, List<S>> destSetMethod) {
        for (D d : dests) {
            if (d != null) {
                K key = destGetMethod.apply(d);
                if (key != null) {
                    List<S> list = sourceListMap.get(key);
                    destSetMethod.accept(d, list);
                }
            }
        }
    }

    /*** 把 sources  按 id分组 ，变成List,设置到 dests中一个字段*/
    public static <D, K, S> void setListByList(List<D> dests, List<S> sources, Function<? super D, K> destGetMethod,
                                               BiConsumer<D, List<S>> destSetMethod, Function<? super S, K> sourceGetMethod) {
        Map<K, List<S>> sourceListMap = toGroup(sources, sourceGetMethod);
        setListByMap(dests, sourceListMap, destGetMethod, destSetMethod);
    }

    public static <D, K, S> void setListByValues(List<D> dests, List<S> sources, Function<? super D, List<K>> destGetMethod,
                                                 BiConsumer<D, List<S>> destSetMethod, Function<? super S, K> sourceGetMethod) {
        if (CollectionUtil.isNotEmpty(dests) && CollectionUtil.isNotEmpty(sources)) {
            Map<K, S> sourcesMap = CollectionUtil.toMap(sources, sourceGetMethod);
            for (D d : dests) {
                if (d != null) {
                    List<K> ks = destGetMethod.apply(d);
                    if (CollectionUtil.isNotEmpty(ks)) {
                        List<s> filtered = new ArrayList<S>();
                        for (K k : ks) {
                            if (k != null) {
                                S s = sourcesMap.get(k);
                                if (s != null) {
                                    filtered.add(s);
                                }
                            }
                        }
                        destSetMethod.accept(d, filtered);
                    }
                }
            }
        }
    }

    /*** 把 sources 中 某个值 按 id分组 ，变成List,设置到 dests中一个字段*/
    public static <D, K, S, V> void setListByList(List<D> dests, List<S> sources, Function<? super D, K> destGetMethod,
                                                  BiConsumer<D, List<V>> destSetMethod, Function<? super S, K> sourceGetMethod,
                                                  Function<? super S, V> sourceGetValueMethod) {
        Map<K, List<V>> sourceListMap = toGroup(sources, sourceGetMethod, sourceGetValueMethod);
        setListByMap(dests, sourceListMap, destGetMethod, destSetMethod);
    }

    public static <T> List<T> newEmptyList() {
        return new ArrayList<T>(0);
    }

    public static <T> Set<T> newEmptySet() {
        return new HashSet<T>(0);
    }

    public static <T> List<T> newArrayList(Collection<T> items) {
        if (items == null) {
            return newEmptyList();
        }
        return new ArrayList<T>(items);
    }

    public static <T> Set<T> newSet(Collection<T> items) {
        if (items == null) {
            return newEmptySet();
        }
        return new HashSet<T>(items);
    }

    public static <K, T> Map<K, T> newEmptyMap() {
        return newMap(0);
    }

    public static <K, T> Map<K, T> newMap(int size) {
        return new HashMap<K, T>(size);
    }

    public static <T, V> List<T> filter(List<T> sources, Function<? super T, V> sourceGetGetMethod, V filterValue) {
        return sources.stream().filter(d -> {
            if (d != null) {
                V v = sourceGetGetMethod.apply(d);
                if (filterValue == null) {
                    if (v == null) {
                        return true;
                    }
                    return false;
                }
                return filterValue.equals(v);
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static <T, V> List<T> filterBySet(List<T> sources, Function<? super T, V> sourceGetMethod, Set<V> valueSet) {
        if (CollectionUtil.isEmpty(valueSet)) {
            return new ArrayList<T>(0);
        }

        if (CollectionUtil.isNotEmpty(sources)) {
            List<T> result = sources.stream().filter(d -> {
                if (d != null) {
                    V v = sourceGetMethod.apply(d);
                    return valueSet.contains(v);
                }
                return false;
            }).collect(Collectors.toList());
            return result;
        }
        return sources;
    }

    public static <T, ID, SUBID> Map<ID, Map<SUBID, T>> getIdSubIdObjectMap(List<T> sources, Function<? super T, ID> idGetMethod,
                                                                            Function<? super T, SUBID> subIdGetMethod) {
        if (CollectionUtil.isEmpty(sources)) {
            return CollectionUtil.newEmptyMap();
        }

        Map<ID, List<T>> idTListMap = CollectionUtil.toGroup(sources, idGetMethod);
        Map<ID, Map<SUBID, T>> idSubIdSetMap = new HashMap<ID, Map<SUBID, T>>(idTListMap.size());
        for (Entry<ID, List<T>> entry : idTListMap.entrySet()) {
            Map<SUBID, T> itemIdSet = CollectionUtil.toMap(entry.getValue(), subIdGetMethod);
            idSubIdSetMap.put(entry.getKey(), itemIdSet);
        }
        return idSubIdSetMap;
    }

    public static <T> boolean contains(Collection<T> items, T t) {
        return CollectionUtil.isNotEmpty(items) && items.contains(t);
    }

    public static <T, V> boolean contains(Map<T, V> map, T t) {
        return CollectionUtil.isNotEmpty(map) && map.containsKey(t);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, L extends List> GetOrCreateWrap<L> getOrCreateList(K key, Map<K, L> sourceMap, Class<? extends List> listClz) {
        List destList = sourceMap.get(key);
        boolean isCreate = false;
        if (destList == null) {
            destList = BeanUtils.instantiate(listClz);
            sourceMap.put(key, (L) destList);
            isCreate = true;
        }
        return new GetOrCreateWrap(destList, isCreate);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, M extends Map> GetOrCreateWrap<M> getOrCreateMap(K key, Map<K, M> sourceMap, Class<? extends Map> mapClz) {
        M destMap = sourceMap.get(key);
        boolean isCreate = false;
        if (destMap == null) {
            destMap = (M) BeanUtils.instantiate(mapClz);
            sourceMap.put(key, destMap);
            isCreate = true;
        }
        return new GetOrCreateWrap(destMap, isCreate);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, V> GetOrCreateWrap<V> getOrCreateObjectFromMap(K key, Map<K, V> sourceMap, Class<V> vClz) {
        V dest = sourceMap.get(key);
        boolean isCreate = false;
        if (dest == null) {
            dest = BeanUtils.instantiate(vClz);
            sourceMap.put(key, dest);
            isCreate = true;
        }
        return new GetOrCreateWrap(dest, isCreate);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, V> GetOrCreateWrap<Map<K, V>> getOrCreateMapFormThreadLocal(ThreadLocal<Map<K, V>> sourceThrdLoc, Class<? extends Map> mapClz) {
        Map<K, V> destMap = sourceThrdLoc.get();
        boolean isCreate = false;
        if (destMap == null) {
            destMap = BeanUtils.instantiate(mapClz);
            sourceThrdLoc.set(destMap);
            isCreate = true;
        }

        return new GetOrCreateWrap(destMap, isCreate);
    }

    /***以下2个method从dalgen中复制过来的，不严谨*/
    public static Map<String, String> toUpCaseMap(String fields) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtil.isNotEmpty(fields)) {
            String[] flds = fields.split(",");
            for (String fld : flds) {
                fld = fld.trim();
                if (StringUtil.isNotEmpty(fld)) {
                    result.put(fld.toUpperCase(), fld);
                }
            }
        }
        return result;
    }

    public static boolean mapContainsKey(Map<String, String> map, String key) {
        return map.containsKey(key);
    }

    /*** 添加成功，返回true,否则返回false*/
    public static <K, V> boolean putIfNotExists(Map<K, V> destMap, K key, V value) {
        if (destMap.containsKey(key)) {
            return false;
        }
        destMap.put(key, value);
        return true;
    }

    public static <K> boolean addIfNotExists(Set<K> set, K key) {
        if (set.contains(key)) {
            return false;
        }
        set.add(key);
        return true;
    }
}
