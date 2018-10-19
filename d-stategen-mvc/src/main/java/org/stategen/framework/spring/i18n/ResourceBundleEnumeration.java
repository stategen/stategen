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
package org.stategen.framework.spring.i18n;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * *
 * 国际化中 内部 enumeration 实现类,业务代码无需关注此类.
 *
 * @author XiaZhengsheng
 */
public class ResourceBundleEnumeration implements Enumeration<String> {
    Set<String>         set;

    Iterator<String>    iterator;

    Enumeration<String> enumeration;

    String              next;

    public ResourceBundleEnumeration(final Set<String> set, final Enumeration<String> enumeration) {

        this.next = null;

        this.set = set;

        this.iterator = set.iterator();

        this.enumeration = enumeration;

    }

    @Override
    public boolean hasMoreElements() {

        if (this.next == null) {
            if (this.iterator.hasNext()) {
                this.next = this.iterator.next();
            } else if (this.enumeration != null) {
                while (this.next == null && this.enumeration.hasMoreElements()) {
                    this.next = this.enumeration.nextElement();
                    if (this.set.contains(this.next)) {
                        this.next = null;
                    }
                }
            }
        }
        return this.next != null;
    }

    @Override
    public String nextElement() {
        if (this.hasMoreElements()) {
            final String next = this.next;
            this.next = null;
            return next;
        }
        throw new NoSuchElementException();
    }
}