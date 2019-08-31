package org.stategen.framework.util;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface AssignService<T, K> {
    /***
     * 查出来赋值给一个子项,比如 把teacher 查出来赋给student的属性 teacher
     * 
     * @param dests
     * @param destGetMethod
     * @param destSetMethod
     */
    <D> void assignBeanTo(Collection<D> dests, Function<? super D, K> destGetMethod, BiConsumer<D, T> destSetMethod);

    /***
    * 查出来赋值给一个子项,比如 把students 查出来赋给teacher的属性 students
    * 
    * @param dests
    * @param destGetMethod
    * @param destSetMethod
    */
    <D, G> void assignBeansTo(Collection<D> dests, Function<? super D, G> destGetMethod, BiConsumer<D, List<T>> destSetMethod,
                              BiConsumer<T, List<G>> resultSetQueryIdsFun, Function<? super T, G> resultGetGoupIdFun);

    /***
    * 相当于联合查询
    * 
    * @param dests
    * @param destGetMethod
    */
    <D> void mergeBeanTo(Collection<D> dests, Function<? super D, K> destGetMethod);

}
