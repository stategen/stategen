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

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.stategen.framework.progen.NamedContext;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.ApiParam;

/**
 * The Class ParamWrap.
 */
public class ParamWrap extends NamedWrap {
    public ParamWrap(NamedContext context) {
        super(context);
    }

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ParamWrap.class);

    private String orgName;

    private String description;

    private Boolean required;
    

    private AnnotatedElement[] members;
    
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
    }

    @Override
    public AnnotatedElement[] getMembers() {
        if (members == null) {
            members = super.getMembers();
        }
        return members;
    }

    public void addMembers(AnnotatedElement... members) {
        AnnotatedElement[] oldMembers = getMembers();
        AnnotatedElement[] newMembers = new AnnotatedElement[oldMembers.length + members.length];
        System.arraycopy(oldMembers, 0, newMembers, 0, oldMembers.length);
        System.arraycopy(members, 0, newMembers, oldMembers.length, members.length);
        this.members = newMembers;
    }

    public Boolean getRequired() {
        if (required == null) {
            required = false;
            if (getIsPath()) {
                required = true;
            }

            if (!required) {
                if (AnnotationUtil.getAnnotationFormMembers(RequestBody.class, getMembers()) != null) {
                    required = true;
                }
            }

            if (!required) {
                required = AnnotationUtil.getAnnotationValueFormMembers(RequestParam.class, RequestParam::required,false, getMembers());
            }

            if (!required) {
                required = AnnotationUtil.getAnnotationValueFormMembers(ApiParam.class, ApiParam::required,false, getMembers());
            }
        }
        return required;
    }

    public Boolean getIsPath() {
        if (AnnotationUtil.getAnnotationFormMembers(PathVariable.class, getMembers()) != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        if (description == null) {
            description = AnnotationUtil.getAnnotationValueFormMembers(ApiParam.class, ApiParam::value,null, members);
            if (StringUtil.isBlank(description)) {
                description = super.getDescription();
            }
        }

        return description;
    }

}