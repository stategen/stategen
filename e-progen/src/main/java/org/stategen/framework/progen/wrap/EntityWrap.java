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

import io.swagger.annotations.ApiModel;

/**
 * The Class EntityWrap.
 */
public class EntityWrap extends BaseWrap {

    private String _apiDescription = null;

    public String getApiDescription() {
        if (_apiDescription == null) {
            _apiDescription = AnnotationUtil.getAnnotationValueFormMembers(ApiModel.class, ApiModel::value,"", getClazz());
        }
        return _apiDescription;
    }

    @Override
    public String getDescription() {
        String description = super.getDescription();
        if (StringUtil.isEmpty(description)) {
            description = getApiDescription();
            if (description == null) {
                description = "";
            }
            setDescription(description);
        }
        return super.getDescription();
    }
}
