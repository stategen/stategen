package org.stategen.framework.util;

import org.stategen.framework.lite.PageList;

public interface BaseService<T> {
    public T insert(T t);

    public T update(T t);

    PageList<T> getPageList(T t, int pageSize, int pageNum);

}
