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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.stategen.framework.annotation.AreaExtraProp;
import org.stategen.framework.annotation.ExcludeBeanRule;
import org.stategen.framework.annotation.GenEffect;
import org.stategen.framework.annotation.GenForm;
import org.stategen.framework.annotation.GenReducer;
import org.stategen.framework.annotation.GenRefresh;
import org.stategen.framework.annotation.State;
import org.stategen.framework.annotation.StateExtraProp;
import org.stategen.framework.enums.DataOpt;
import org.stategen.framework.generator.util.ControllerHelpers;
import org.stategen.framework.generator.util.RequestMethodWrap;
import org.stategen.framework.lite.SimpleResponse;
import org.stategen.framework.progen.GenContext;
import org.stategen.framework.progen.UrlPart;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.ReflectionUtil;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * The Class MethodWrap.
 */
public class MethodWrap {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MethodWrap.class);
    private Method methodFun;
    private ParamWrap json = null;
    private List<ParamWrap> params = new ArrayList<ParamWrap>();

    private ReturnWrap returnWrap = null;
    private String url = null;
    private String description;
    private ApiWrap apiWrap = null;
    private List<UrlPart> urlParts = null;
    private RequestMethod method;

    private StateWrap state = null;

    private BaseWrap area;
    private Boolean genForm = null;

    public MethodWrap(ApiWrap apiWrap, Method method) {
        super();
        this.apiWrap = apiWrap;
        this.methodFun = method;
        genReturn();
        genParams();
        genUrlAndHttpMethod();
        genState();
    }

    public Boolean getGenForm() {
        if (genForm == null) {
            genForm = AnnotationUtil.getAnnotationValueFormMembers(GenForm.class, GenForm::value, false, methodFun);
        }
        return genForm;
    }

    protected void genReturn() {
        Class<?> rawReturnClass = methodFun.getReturnType();
        Type genericReturnType = methodFun.getGenericReturnType();
        returnWrap = GenContext.wrapContainer.genMemberWrap(apiWrap, rawReturnClass, genericReturnType, ReturnWrap.class, methodFun);
        returnWrap.setMember(methodFun);
    }

    private void genState() {
        Class<?> returnClz = returnWrap.getIsGeneric() ? returnWrap.getGeneric().getClazz() : returnWrap.getClazz();
        boolean stateFieldAdded = false;

        DataOpt dataOpt = DataOpt.APPEND_OR_UPDATE;
        Set<String> areaExtraProps = null;
        Set<String> stateExtraProps = null;
        Boolean init = false;
        Boolean isSetted = false;
        Boolean genEffect = true;
        Boolean genReducer = true;
        Boolean initCheck = true;
        BaseWrap areaTemp = null;
        Boolean genRefresh = false;

        StateWrap stateWrap = new StateWrap();
        this.setState(stateWrap);
        Set<AreaExtraProp> areaExtraPropAnnos = AnnotatedElementUtils.getMergedRepeatableAnnotations(methodFun, AreaExtraProp.class);
        areaExtraProps = CollectionUtil.toSet(areaExtraPropAnnos, AreaExtraProp::value);

        Set<StateExtraProp> stateExtraPropAnnos = AnnotatedElementUtils.getMergedRepeatableAnnotations(methodFun, StateExtraProp.class);
        stateExtraProps = CollectionUtil.toSet(stateExtraPropAnnos, StateExtraProp::value);

        genEffect = AnnotationUtil.getAnnotationValueFormMembers(GenEffect.class, GenEffect::value, false, methodFun);
        genReducer = AnnotationUtil.getAnnotationValueFormMembers(GenReducer.class, GenReducer::value, false, methodFun);
        genRefresh = AnnotationUtil.getAnnotationValueFormMembers(GenRefresh.class, GenRefresh::value, false, methodFun);

        State stateAnno = AnnotatedElementUtils.getMergedAnnotation(methodFun, State.class);
        if (stateAnno != null) {
            init = stateAnno.init();
            dataOpt = stateAnno.dataOpt();
            isSetted = true;
            initCheck = stateAnno.initCheck();
            Class<?> stateAreaClass = stateAnno.area();
            if (stateAreaClass != Object.class) {
                if (stateAreaClass != returnClz) {
                    areaTemp = GenContext.wrapContainer.add(stateAreaClass, false);
                    stateFieldAdded = true;
                }
            }
        }

        if (!stateFieldAdded && area == null ) {
            if (returnClz!=Object.class && GenContext.wrapContainer.checkIsOrgSimpleOrEnum(returnClz) && ((ApiWrap)apiWrap).getGenModel()) {
                String classPath =this.methodFun.getDeclaringClass().getPackage().getName()+'.'+this.methodFun.getDeclaringClass().getSimpleName()+'.'+this.methodFun.getName();
                String errMessage=String.format("未指定area,如 @State(area=User.class),只有返回值是非基本类型、SimpleResponse、Object可以不指定area\n at "+classPath+"("+ this.methodFun.getDeclaringClass().getSimpleName()+".java:%s)", 0);
                AssertUtil.throwException(errMessage);
            }
            areaTemp = GenContext.wrapContainer.add(returnClz, false);
        }

        if (areaTemp != null && !SimpleResponse.class.isAssignableFrom(areaTemp.getClazz())) {
            apiWrap.addArea(areaTemp);
            this.area = areaTemp;
        }

        if (areaTemp == null) {

        }

        stateWrap.setInit(init);
        stateWrap.setAreaExtraProps(areaExtraProps);
        stateWrap.setStateExtraProps(stateExtraProps);
        stateWrap.setDataOpt(dataOpt);
        stateWrap.setIsSetted(isSetted);
        stateWrap.setGenEffect(genEffect);
        stateWrap.setInitCheck(initCheck);
        stateWrap.setGenRefresh(genRefresh);
        stateWrap.setGenReducer(genReducer);
    }

    public BaseWrap getArea() {
        return area;
    }

    protected void genParams() {

        Set<Class<? extends Annotation>> methodExcludeBeanRules = findExcludeAnnoClasses(this.methodFun);

        Parameter[] parameters = methodFun.getParameters();
        DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = defaultParameterNameDiscoverer.getParameterNames(methodFun);
        Set<Class<?>> noneOrigParameters = new HashSet<Class<?>>();

        List<ParamWrap> orgParamWraps = new ArrayList<ParamWrap>();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (GenContext.checkIgnore(parameter)) {
                continue;
            }
            Class<?> paramRawType = parameter.getType();

            ApiParam apiParamAnno = AnnotatedElementUtils.getMergedAnnotation(parameter, ApiParam.class);
            if (apiParamAnno != null && apiParamAnno.hidden()) {
                noneOrigParameters.add(paramRawType);
                continue;
            }

            String paramName = AnnotationUtil.getAnnotationValueFormMembers(PathVariable.class, PathVariable::value, "", parameter);

            if (StringUtil.isBlank(paramName)) {
                paramName = AnnotationUtil.getAnnotationValueFormMembers(RequestParam.class, RequestParam::value, "", parameter);
                if (StringUtil.isNotBlank(paramName)) {
                    paramName = StringUtil.trimRight(paramName, "[]");
                }
            }

            String orgName = parameterNames[i];
            if (StringUtil.isBlank(paramName)) {
                paramName = orgName;
            }

            Boolean isJson = AnnotationUtil.getAnnotation(parameter, RequestBody.class) != null;
            if (isJson) {
                AssertUtil.mustNull(this.json, new StringBuffer().append(methodFun).append(" 方法中有不只1个RequestBody,不能同时处理").toString());
            }

            Type parameterizedType = parameter.getParameterizedType();

            ParamWrap paramWrap = GenContext.wrapContainer.genMemberWrap(apiWrap, paramRawType, parameterizedType, ParamWrap.class, parameter);

            paramWrap.setName(paramName);
            paramWrap.setMember(parameter);
            paramWrap.setOrgName(orgName);
            params.add(paramWrap);

            Set<Class<? extends Annotation>> parameterExcludeBeanRules = findExcludeAnnoClasses(parameter);
            parameterExcludeBeanRules.addAll(methodExcludeBeanRules);
            paramWrap.set_excludeAnnos(parameterExcludeBeanRules);

            if (isJson) {
                this.json = paramWrap;
            }

            boolean isOrgOrEnum = GenContext.wrapContainer.checkIsOrgSimpleOrEnum(paramRawType);
            if (isOrgOrEnum) {
                orgParamWraps.add(paramWrap);
            } else {
                noneOrigParameters.add(paramRawType);
            }

        }

        Map<String, Method> getterMethodMap = new HashMap<String, Method>();
        Map<String, Field> fieldMaps = new HashMap<String, Field>();

        //TODO set方法是查找??
        for (Class<?> noneOrgClz : noneOrigParameters) {
            Map<String, Field> fieldNameFieldMap = ReflectionUtil.getFieldNameFieldMap(noneOrgClz);
            Map<String, Method> getterNameMethods = ReflectionUtil.getGetterNameMethods(noneOrgClz);

            getterMethodMap.putAll(getterNameMethods);
            fieldMaps.putAll(fieldNameFieldMap);
        }

        for (ParamWrap paramWrap : orgParamWraps) {
            String orgName = paramWrap.getOrgName();
            Method getterMethod = getterMethodMap.get(orgName);
            Field field = fieldMaps.get(orgName);
            paramWrap.addMembers(getterMethod, field);
        }

    }

    private Set<Class<? extends Annotation>> findExcludeAnnoClasses(AnnotatedElement member) {
        Set<ExcludeBeanRule> memberExcludeBeanRuleAnnos = AnnotatedElementUtils.findMergedRepeatableAnnotations(member, ExcludeBeanRule.class);
        Set<Class<? extends Annotation>> result = new HashSet<Class<? extends Annotation>>(5);
        for (ExcludeBeanRule excludeBeanRule : memberExcludeBeanRuleAnnos) {
            Class<? extends Annotation>[] annoClasses = excludeBeanRule.value();
            for (Class<? extends Annotation> annoClass : annoClasses) {
                result.add(annoClass);
            }
        }
        return result;
    }

    public ParamWrap getJson() {
        return json;
    }

    public RequestMethod getMethod() {
        return method;
    }

    private void genUrlAndHttpMethod() {
        if (url == null) {
            String controllerUrl = this.apiWrap.getControllerUrl();
            controllerUrl = StringUtil.startWithSlash(controllerUrl);
            RequestMethodWrap requestMethodWrap = ControllerHelpers.getMethodUrlAndMethod(controllerUrl, methodFun,
                CollectionUtil.isNotEmpty(getParams()));
            this.url = requestMethodWrap.getUrl();
            this.method = requestMethodWrap.getRequestMethod();
        }
    }

    public String getUrl() {
        return url;
    }

    public List<UrlPart> getUrlParts() {
        if (urlParts == null) {
            urlParts = new ArrayList<UrlPart>();
            Pattern pattern = Pattern.compile("\\{(.+?)\\}");
            String path = getUrl();
            Matcher matcher = pattern.matcher(path);
            int last = 0;
            while (matcher.find()) {
                UrlPart urlPart = new UrlPart(path.substring(last, matcher.start()), false);
                urlParts.add(urlPart);
                last = matcher.end();
                String find = matcher.group();
                urlPart = new UrlPart(find.substring(1, find.length() - 1), true);
                urlParts.add(urlPart);
            }

            if (last + 1 < path.length() - 1) {
                UrlPart urlPart = new UrlPart(path.substring(last), false);
                urlParts.add(urlPart);
            }
        }
        return urlParts;
    }

    public ReturnWrap getReturn() {
        return returnWrap;
    }

    public List<ParamWrap> getParams() {
        return params;
    }

    public String getDescription() {
        if (description == null) {
            description = AnnotationUtil.getAnnotationValueFormMembers(ApiOperation.class, ApiOperation::value, "", methodFun);
        }
        return description;
    }

    @Override
    public String toString() {
        return methodFun.getName();
    }

    public StateWrap getState() {
        return state;
    }

    public void setState(StateWrap state) {
        this.state = state;
    }

}