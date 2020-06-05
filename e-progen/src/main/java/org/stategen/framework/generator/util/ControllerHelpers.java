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
import org.stategen.framework.annotation.GenRoute;
import org.stategen.framework.annotation.Menu;
import org.stategen.framework.annotation.VisitCheck;
import org.stategen.framework.lite.IMenu;
import org.stategen.framework.lite.enums.MenuType;
import org.stategen.framework.lite.enums.VisitCheckType;
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

    public static String getRoute(Class<?> controllerClass,String controllerName, boolean isPath) {
        GenRoute routeAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, GenRoute.class);
        
        if (routeAnno == null || !routeAnno.value()) {
            return null;
        }
        String route=StringUtil.trimRight(controllerName, CONTROLLER_SUBFIX);
        
        route = StringUtil.uncapfirst(route);
        int size = route.length() + 8;
        StringBuilder sb = new StringBuilder(size);
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
//        methodUrl = StringUtil.startWithSlash(methodUrl);
        String url = StringUtil.concatWithSlash(controllerUrl, methodUrl);
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
        List<M> allControllerMenus = new ArrayList<M>();

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

        scanControllerMenus(controllerClassSet, menuClz, allControllerMenus);
        allControllerMenus = genControllerMenuRelations(menuClz, allControllerMenus);

        return allControllerMenus;

    }

    /***
     * 转换菜单的关系，变为树
     * 
     * @param projectControllerMenuMap
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    private static <M extends IMenu<M>> List<M> genControllerMenuRelations(Class<M> menuClass,
                                                                           List<M> allControllerMenus) throws IllegalArgumentException, IllegalAccessException {

        Map<String, M> simpleNameMap = new LinkedCaseInsensitiveMap<M>(allControllerMenus.size());
        CollectionUtil.toMap(simpleNameMap, allControllerMenus, M::getControllerName);
        List<String> simpleControllerNames = new ArrayList<String>(simpleNameMap.keySet());

        for (String simpleControllerName : simpleControllerNames) {
            String currentMenuName = simpleControllerName;
            int lastIdx = currentMenuName.lastIndexOf(StringUtil.UNDERLINE);
            while (lastIdx > 0) {
                M currentMenu = simpleNameMap.get(currentMenuName);

                String menuParentControllerName = currentMenuName.substring(0, lastIdx);
                M menuParent = simpleNameMap.get(menuParentControllerName);

                if (menuParent == null) {
                    String menuName = StringUtil.trimLeftFormRightTo(menuParentControllerName, StringUtil.UNDERLINE);
                    MenuType menuType = getMenuTypeByControllerName(menuParentControllerName, true);
                    menuParent = IMenu.createMenu(menuClass, menuParentControllerName, null, null, menuName, null, menuType, VisitCheckType.NONE);
                    simpleNameMap.put(menuParentControllerName, menuParent);
                }
                menuParent.addChild(currentMenu);

                currentMenuName = menuParentControllerName;
                lastIdx = currentMenuName.lastIndexOf(StringUtil.UNDERLINE);
            }
        }

        ArrayList<M> result = new ArrayList<M>();
        for (M m : allControllerMenus) {
            if (m.getParent() == null) {
                result.add(m);
            }
        }
        return result;
    }

    private static MenuType getMenuTypeByControllerName(String controllerName, boolean isMenu) {
        if (!isMenu) {
            return MenuType.NONE;
        }
        return controllerName.contains(StringUtil.$) ? MenuType.DYNAMIC : MenuType.MENU;
    }

    private static <M extends IMenu<M>> void scanControllerMenus(Set<Class<?>> controllerClassSet, Class<M> menuClz,
                                                                 List<M> allControllerMenus) throws InstantiationException, IllegalAccessException {
        Set<String> controllerApiNames = new HashSet<String>();

        for (Class<?> controllerClass : controllerClassSet) {
            Api apiAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, Api.class);
            Menu menuAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, Menu.class);
            VisitCheck controllerVisitCheckAnno = AnnotatedElementUtils.findMergedAnnotation(controllerClass, VisitCheck.class);
            final String controllerName = StringUtil.trimRight(controllerClass.getSimpleName(), CONTROLLER_SUBFIX);
            
            String route = ControllerHelpers.getRoute(controllerClass,controllerName, false);
            route = StringUtil.startWithSlash(route);
            
            boolean isMenu = menuAnno != null && menuAnno.value();
            MenuType controllerVisitType = getMenuTypeByControllerName(controllerName, isMenu);
            VisitCheckType controllerVisitCheckType = VisitCheckType.getCheckType(controllerVisitCheckAnno != null);

            String apiName = apiAnno != null ? apiAnno.value() : null;
            if (StringUtil.isBlank(apiName)) {
                apiName = StringUtil.trimLeftFormRightTo(controllerName, StringUtil.UNDERLINE);
            }

            AssertUtil.mustNotContainsAndAdd(controllerApiNames, apiName, "菜单名称不能相同：" + apiName);
            M controllerMenu = IMenu.createMenu(menuClz, controllerName, null, null, apiName, route, controllerVisitType, controllerVisitCheckType);
            allControllerMenus.add(controllerMenu);

            String controllerUrl = getControllerUrl(controllerClass);
            scanMethodMenus(menuClz, controllerClass, controllerName, controllerMenu, controllerUrl);
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
    private static <M extends IMenu<M>> void scanMethodMenus(Class<M> menuClz, Class<?> controllerClass, final String controllerName,
                                                             M controllerMenu, String controllerUrl) {
        ReflectionUtil.doWithMethods(controllerClass, new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                VisitCheck visitCheckAnno = AnnotatedElementUtils.findMergedAnnotation(method, VisitCheck.class);
                String methodName = method.getName();
                MenuType menuType = MenuType.API_PATH;
                VisitCheckType visitCheckType = VisitCheckType.getCheckType(visitCheckAnno != null);

                ApiOperation apiOperation = AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation.class);
                String methodApiName = apiOperation == null ? methodName : apiOperation.value();
                methodApiName = StringUtil.isNotBlank(methodApiName) ? methodApiName : methodName;

                String url = ControllerHelpers.getMethodUrlAndMethod(controllerUrl, method, true).getUrl();

                M methodMenu = IMenu.createMenu(menuClz, controllerName, methodName, url, methodApiName, null, menuType, visitCheckType);
                controllerMenu.addChild(methodMenu);
            }
        }, ApiMethodFilter.filter);
    }

}
