package org.stategen.framework.ibatis.util;

/**
 * 分页查询对象
 * 该类复制自rapid_framework.
 *
 * @author badqiu
 * @author hunhun
 * @version $Id: PageQuery.java,v 0.1 2010-11-29 下午05:34:12 badqiu Exp $
 */
public class PageQuery implements java.io.Serializable {
    private static final long serialVersionUID  = -8000900575354501298L;

    public static final int   DEFAULT_PAGE_SIZE = 10;
    /** 页数  */
    private int               page;
    /** 分页大小 */
    private int               pageSize          = DEFAULT_PAGE_SIZE;

    public PageQuery() {
    }

    public PageQuery(int pageSize) {
        this.pageSize = pageSize;
    }

    public PageQuery(PageQuery query) {
        this.page = query.page;
        this.pageSize = query.pageSize;
    }

    public PageQuery(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "page:" + page + ",pageSize:" + pageSize;
    }

}
