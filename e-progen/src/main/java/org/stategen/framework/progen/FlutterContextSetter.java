package org.stategen.framework.progen;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.servlet.ModelAndView;

public class FlutterContextSetter implements ContextSetter {

    @Override
    public void setContext() {
        GenContext.registSimpleClz(List.class, "");
        GenContext.registSimpleClz(Map.class, "dynamic");
        
        GenContext.registSimpleClz(Void.class, "void");
        GenContext.registSimpleClz(String.class, "String");
        
        GenContext.registSimpleClz(Number.class, "int");
        
        GenContext.registSimpleClz(Long.class, "int");
        GenContext.registSimpleClz(long.class, "int");
        
        GenContext.registSimpleClz(Integer.class, "int");
        GenContext.registSimpleClz(int.class, "int");
        
        GenContext.registSimpleClz(Short.class, "int");
        GenContext.registSimpleClz(short.class, "int");
        
        GenContext.registSimpleClz(Byte.class, "String");
        GenContext.registSimpleClz(byte.class, "String");
        
        GenContext.registSimpleClz(Character.class, "String");
        GenContext.registSimpleClz(char.class, "String");
        
        GenContext.registSimpleClz(Float.class, "double");
        GenContext.registSimpleClz(float.class, "double");
        
        GenContext.registSimpleClz(Double.class, "double");
        GenContext.registSimpleClz(double.class, "double");
        
        GenContext.registSimpleClz(BigDecimal.class, "double");

        GenContext.registSimpleClz(boolean.class, "bool");
        GenContext.registSimpleClz(Boolean.class, "bool");
        
        GenContext.registSimpleClz(Date.class, "DateTime");
        GenContext.registSimpleClz(Object.class, "dynamic");
        
        
        GenContext.registIgnoreParamClz(HttpServletRequest.class);
        GenContext.registIgnoreParamClz(HttpServletResponse.class);
        GenContext.registIgnoreParamClz(Model.class);
        GenContext.registIgnoreParamClz(ModelAndView.class);
        
        GenContext.registIgnoreParamAnnotationClz(CookieValue.class);
    }

}
