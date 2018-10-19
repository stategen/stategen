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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class CaseInsensitiveSet.
 * 不区分大小写的HashSet
 */
public class CaseInsensitiveSet implements Set<String>,Serializable {
    
    /**
     * *  The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * *  The lower map.
     */
    private Map<String, String> lowerMap;
    
    /**
     * Instantiates a new case insensitive set.
     */
    public CaseInsensitiveSet() {
        super();
        lowerMap=new ConcurrentHashMap<String, String>();
    }
    
    
    /**
     * Instantiates a new case insensitive set.
     *
     * @param capcity the capcity
     */
    public CaseInsensitiveSet(Integer capcity) {
        super();
        lowerMap=new ConcurrentHashMap<String, String>(capcity);
    }
    
    /**
     * Instantiates a new case insensitive set.
     *
     * @param c the c
     */
    public CaseInsensitiveSet(Collection<? extends String> c) {
        super();
        lowerMap=new ConcurrentHashMap<String, String>(c.size());
        this.addAll(c);
    }

    /** 
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return lowerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return lowerMap.isEmpty();
    }

    /** 
     * @see java.util.Set#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return lowerMap.containsKey(((String)o).toLowerCase());
    }

    /** 
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<String> iterator() {
        return lowerMap.values().iterator();
    }

    /** 
     * @see java.util.Set#toArray()
     */
    @Override
    public Object[] toArray() {
        return lowerMap.values().toArray();
    }

    /** 
     * @see java.util.Set#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return lowerMap.values().toArray(a);
    }

    /** 
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(String e) {
        lowerMap.put(e.toLowerCase(), e);
        return true;
    }

    /** 
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        lowerMap.remove(((String)o).toLowerCase());
        return true;
    }

    /** 
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
          if (!lowerMap.containsKey(((String)o).toLowerCase())){
              return false;
          }
        }
        return true;
    }

    /** 
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends String> c) {
        for (String s : c) {
            lowerMap.put(s.toLowerCase(), s);
        }
        return true;
    }

    /** 
     * @see java.util.Set#clear()
     */
    @Override
    public void clear() {
        lowerMap.clear();
    }


    /** 
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        clear();
        for (Object o : c) {
            lowerMap.put(((String)o).toLowerCase(), (String)o);
        }
        return true;
    }

    /** 
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            lowerMap.remove(((String)o).toLowerCase());
        }
        return true;
    }

    
}