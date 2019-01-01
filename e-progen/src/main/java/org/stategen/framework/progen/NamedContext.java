package org.stategen.framework.progen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.stategen.framework.progen.wrap.ApiWrap;

public class NamedContext {
    
    private Map<String, Parameter> fieldNameParameterMap ;
    private Map<String, Field> fieldNameFieldMap ;
    private Map<String, Method> getterNameMethods;
    
    
    public NamedContext(Map<String, Parameter> fieldNameParameterMap, Map<String, Field> fieldNameFieldMap, Map<String, Method> getterNameMethods) {
        super();
        this.fieldNameParameterMap = fieldNameParameterMap;
        this.fieldNameFieldMap = fieldNameFieldMap;
        this.getterNameMethods = getterNameMethods;
    }

    public ApiWrap getAppWrap() {
        return GenContext.appWrap;
    }

    public Map<String, Parameter> getFieldNameParameterMap() {
        return fieldNameParameterMap;
    }


    public Map<String, Field> getFieldNameFieldMap() {
        return fieldNameFieldMap;
    }


    public Map<String, Method> getGetterNameMethods() {
        return getterNameMethods;
    }


}
