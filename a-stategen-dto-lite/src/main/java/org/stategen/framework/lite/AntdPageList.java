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

import java.util.List;

import javax.org.stategen.framework.lite.BusinessException;

import org.stategen.framework.annotation.GenForm;

/**
 * The Class AntdPageList.
 *
 * @param <E> the element type
 */
@GenForm(false)
public class AntdPageList<E> implements IPageList {
    
    private transient PageList<E> pageList;
    
    
    public static <T> AntdPageList<T> create(PageList<T> pageList){
        return new AntdPageList<T>(pageList);
    }
    
    /**
     * Instantiates a new antd page list.
     *
     * @param pageList the page list
     */
    public AntdPageList(PageList<E> pageList){
        if (pageList==null){
            throw new BusinessException("pageList不能为空");
        }
        this.pageList =pageList;
    }
    
    public Pagination getPagination() {
        return pageList.getPagination();
    }
    
    public List<E> getList(){
        return pageList.getItems();
    }

}
