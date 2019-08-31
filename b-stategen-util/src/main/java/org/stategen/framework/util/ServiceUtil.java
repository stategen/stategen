package org.stategen.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.stategen.framework.lite.PageList;

public class ServiceUtil {

    public static <D, T, K, S> void interalAssignBeanTo(Collection<D> dests, Function<? super D, K> destGetMethod, BiConsumer<D, T> destSetMethod,
                                                        S provider, BiFunction<S, List<K>, List<T>> serviceByIdsFun,
                                                        Function<? super T, K> resultGetIdFun) {
        if (CollectionUtil.isNotEmpty(dests)) {
            Set<K> keys = CollectionUtil.toSet(dests, destGetMethod);
            List<T> results = serviceByIdsFun.apply(provider, new ArrayList<K>(keys));
            if (CollectionUtil.isNotEmpty(results)) {
                CollectionUtil.setModelByList(dests, results, destGetMethod, destSetMethod, resultGetIdFun);
            }
        }
    }

    public static <D, T, G, S extends BaseService<T>> void interalAssignBeansTo(Collection<D> dests, Function<? super D, G> destGetMethod,
                                                                                BiConsumer<D, List<T>> destSetMethod, S provider, T query,
                                                                                BiConsumer<T, List<G>> resultSetQueryIdsFun,
                                                                                Function<? super T, G> resultGetGoupIdFun, Integer maxLenth) {
        if (CollectionUtil.isNotEmpty(dests)) {
            Set<G> keys = CollectionUtil.toSet(dests, destGetMethod);
            resultSetQueryIdsFun.accept(query, new ArrayList<G>(keys));
            PageList<T> pageList = provider.getPageList(query, maxLenth, 1);
            List<T> results = pageList.getItems();
            if (CollectionUtil.isNotEmpty(results)) {
                Map<G, List<T>> resultGroup = CollectionUtil.toGroup(results, resultGetGoupIdFun);
                CollectionUtil.setListByMap(dests, resultGroup, destGetMethod, destSetMethod);
            }
        }
    }

    public static <D, T, K, S> void interalMergeBeanTo(Collection<D> dests, Function<? super D, K> destGetMethod, S provider,
                                                       BiFunction<S, List<K>, List<T>> serviceByIdsFun, Function<? super T, K> resultGetIdFun) {
        if (CollectionUtil.isNotEmpty(dests)) {
            Map<K, List<D>> destsGroup = CollectionUtil.toGroup(dests, destGetMethod);
            Set<K> keys = destsGroup.keySet();
            List<T> results = serviceByIdsFun.apply(provider, new ArrayList<K>(keys));
            for (T t : results) {
                K id = resultGetIdFun.apply(t);
                List<D> destSub = destsGroup.get(id);
                if (CollectionUtil.isNotEmpty(destSub)) {
                    for (D d : destSub) {
                        CopyUtil.merge(t, d);
                        CopyUtil.merge(d, t);
                    }
                }
            }
        }
    }

}
