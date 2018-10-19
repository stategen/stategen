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
/**
 * 该类复制自 spring相关类，
 * 不区分key大小写的HashMap
 */
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The Class CaseInsensitiveHashMap.
 *
 * @param <V> the value type
 */
public class CaseInsensitiveHashMap<V> extends HashMap<String, V> {

    private static final long         serialVersionUID = 1L;

    private final Map<String, String> caseInsensitiveKeys;

    private final Locale              locale;

    public CaseInsensitiveHashMap() {
        this(null);
    }

    public CaseInsensitiveHashMap(Locale locale) {
        super();
        this.caseInsensitiveKeys = new HashMap<String, String>();
        this.locale = (locale != null ? locale : Locale.getDefault());
    }

    public CaseInsensitiveHashMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    public CaseInsensitiveHashMap(int initialCapacity, Locale locale) {
        super(initialCapacity);
        this.caseInsensitiveKeys = new HashMap<String, String>(initialCapacity);
        this.locale = (locale != null ? locale : Locale.getDefault());
    }

    @Override
    public V put(String key, V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            super.remove(oldKey);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.caseInsensitiveKeys
            .containsKey(convertKey((String) key)));
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return super.get(this.caseInsensitiveKeys.get(convertKey((String) key)));
        } else {
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            return super.remove(this.caseInsensitiveKeys.remove(convertKey((String) key)));
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        this.caseInsensitiveKeys.clear();
        super.clear();
    }

    protected String convertKey(String key) {
        return key.toLowerCase(this.locale);
    }

}