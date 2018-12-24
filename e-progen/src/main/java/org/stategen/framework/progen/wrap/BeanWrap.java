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
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.annotation.GenForm;
import org.stategen.framework.progen.GenContext;
import org.stategen.framework.progen.GenericTypeResolver;
import org.stategen.framework.progen.WrapContainer;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.ReflectionUtil;
import org.stategen.framework.util.StringUtil;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * The Class BeanWrap.
 */
public class BeanWrap extends BaseHasImportsWrap implements CanbeImportWrap {
    final static Logger logger = LoggerFactory.getLogger(BeanWrap.class);

    private BeanWrap parentBean;

    private String idKeyName;

    private Map<String, FieldWrap> genericFieldMap;

    private Map<String, FieldWrap> fieldMap = new LinkedHashMap<String, FieldWrap>();
    private Map<String, FieldWrap> allFieldMap = new LinkedHashMap<String, FieldWrap>();

    private Boolean genForm;

    @Override
    public String getImportPath() {
        return CanbeImportWrap.super.getImportPath();
    }

    @Override
    public Boolean getIsGeneric() {
        return CollectionUtil.isNotEmpty(genericFieldMap);
    }

    private static boolean canSerialize(AnnotatedElement annotatedElement) {
        int modifiers = ((Member) annotatedElement).getModifiers();
        if (!Modifier.isTransient(modifiers)) {
            JSONField jsonFieldAnno = AnnotationUtil.getAnnotation(annotatedElement, JSONField.class);
            if (jsonFieldAnno != null) {
                return jsonFieldAnno.serialize();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setClazz(Class<?> clz) {
        super.setClazz(clz);
        this.scanAndAddFieldsClz(GenContext.wrapContainer);
    }

    private void scanAndAddFieldsClz(WrapContainer wrapContainer) {
        Class<?> currentType = getClazz();
        Map<String, Field> fieldNameFieldMap = ReflectionUtil.getFieldNameFieldMap(currentType);
        Map<String, Method> getterNameMethods = ReflectionUtil.getGetterNameMethods(currentType);
        Map<String, Method> getterNameMethodsSorted = new LinkedHashMap<String, Method>(getterNameMethods.size());

        //先按 field顺序排，再按getter方法排
        for (String fieldName : fieldNameFieldMap.keySet()) {
            Method method = getterNameMethods.remove(fieldName);
            if (method != null) {
                getterNameMethodsSorted.put(fieldName, method);
            }
        }

        getterNameMethodsSorted.putAll(getterNameMethods);

        for (Entry<String, Method> entry : getterNameMethodsSorted.entrySet()) {

            Method getterMethod = entry.getValue();
            if (!canSerialize(getterMethod)) {
                continue;
            }

            String fieldName = entry.getKey();
            Field field = fieldNameFieldMap.get(fieldName);
            if (field != null && !canSerialize(field)) {
                continue;
            }

            Class<?> returnType = getterMethod.getReturnType();
            Type genericReturnType = getterMethod.getGenericReturnType();

            FieldWrap fieldWrap = GenContext.wrapContainer.genMemberWrap(null, returnType, genericReturnType, FieldWrap.class, getterMethod);

            fieldWrap.setMember(getterMethod);
            fieldWrap.setField(field);
            fieldWrap.setName(fieldName);

            addFieldWrap(fieldName, fieldWrap);
            String genericName = GenericTypeResolver.getGenericName(genericReturnType, 0);
            if (StringUtil.isNotEmpty(genericName)) {
                fieldWrap.setGenericName(genericName);
                this.addGenericFieldWrap(genericName, fieldWrap);
            }
            if (fieldWrap.getIsId()) {
                this.idKeyName = fieldName;
            }
        }
    }

    public List<FieldWrap> getGenericFields() {
        if (CollectionUtil.isNotEmpty(genericFieldMap)) {
            return new ArrayList<FieldWrap>(genericFieldMap.values());
        }
        return null;
    }

    private void addGenericFieldWrap(String genericName, FieldWrap fieldWrap) {
        if (this.genericFieldMap == null) {
            this.genericFieldMap = new LinkedHashMap<String, FieldWrap>();
        }
        genericFieldMap.put(genericName, fieldWrap);
    }

    public Map<String, FieldWrap> getFieldWrapMap() {
        return fieldMap;
    }

    public void addFieldWrap(String fieldName, FieldWrap fieldWrap) {
        this.fieldMap.put(fieldName, fieldWrap);
        this.allFieldMap.put(fieldName, fieldWrap);
    }

    public List<FieldWrap> getFields() {
        return new ArrayList<FieldWrap>(fieldMap.values());
    }

    public List<FieldWrap> getAllFields() {
        return new ArrayList<FieldWrap>(allFieldMap.values());
    }

    public void setParentBean(BeanWrap parentBean) {
        this.parentBean = parentBean;
        if (parentBean != null) {
            addImport(parentBean);

            //把与父类相同的fieldName去掉
            Map<String, FieldWrap> parentFieldWrapMap = parentBean.getFieldWrapMap();
            Set<String> parentFieldNams = parentFieldWrapMap.keySet();
            for (String parentFieldName : parentFieldNams) {
                fieldMap.remove(parentFieldName);
            }
        }
    }

    public BeanWrap getParentBean() {
        return parentBean;
    }

    public Boolean getExtend() {
        return parentBean != null;
    }

    public String getIdKeyName() {
        return idKeyName;
    }

    @Override
    public Boolean getGenForm() {
        if (genForm == null) {
            genForm =AnnotationUtil.getAnnotationValueFormMembers(GenForm.class, GenForm::value,false, getClazz());
        }
        return genForm;
    }

}
