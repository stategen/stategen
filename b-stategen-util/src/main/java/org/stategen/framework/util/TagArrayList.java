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

/**
 * The Class TagArrayList.
 *
 * @param <T> the generic type
 */
public class TagArrayList<T> extends ArrayList<T>{
    /**  */
    private static final long serialVersionUID = 1L;
    private boolean tagged =false;
    
    public Boolean isTagged() {
        return tagged;
    }
    
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }
    
}
