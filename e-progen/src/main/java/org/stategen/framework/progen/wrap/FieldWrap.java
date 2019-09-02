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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import org.stategen.framework.progen.NamedContext;
import org.stategen.framework.util.AnnotationUtil;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * The Class FieldWrap.
 */
public class FieldWrap extends NamedWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FieldWrap.class);

    /***一些annotations从field上找，其它从 getMethod上长*/
    private Field field;

    AnnotatedElement[] members;

    /**是否是父类中的字段*/
    private Boolean isSuper = false;

    private Boolean serialize;
    private Boolean deserialize;

    public FieldWrap(NamedContext context) {
        super(context);
    }

    @Override
    public AnnotatedElement[] getMembers() {
        if (members == null) {
            members = super.getMembers();
            if (field == null) {
                return members;
            }

            members = new AnnotatedElement[] { members[0], field };
        }
        return members;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Boolean getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(Boolean isSuper) {
        this.isSuper = isSuper;
    }

    public Boolean getSerialize() {
        if (serialize == null) {
            serialize = AnnotationUtil.getAnnotationValueFormMembers(JSONField.class, JSONField::serialize, true, getMembers());
        }
        return serialize;
    }

    public Boolean getDeserialize() {
        if (deserialize == null) {
            deserialize = AnnotationUtil.getAnnotationValueFormMembers(JSONField.class, JSONField::deserialize, true, getMembers());
        }
        return deserialize;
    }

}
