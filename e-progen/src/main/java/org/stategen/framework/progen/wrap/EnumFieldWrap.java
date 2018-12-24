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

import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class EnumFieldWrap.
 *
 * @param <T> the generic type
 */
public class EnumFieldWrap<T extends Enum<T>> {

    private Enum<T> enumConst;

    private String description;
    private String title;
    private String _apiDescription;

    private EnumWrap<T> enumWrap;

    public EnumFieldWrap(T t, EnumWrap<T> enumWrap) {
        super();
        this.enumConst = t;
        this.enumWrap = enumWrap;
    }

    public String getApiDescription() throws NoSuchFieldException, SecurityException {
        if (_apiDescription == null) {
            String enumName = ((Enum<?>) enumConst).name();
            _apiDescription = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::value, "",
                enumConst.getClass().getField(enumName));
        }
        return _apiDescription;
    }

    public String getDescription() throws NoSuchFieldException, SecurityException {
        if (description == null) {
            description = getApiDescription();
            if (StringUtil.isEmpty(description)) {
                description = this.enumWrap.getEnumValueDescList().get(enumConst);
            }
        }
        return description;
    }

    public Enum<T> getEnum() {
        return enumConst;
    }

    @Override
    public String toString() {
        return enumConst.toString();
    }

    public String getTitle() throws NoSuchFieldException, SecurityException {
        if (title == null) {
            String description = this.getDescription();
            title = StringUtil.trimLeftTo(description, " ");
        }
        return title;
    }
}
