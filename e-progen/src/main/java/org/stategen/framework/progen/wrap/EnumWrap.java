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
package org.stategen.framework.progen.wrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.stategen.framework.util.EnumUtil;

/**
 * The Class EnumWrap.
 *
 * @param <T> the generic type
 */
public class EnumWrap<T extends Enum<T>> extends EntityWrap implements CanbeImportWrap {

    private List<EnumFieldWrap<T>> enumFieldWraps = new ArrayList<EnumFieldWrap<T>>();

    private Map<T, String> _enumValueDescList;

    @Override
    public String getImportPath() {
        return CanbeImportWrap.super.getImportPath();
    }

    @Override
    public void setClazz(Class<?> clz) {
        super.setClazz(clz);
        genEnumFieldWraps();
    }

    @SuppressWarnings("unchecked")
    public void genEnumFieldWraps() {
        Class<?> clz = getClazz();
        T[] enumConstants = (T[]) clz.getEnumConstants();
        for (T t : enumConstants) {
            EnumFieldWrap<T> enumFieldWrap = new EnumFieldWrap<T>(t, this);
            enumFieldWraps.add(enumFieldWrap);
        }
    }

    public List<EnumFieldWrap<T>> getEnums() {
        return enumFieldWraps;
    }

    @SuppressWarnings("unchecked")
    public Map<T, String> getEnumValueDescList() {
        if (this._enumValueDescList == null) {
            Class<T> clazz = (Class<T>) this.getClazz();
            _enumValueDescList = EnumUtil.getEnumValueDescList(clazz);
        }
        return _enumValueDescList;
    }

}
