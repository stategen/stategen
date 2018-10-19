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
package org.stategen.framework.generator.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.stategen.framework.annotation.Menu;
import org.stategen.framework.annotation.Route;
import org.stategen.framework.annotation.VisitCheck;
import org.stategen.framework.lite.IMenu;
import org.stategen.framework.lite.enums.VisitCheckType;
import org.stategen.framework.lite.enums.VisitType;
import org.stategen.framework.progen.ApiMethodFilter;
import org.stategen.framework.spring.util.RequestMappingResolveResult;
import org.stategen.framework.spring.util.RequestMappingResolver;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.ReflectionUtil;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class ControllerHelpers.
 */
public class ControllerHelpers {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ControllerHelpers.class);
    public static final String CONTROLLER_SUBFIX = "Controller";

    public static String getRoute(String controllerName, boolean isPath) {
        String route = controllerName;
        route = StringUtil.trimRight(route, CONTROLLER_SUBFIX);
        route = StringUtil.uncapfirst(route);
        int size = route.length() + 8;
        StringBuffer sb = new StringBuffer(size);
        String[] subRoutes = route.split(StringUtil.UNDERLINE);
        for (int i = 0; i < subRoutes.length; i++) {
            String subRoute = subRoutes[i];
            if (StringUtil.isNotEmpty(subRoute)) {
                if (i != 0) {
                    sb.append(StringUtil.SLASH);
                }
                if (subRoute.startsWith(StringUtil.$)) {
                    String repl = isPath ? StringUtil.$ : StringUtil.COLON;
                    sb.append(repl);
                    subRoute = subRoute.substring(1);
                }
                sb.append(subRoute);
            }
        }

        route = sb.toString();
        return route;
    }

    /**
     * The Class ControllerMenuWrap.
     *
     * @param <M> the generic type
     */
    private static class ControllerMenuWrap<M> {
        M menu;
        String controllerSimpleName;
        Class<?> controllerClass;
        Class<?> breadParentClass;

        public ControllerMenuWrap(String controllerSimpleName, M menu, Class<?> controllerClass, Class<?> breadParentClass) {
            super();
            this.controllerSimpleName = controllerSimpleName;
            this.menu = menu;
            this.controllerClass = controllerClass;
            this.breadParentClass = breadParentClass;
        }

        public M getMenu() {
            return menu;
        }

        //        public Class<?> getControllerClass() {
        //            return controllerClass;
        //        }
        //
        //        public Class<?> getBreadParentClass() {
        //            return breadParentClass;
        //        }

        public String getControllerSimpleName() {
            return controllerSimpleName;
        }

        public String getControllerClassName() {
            return controllerClass.getName();
        }

        public String getBreadParentClassName() {
            if (breadParentClass != null) {
                return breadParentClass.getName();
            }
            return null;
        }
    }

    public static RequestMethodWrap getMethodUrlAndMethod(String controllerUrl, Method method, boolean defautPost) {
        RequestMappingResolveResult resolved = RequestMappingResolver.resolveOwnPath(method);
        RequestMethod requestMethod = resolved.getRequestMethod();
        if (requestMethod == null) {
            if (defautPost) {
                requestMethod = RequestMethod.POST;
            } else {
                requestMethod = RequestMethod.GET;
            }
        }

        String methodUrl = resolved.getPath();
        methodUrl = StringUtil.startWithSlash(methodUrl);
        String url = StringUtil.concatNoNull(controllerUrl, methodUrl);
        return new RequestMethodWrap(url, requestMethod);
    }

    public static String getControllerUrl(Class<?> controllerClass) {
        String controllerUrl = null;
        RequestMapping controllerRequestMappingAnno = AnnotationUtil.getAnnotationUntilSupper(controllerClass, RequestMapping.class);
        if (controllerRequestMappingAnno != null) {
            controllerUrl = RequestMappingResolver.getRequestMappingValue(controllerRequestMappingAnno);
            controllerUrl = StringUtil.startWithSlash(controllerUrl);
        }
        return controllerUrl;
    }

    /***
     * 通过 RequestMappingHandlerMapping 找到Method,再通过Method找到对应的package,再由package找到 controllerClass,再上而下地找
     * @param menuClz
     * @param requestMappingHandlerMapping
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <M extends IMenu<M>> List<M> genAllControllerMenus(Class<M> menuClz,
                                                                     RequestMappingHandlerMapping requestMappingHandlerMapping) throws InstantiationException, IllegalAccessException {

        Map<RequestMappingInfo, HandlerMethod> requestMappingMap = requestMappingHandlerMapping.getHandlerMethods();
        List<M> allMenus = new ArrayList<M>();

        Set<String> packageNames = new HashSet<String>(requestMappingMap.size());
        for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingEntry : requestMappingMap.entrySet()) {
            HandlerMethod handlerMethod = requestMappingEntry.getValue();
            Method method = handlerMethod.getMethod();
            Class<?> controllerClass = method.getDeclaringClass();
            String packageName = controllerClass.getPackage().getName();
            CollectionUtil.addIfNotExists(packageNames, packageName);
        }

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        for (String packageName : packageNames) {
            controllerClasses.addAll(ClassHelpers.getClasses(packageName));
        }

        Set<Class<?>> controllerClassSet = new HashSet<Class<?>>();
        Set<String> controllerSimpleNames = new HashSet<String>();
        for (Class<?> controllerClass : controllerClasses) {
            String controllerSimplenameName = controllerClass.getSimpleName();
            String controllerName = StringUtil.trimRight(controllerClass.getSimpleName(), CONTROLLER_SUBFIX).toLowerCase();
            if (StringUtil.isBlank(controllerName)) {
                //内部类
                continue;
            }

            if (AnnotatedElementUtils.findMergedAnnotation(controllerClass, Api.class) == null) {
                continue;
            }

            if (AnnotatedElementUtils.findMergedAnnotation(controllerClass, Controller.class) == null) {
                continue;
            }
            boolean putOk = CollectionUtil.addIfNotExists(controllerSimpleNames, controllerSimplenameName);
            AssertUtil.mustTrue(putOk);
            controllerClassSet.add(controllerClass);
        }

        List<ControllerMenuWrap<M>> controllerMenus = new ArrayList<ControllerMenuWrap<M>>();
        scanControllerMenus(controllerClassSet, menuClz, allMenus, controllerMenus);
        genControllerMenuRelations(controllerMenus);

        return allMenus;

    }

    /***
     * 转换菜单的关系，变为树
     * 
     * @param projectControllerMenuMap
     */
    private static <M extends IMenu<M>> void genControllerMenuRelations(List<ControllerMenuWrap<M>> controllerMenus) {
        Map<String, M> simpleNameMap = new LinkedCaseInsensitiveMap<M>(controllerMenus.size());
        CollectionUtil.toMap(simpleNameMap, ControllerMenuWrap::getControllerSimpleName, controllerMenus, ControllerMenuWrap::getMenu);
        Map<String, M> controllClassMap = CollectionUtil.toMap(ControllerMenuWrap::getControllerClassName, controllerMenus,
            ControllerMenuWrap::getMenu);
        Map<M, String> breadControllerClassMap = CollectionUtil.toMap(ControllerMenuWrap::getMenu, controllerMenus,
            ControllerMenuWrap::getBreadParentClassName);

        List<String> simpleControllerNames = new ArrayList<String>(simpleNameMap.keySet());

        for (String simpleControllerName : simpleControllerNames) {
            M currentMenu = simpleNameMap.get(simpleControllerName);
            M menuParent = null;
            M breadParent = null;

            int lastIdx = simpleControllerName.lastIndexOf(StringUtil.UNDERLINE);
            if (lastIdx > 0) {
                String menuParentControllerName = simpleControllerName.substring(0, lastIdx);
                menuParent = simpleNameMap.get(menuParentControllerName);
            } else {
                String breadParentControllerClass = breadControllerClassMap.get(currentMenu);
                if (breadParentControllerClass != null) {
                    breadParent = controllClassMap.get(breadParentControllerClass);
                }
            }

            if (menuParent != null) {
                currentMenu.setMenuParent(menuParent);
                currentMenu.setBreadParent(menuParent);
            } else if (breadParent != null) {
                currentMenu.setBreadParent(breadParent);
            }
        }
    }

    private static <M extends IMenu<M>> void scanControllerMenus(Set<Class<?>> controllerClassSet, Class<M> menuClz, List<M> allMenus,
                                                                 List<ControllerMenuWrap<M>> controllerMenus) throws InstantiationException, IllegalAccessException {
        Set<String> controllerApiNames = new HashSet<String>();

        for (Class<?> controllerClass : controllerClassSet) {
            Api apiAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, Api.class);
            Menu menuAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, Menu.class);
            VisitCheck controllerVisitCheckAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, VisitCheck.class);
            Route routeAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, Route.class);

            final String controllerName = StringUtil.trimRight(controllerClass.getSimpleName(), CONTROLLER_SUBFIX);

            String controllerRoute = null;
            if (routeAnno != null && routeAnno.value()) {
                controllerRoute = ControllerHelpers.getRoute(controllerName, false);
                controllerRoute = StringUtil.startWithSlash(controllerRoute);
            }
            boolean isMenu = menuAnno != null && menuAnno.value();
            VisitType controllerVisitType = VisitType.getType(isMenu);
            VisitCheckType controllerVisitCheckType = VisitCheckType.getCheckType(controllerVisitCheckAnno != null);

            String controllerApiName = apiAnno != null ? apiAnno.value() : null;
            if (StringUtil.isBlank(controllerApiName)) {
                controllerApiName = StringUtil.trimLeftTo(controllerName, StringUtil.UNDERLINE);
            }

            AssertUtil.mustNotContainsAndAdd(controllerApiNames, controllerApiName, "菜单名称不能相同：" + controllerApiName);
            M controllerMenu = IMenu.createMenu(menuClz, controllerName, null, null, controllerApiName, controllerRoute, controllerVisitType,
                controllerVisitCheckType);
            allMenus.add(controllerMenu);

            Class<?> breadParentClass = null;

            if (isMenu) {
                breadParentClass = menuAnno.breadParent();
                if (breadParentClass == null || breadParentClass == Object.class || breadParentClass == controllerClass) {
                    breadParentClass = null;
                }
            }

            controllerMenus.add(new ControllerMenuWrap<M>(controllerName, controllerMenu, controllerClass, breadParentClass));
            String controllerUrl = getControllerUrl(controllerClass);
            scanMethodMenus(allMenus, menuClz, controllerClass, controllerName, controllerMenu, controllerUrl);
        }
    }

    /***
     * controller中的method扫描
     * 
     * @param menuClz
     * @param controllerClass
     * @param controllerName
     * @param controllerMenu
     * @param controllerUrl
     */
    private static <M extends IMenu<M>> void scanMethodMenus(List<M> allMenus, Class<M> menuClz, Class<?> controllerClass,
                                                             final String controllerName, M controllerMenu, String controllerUrl) {
        ReflectionUtil.doWithMethods(controllerClass, new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                VisitCheck visitCheckAnno = AnnotatedElementUtils.findMergedAnnotation(method, VisitCheck.class);
                String methodName = method.getName();
                VisitType visitType = VisitType.API_PATH;
                VisitCheckType visitCheckType = VisitCheckType.getCheckType(visitCheckAnno != null);

                ApiOperation apiOperation = AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation.class);
                String methodApiName = apiOperation == null ? methodName : apiOperation.value();
                methodApiName = StringUtil.isNotBlank(methodApiName) ? methodApiName : methodName;

                String url = ControllerHelpers.getMethodUrlAndMethod(controllerUrl, method, true).getUrl();

                M methodMenu = IMenu.createMenu(menuClz, controllerName, methodName, url, methodApiName, null, visitType, visitCheckType);
                allMenus.add(methodMenu);
                methodMenu.setMenuParent(controllerMenu);
            }
        }, ApiMethodFilter.filter);
    }

}
