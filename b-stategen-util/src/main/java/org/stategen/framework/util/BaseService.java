package org.stategen.framework.util;

import org.stategen.framework.lite.PageList;

public interface BaseService<T> {
    public T insert(T t);

    public T update(T t);

    PageList<T> getPageList(T query, int pageSize, int pageNum);
    
    default public T getFirst(T query) {
       return  getPageList(query,1,1).first();
    }

}
