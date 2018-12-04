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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.stategen.framework.generator.util.ControllerHelpers;
import org.stategen.framework.progen.ApiMethodFilter;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.ReflectionUtil;
import org.stategen.framework.util.StringComparetor;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.Api;

/**
 * The Class ApiWrap.
 */
public class ApiWrap extends BaseHasImportsWrap implements CanbeImportWrap {

    String className;
    Map<String, MethodWrap> functions = new TreeMap<String, MethodWrap>(new StringComparetor());

    String description;

    private Boolean isApi = null;
    private Boolean hidden = null;

    private String route = null;

    private String controllerUrl = null;

    private List<MethodWrap> inits = null;
    private List<MethodWrap> effectInits = null;

    private Map<String, BaseWrap> areas = new TreeMap<String, BaseWrap>(new StringComparetor());

    public ApiWrap(Class<?> controllerClz, String className) {
        super();
        setClazz(controllerClz);
        this.className = className;
        scanMethods();
    }

    private Api getApiAnno() {
        return AnnotatedElementUtils.findMergedAnnotation(getClazz(), Api.class);
    }

    public Boolean getIsApi() {
        if (isApi == null) {
            isApi = getApiAnno() != null;
        }

        return isApi;
    }

    public Boolean getHidden() {
        if (hidden == null) {
            Api apiAnno = getApiAnno();
            hidden = apiAnno == null || apiAnno.hidden();
        }

        return hidden;
    }

    public List<BaseWrap> getAreas() {
        return CollectionUtil.newArrayList(areas.values());
    }

    public void addArea(BaseWrap area) {
        Class<?> clazz = area.getClazz();
        String className=clazz.getName();
        if (!areas.containsKey(className)) {
            areas.put(className, area);
        }
    }

    protected void scanMethods() {

        final ApiWrap _this = this;
        ReflectionUtil.doWithMethods(getClazz(), new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                MethodWrap methodWrap = new MethodWrap(_this, method);
                String methodName = method.getName();
                AssertUtil.mustFalse(functions.containsKey(methodName), "代码生成终止：" + getClazz() + " 中超过1个相同的api:" + methodName);
                functions.put(methodName, methodWrap);
            }
        }, ApiMethodFilter.filter);
    }

    public List<MethodWrap> getFunctions() {
        return new ArrayList<MethodWrap>(functions.values());
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameLower() {
        return new StringBuffer(className.length()).append(className.substring(0, 1).toLowerCase()).append(className.substring(1)).toString();
    }

    public String getControllerUrl() {
        if (controllerUrl == null) {
            controllerUrl = ControllerHelpers.getControllerUrl(getClazz());
        }
        return controllerUrl;
    }

    public String getDescription() {
        Api apiAnno = AnnotationUtil.getAnnotation(getClazz(), Api.class);
        if (apiAnno != null) {
            String value = apiAnno.value();
            if (StringUtil.isNotEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    public String getRoute() {
        if (route == null) {
            route = ControllerHelpers.getRoute(getClassName(), true);
        }
        return route;
    }

    public List<MethodWrap> getInits() {
        if (inits == null) {
            inits = new ArrayList<MethodWrap>();
            for (MethodWrap methodWrap : this.functions.values()) {
                if (methodWrap.getState().getInit()) {
                    inits.add(methodWrap);
                }
            }
        }
        return inits;
    }

    public List<MethodWrap> getEffectInits() {
        if (this.effectInits == null) {
            List<MethodWrap> inits = getInits();
            effectInits = new ArrayList<MethodWrap>(inits.size());
            for (MethodWrap methodWrap : inits) {
                if (methodWrap.getState().getGenEffect()) {
                    effectInits.add(methodWrap);
                }
            }
        }
        return effectInits;
    }

    @Override
    public String toString() {
        String name = getClassName();
        //name = name.replace("_$", "$");
        return name;
    }

}