package org.stategen.framework.util;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface AssignSerice<S,K> {
    
   <D> void assignBeanTo(Collection<D> dests,Function<? super D, K> destGetMethod,BiConsumer<D, S> destSetMethod);
}
