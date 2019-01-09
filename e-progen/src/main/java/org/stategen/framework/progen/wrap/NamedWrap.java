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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.BeanUtils;
import org.stategen.framework.annotation.ChangeBy;
import org.stategen.framework.annotation.Editor;
import org.stategen.framework.annotation.OptionConvertor;
import org.stategen.framework.annotation.ReferConfig;
import org.stategen.framework.generator.util.ClassHelpers;
import org.stategen.framework.lite.enums.EditorType;
import org.stategen.framework.progen.FieldRule;
import org.stategen.framework.progen.GenContext;
import org.stategen.framework.progen.GenericTypeResolver;
import org.stategen.framework.progen.NamedContext;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.ReflectionUtil;
import org.stategen.framework.util.StringUtil;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class NamedWrap.
 */
public abstract class NamedWrap extends MemberWrap {

    private String name;

    private List<FieldRule> rules;

    private Boolean hidden;

    private String title;

    private Boolean isId;

    private String temporalType;

    private Boolean isImage;

    private boolean _hasGentemporalType = false;

    private String editorType;

    private ReferConfigWrap referConfig;

    private Boolean isSimple;

    private String changeBy;

    private String props;

    private String falseTitle;

    private NamedContext context;

    private Set<Class<? extends Annotation>> _excludeAnnos;

    private boolean asserted = false;
    
    private NamedWrap field;
    
    private Boolean noJson;
    
    public void setField(NamedWrap field) {
        this.field = field;
    }
    
    public NamedWrap getField() {
        return field;
    }

    public NamedWrap(NamedContext context) {
        this.context = context;
    }

    public NamedContext getContext() {
        return context;
    }

    public List<FieldRule> getRules() {
        if (rules == null) {
            Set<Class<? extends Annotation>> excludeAnnos = _excludeAnnos;
            if (excludeAnnos == null) {
                excludeAnnos = new HashSet<Class<? extends Annotation>>(0);
            }
            rules = FieldRule.checkRules(excludeAnnos, getMembers());
        }
        return rules;
    }

    public void set_excludeAnnos(Set<Class<? extends Annotation>> _excludeAnnos) {
        this._excludeAnnos = _excludeAnnos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Boolean getIsImage() {
        if (isImage == null) {
            Class<? extends EditorType> editorType = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::value, null, getMembers());
            if (editorType != null) {
                isImage = editorType.getSimpleName().equals(EditorType.Image.class.getSimpleName());
            } else {
                isImage = false;
            }
        }
        return isImage;
    }

    public String getTitle() {
        if (title == null) {
            title = getDescription();
            title = StringUtil.getSimpleTitle(title);
        }
        return title;
    }

    public Boolean getIsId() {
        if (isId == null) {
            isId = AnnotationUtil.getAnnotationFormMembers(Id.class, getMembers()) != null;
        }
        return isId;
    }

