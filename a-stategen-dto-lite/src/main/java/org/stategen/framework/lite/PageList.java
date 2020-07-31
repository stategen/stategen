package org.stategen.framework.lite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 包含“分页”信息的List
 * 
 * <p>要得到总页数请使用 toPaginator().getTotalPages();</p>
 *  该类用于返回dalgen中分页查询结果
 *
 * @author badqiu
 * @param <E> the element type
 */
public class PageList<E> implements IPageList, Serializable {

    private static final long serialVersionUID = 1412759446332294209L;

    /** 分页大小 */
    private long pageSize;
    /** 页数  */
    private long pageNum;
    /** 总记录数 */
    private long totalCount;
    /** 总页数*/
    private long totalPages;

    private List<E> items;


    public PageList() {
        items = new ArrayList<>(0);
    }

    public PageList(long pageNum, long pageSize, long totalCount) {
        this();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        calculteTotalPages();
    }

    public PageList(List<E> items, long pageNum, long pageSize, long totalCount) {
        this(pageNum, pageSize, totalCount);
        this.setItems(items);
        calculteTotalPages();
    }

    private void calculteTotalPages() {
        if (totalCount != 0 && pageSize != 0) {
            double c = totalCount / pageSize;
            this.totalPages = ((long) Math.ceil(c));
        }
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public Pagination getPagination() {
        return new Pagination(this);
    }

    public E first() {
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

}
