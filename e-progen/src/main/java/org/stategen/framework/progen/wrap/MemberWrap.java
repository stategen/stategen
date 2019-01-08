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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.lite.IPagination;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.StringUtil;

/**
 * The Class MemberWrap.
 */
public abstract class MemberWrap extends BaseWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MemberWrap.class);

    private ApiWrap apiWrap;

    private BaseWrap generic;

    private BaseWrap orgWrap;

    private AnnotatedElement[] members;
    
    private String genericName;
    
    private BaseWrap owner;
    
    public BaseWrap getOwner() {
        return owner;
    }
    
    public void setOwner(BaseWrap owner) {
        this.owner = owner;
    }
    
    public AnnotatedElement[] getMembers() {
        return members;
    }

    public void setMember(AnnotatedElement member) {
        members = new AnnotatedElement[] { member };
    }

    @Override
    public Boolean getIsGeneric() {
        return orgWrap.getIsGeneric() || StringUtil.isNotEmpty(genericName) || getGeneric() != null ;
    }

    public void setApiWap(ApiWrap apiWrap) {
        this.apiWrap = apiWrap;
        addBeanOrEnumWrapClazzToApi();
        addRealWrapClazzToApi();
    }

    public void setOrgWrap(BaseWrap ownClazzWrap) {
        this.orgWrap = ownClazzWrap;
        addBeanOrEnumWrapClazzToApi();
    }

    public void setGenericWrap(BaseWrap genericWrap) {
        this.generic = genericWrap;
        addRealWrapClazzToApi();
    }

    public BaseWrap getGeneric() {
        return generic;
    }

    public BaseWrap getOrgWrap() {
        return orgWrap;
    }

    private void addBeanOrEnumWrapClazzToApi() {
        if (apiWrap != null && orgWrap != null) {
            apiWrap.addImport(orgWrap);
        }
    }

    private void addRealWrapClazzToApi() {
        if (apiWrap != null && generic != null) {
            apiWrap.addImport(generic);
        }
    }

    @Override
    public Boolean getIsSimple() {
        if (generic != null) {
            return generic.getIsSimple();
        } else if (orgWrap != null) {
            return orgWrap.getIsSimple();
        } else {
            return super.getIsSimple();
        }
    }

    public Class<?> getMemberClass() {
        AnnotatedElement member =getMembers()[0];
        AssertUtil.mustNotNull(member, "没有设置 member");
        if (member instanceof Field) {
            return ((Field) member).getDeclaringClass();
        }

        if (member instanceof Method) {
            return ((Method) member).getReturnType();
        }

        if (member instanceof Parameter) {
            return ((Parameter) member).getType();
        }

        return null;
    }

    @Override
    public Boolean getIsMap() {
        return Map.class.isAssignableFrom(getMemberClass());
    }

    @Override
    public Boolean getIsArray() {
        return ClassHelpers.isArray(getMemberClass());
    }
    
    @Override
    public Boolean getIsPagination() {
        return IPagination.class.isAssignableFrom(getMemberClass());
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    @Override
    public boolean getIsEnum() {
        return super.getIsEnum() || (generic!=null && generic.getIsEnum());
    }
}
