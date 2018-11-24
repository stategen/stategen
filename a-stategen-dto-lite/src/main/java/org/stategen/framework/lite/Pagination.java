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
package org.stategen.framework.lite;

/**
 * The Class Pagination.
 */
public class Pagination implements IPagination {

    private transient PageList<?> pageList;

    private int page = 1;
    private int pageSize = 5;
    
    public Pagination() {

    }
    
    public Pagination(PageList<?> pageList) {
        this.pageList = pageList;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrent() {
        return (int) pageList.getPageNum();
    }

    public int getPageSize() {
        if (pageList != null) {
            return (int) pageList.getPageSize();
        }
        return pageSize;
    }

    public long getTotal() {
        return pageList.getTotalCount();
    }

    public long getTotalPages() {
        return pageList.getTotalPages();
    }

}
