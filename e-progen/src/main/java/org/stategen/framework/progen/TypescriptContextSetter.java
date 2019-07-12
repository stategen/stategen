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

public class TypescriptContextSetter implements ContextSetter {

    @Override
    public void setContext() {
        GenContext.registSimpleClz(List.class, "");
        GenContext.registSimpleClz(Map.class, "any");
        
        GenContext.registSimpleClz(Void.class, "void");
        GenContext.registSimpleClz(String.class, "string");
        
        GenContext.registSimpleClz(Number.class, "number");
        
        GenContext.registSimpleClz(Long.class, "number");
        GenContext.registSimpleClz(long.class, "number");
        
        GenContext.registSimpleClz(Integer.class, "number");
        GenContext.registSimpleClz(int.class, "number");
        
        GenContext.registSimpleClz(Short.class, "number");
        GenContext.registSimpleClz(short.class, "number");
        
        GenContext.registSimpleClz(Byte.class, "String");
        GenContext.registSimpleClz(byte.class, "String");
        
        GenContext.registSimpleClz(Character.class, "String");
        GenContext.registSimpleClz(char.class, "String");
        
        GenContext.registSimpleClz(Float.class, "number");
        GenContext.registSimpleClz(float.class, "number");
        
        GenContext.registSimpleClz(Double.class, "number");
        GenContext.registSimpleClz(double.class, "number");
        
        GenContext.registSimpleClz(BigDecimal.class, "number");

        GenContext.registSimpleClz(boolean.class, "boolean");
        GenContext.registSimpleClz(Boolean.class, "boolean");
        
        GenContext.registSimpleClz(Date.class, "Date");
        GenContext.registSimpleClz(Object.class, "any");
        
        
        GenContext.registIgnoreParamClz(HttpServletRequest.class);
        GenContext.registIgnoreParamClz(HttpServletResponse.class);
        GenContext.registIgnoreParamClz(Model.class);
        GenContext.registIgnoreParamClz(ModelAndView.class);
        
        GenContext.registIgnoreParamAnnotationClz(CookieValue.class);
    }

}