    public String getEditorType() {
        if (editorType == null) {
            Class<? extends EditorType> editorTypeClz = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::value, null, getMembers());

            if (editorTypeClz != null) {
                editorType = editorTypeClz.getSimpleName();
                assertReferConfig(editorTypeClz);
            } else {
                editorType = "";
            }
        }
        return editorType;
    }

    private AccessibleObject getFirstAccessibleObject() {
        AnnotatedElement member = this.getMembers()[0];
        if (member instanceof Parameter) {
            Parameter parameter = (Parameter) member;
            return parameter.getDeclaringExecutable();
        } else if (member instanceof Field) {
            Field field = (Field) member;
            return field;
        } else {
            return (Method) member;
        }
    }

    private void assertReferConfig(Class<? extends EditorType> editorTypeClz) {
        if (!asserted) {
            if (!Modifier.isAbstract(editorTypeClz.getModifiers())) {
                EditorType ins = BeanUtils.instantiate(editorTypeClz);
                if (ins.needReferConfig()) {
                    ReferConfigWrap referConfig = this.getReferConfig();
                    AccessibleObject accessibleObject = getFirstAccessibleObject();

                    String javaConsoleLink = ReflectionUtil.getJavaConsoleLink(accessibleObject);
                    if (referConfig == null) {
                        AssertUtil.throwException("参数:" + getName() + " 在引用的类上必须指定@ReferConfig" + javaConsoleLink);
                    }

                    String referField = referConfig.getReferField();
                    Field field = context.getFieldNameFieldMap().get(referField);
                    Method method = context.getGetterNameMethods().get(referField);
                    if (!getIsEnum() && !getIsArray() && field == null && method == null) {
                        AssertUtil.throwException("参数:" + getName() + " @ReferConfig 中获得不了referField:" + referField + " 字段" + javaConsoleLink);
                    }

                    if (!getIsEnum()) {
                        OptionConvertor optionConvertorAnno = AnnotationUtil.getAnnotationFormMembers(OptionConvertor.class, getMembers());
                        Class<?> beanClz = null;
                        if (method != null) {
                            beanClz = ClassHelpers.getClazzIfCollection(method.getReturnType());
                            if (ClassHelpers.isArrayOrMap(beanClz)) {
                                Type genericReturnType = method.getGenericReturnType();
                                beanClz = GenericTypeResolver.getClass(genericReturnType, 0);
                                beanClz = ClassHelpers.getClazzIfCollection(beanClz);
                            }
                            if (optionConvertorAnno == null && !ClassHelpers.isArrayOrMap(beanClz)) {
                                optionConvertorAnno = AnnotationUtil.getAnnotationFormMembers(OptionConvertor.class, beanClz);
                            }
                        }

                        if (optionConvertorAnno != null) {
                            String value = optionConvertorAnno.value();
                            String title = optionConvertorAnno.title();
                            String parentId = optionConvertorAnno.parentId();
                            String url = optionConvertorAnno.url();
                            String label = optionConvertorAnno.label();

                            if (beanClz != null) {
                                BaseWrap baseWrap = GenContext.wrapContainer.get(beanClz);
                                if (baseWrap instanceof BeanWrap) {
                                    BeanWrap beanWrap = (BeanWrap) baseWrap;
                                    String message = "在类 " + beanClz.getSimpleName() + "设置的 @OptionConvertor {0}:{1}不存在"
                                                     + ReflectionUtil.getJavaConsoleLink(beanClz.getConstructors()[0]);

                                    if (StringUtil.isNotBlank(value)) {
                                        AssertUtil.mustNotNull(beanWrap.get(value), String.format(message, "value", value));
                                    }
                                    if (StringUtil.isNotBlank(title)) {
                                        AssertUtil.mustNotNull(beanWrap.get(title), String.format(message, "title", title));
                                    }
                                    if (StringUtil.isNotBlank(parentId)) {
                                        AssertUtil.mustNotNull(beanWrap.get(parentId), "在类 " + String.format(message, "parentId", parentId));
                                    }
                                    if (StringUtil.isNotBlank(url)) {
                                        AssertUtil.mustNotNull(beanWrap.get(url), "在类 " + String.format(message, "url", url));
                                    }
                                    if (StringUtil.isNotBlank(label)) {
                                        AssertUtil.mustNotNull(beanWrap.get(label), "在类 " + String.format(message, "label", label));
                                    }
                                }
                            }

                            OptionConvertorWrap optionConvertor = new OptionConvertorWrap(value, title,label, parentId, url);
                            referConfig.setOptionConvertor(optionConvertor);
                        }
                    }

                    if (!getIsEnum() && !getIsImage()) {
                        String api = referConfig.getApi();
                        AssertUtil.mustNotNull(context.getAppWrap()
                            .getFunction(api), "参数:" + getName() + "在AppController中 找不到对应的方法:" + api + " 无法 通过ajax请求检查" + javaConsoleLink
                                               + ReflectionUtil.getJavaConsoleLink(context.getAppWrap().getClazz().getConstructors()[0]));
                    }
                }

            }
            asserted = true;
        }
    }

    public String getFalseTitle() {
        if (falseTitle == null) {
            falseTitle = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::falseTitle, "", getMembers());
        }
        return falseTitle;
    }

    public String getProps() {
        if (props == null) {
            props = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::props, "", getMembers());
        }
        return props;
    }

    public String getTemporalType() {
        if (!_hasGentemporalType && temporalType == null) {
            TemporalType temporalTypeEnum = AnnotationUtil.getAnnotationValueFormMembers(Temporal.class, Temporal::value, null, getMembers());
            if (temporalTypeEnum != null) {
                temporalType = temporalTypeEnum.toString();
            } else {
                temporalType = null;
            }
            _hasGentemporalType = true;
        }
        return temporalType;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getHidden() {
        if (hidden == null) {
            hidden = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::hidden, false, getMembers());
        }

        return hidden;
    }

    @Override
    public String getDescription() {
        String description = super.getDescription();
        if (StringUtil.isEmpty(description)) {
            description = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::value, null, getMembers());
            if (StringUtil.isEmpty(description)) {
                description = getName();
            }
            setDescription(description);
        }

        return super.getDescription();
    }

    public String getChangeBy() {
        if (changeBy == null) {
            changeBy = AnnotationUtil.getAnnotationValueFormMembers(ChangeBy.class, ChangeBy::value, "", getMembers());
        }

        return changeBy;
    }

    public ReferConfigWrap getReferConfig() {
        if (referConfig == null) {
            if (getIsEnum()) {
                referConfig = new ReferConfigWrap();
            } else {
                ReferConfig referConfigAnno = AnnotationUtil.getAnnotationFormMembers(ReferConfig.class, getMembers());
                if (referConfigAnno != null) {
                    referConfig = new ReferConfigWrap();
                    String referField = referConfigAnno.referField();
                    String api = referConfigAnno.api();

                    if (StringUtil.isBlank(referField) || StringUtil.isBlank(api)) {
                        String defaultReferField = this.getName();

                        final String[] idSubfixs = { "ID", "IDS" };//cityId ,cityIds =>city,city=>city
                        String upperCaseBeanName = defaultReferField.toUpperCase();
                        for (String idSubfix : idSubfixs) {
                            if (upperCaseBeanName.endsWith(idSubfix)) {
                                defaultReferField = defaultReferField.substring(0, (defaultReferField.length() - idSubfix.length()));
                                break;
                            }
                        }

                        //city=>null;
                        if (defaultReferField.equals(getName())) {
                            defaultReferField = null;
                        }

                        String defaultOptionBean = defaultReferField;

                        Class<?> beanClz = referConfigAnno.optionClass();

                        //City.class=>city
                        if (beanClz != Void.class) {
                            defaultOptionBean = StringUtil.uncapfirst(beanClz.getSimpleName());
                            if (StringUtil.isBlank(defaultReferField)) {
                                defaultReferField = defaultOptionBean;
                            }
                        }

                        //city=>getCityOptions
                        if (StringUtil.isBlank(api)) {
                            api = "get" + StringUtil.capfirst(defaultOptionBean) + "Options";
                        }

                        //cityId=>city,cityIds=>citys
                        if (StringUtil.isBlank(referField)) {
                            referField = defaultReferField;
                            if (this.getIsArray()) {
                                referField = defaultReferField + "s";
                            }
                        }
                    }
                    AssertUtil.mustNotBlank(referField, "参数:" + getName() + " 根据标注 @ReferConfig 无法推导出referField"
                                                        + ReflectionUtil.getJavaConsoleLink(this.getFirstAccessibleObject()));
                    AssertUtil.mustNotBlank(api,
                        "参数:" + getName() + " 根据标注 @ReferConfig 无法推导出api" + ReflectionUtil.getJavaConsoleLink(this.getFirstAccessibleObject()));
                    referConfig.setReferField(referField);
                    referConfig.setApi(api);
                }
            }
        }
        return referConfig;
    }

    public Boolean getIsSimple() {
        if (isSimple == null) {
            SimpleWrap simpleWrap = GenContext.wrapContainer.checkAndGetFromSimple(this.getClazz());
            isSimple = simpleWrap != null;
        }
        return isSimple;
    }

    public Boolean getNoJson(){
        if (this.noJson==null){
            AnnotatedElement[] members = getMembers();
            for (AnnotatedElement annotatedElement:members){
                if (annotatedElement instanceof Field) {
                    Field field = (Field) annotatedElement;
                    if (Modifier.isTransient(field.getModifiers())){
                        noJson=true;
                        return noJson;
                    }
                }
            }
            Boolean serialize = AnnotationUtil.getAnnotationValueFormMembers(JSONField.class, JSONField::serialize, true, members);
            noJson=!serialize;
        }
        return noJson;
    }
}
