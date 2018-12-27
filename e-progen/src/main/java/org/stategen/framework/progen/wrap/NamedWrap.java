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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.stategen.framework.annotation.ChangeBy;
import org.stategen.framework.annotation.Editor;
import org.stategen.framework.annotation.ReferConfig;
import org.stategen.framework.lite.enums.EditorType;
import org.stategen.framework.progen.FieldRule;
import org.stategen.framework.progen.GenContext;
import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.StringUtil;

import io.swagger.annotations.ApiModelProperty;

/**
 * The Class NamedWrap.
 */
public class NamedWrap extends MemberWrap {

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
    
    private String nullLablel;
    
    private Set<Class<? extends Annotation>> _excludeAnnos;

    public List<FieldRule> getRules() {
        if (rules == null) {
            Set<Class<? extends Annotation>> excludeAnnos =_excludeAnnos;
            if (excludeAnnos==null){
                excludeAnnos=new HashSet<Class<? extends Annotation>>(0);
            }
            rules = FieldRule.checkRules(excludeAnnos,getMembers());
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
            if (editorType!=null){
                isImage=editorType.getSimpleName().equals(EditorType.Image.class.getSimpleName());
            } else {
                isImage =false;
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
            Class<? extends EditorType> editorTypeClass = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::value,null, getMembers());
            if (editorTypeClass != null) {
                editorType = editorTypeClass.getSimpleName();
            } else {
                editorType = "";
            }
        }
        return editorType;
    }
    
    public String getNullLablel() {
        if (nullLablel == null) {
            nullLablel = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::nullLable,"", getMembers());
        }
        return nullLablel;
    }

    public String getProps() {
        if (props == null) {
            props = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::props,"", getMembers());
        }
        return props;
    }

    public String getTemporalType() {
        if (!_hasGentemporalType && temporalType == null) {
            TemporalType temporalTypeEnum = AnnotationUtil.getAnnotationValueFormMembers(Temporal.class, Temporal::value,null, getMembers());
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
            hidden = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::hidden,false, getMembers());
        }

        return hidden;
    }

    @Override
    public String getDescription() {
        String description = super.getDescription();
        if (StringUtil.isEmpty(description)) {
            description = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::value,null, getMembers());
            if (StringUtil.isEmpty(description)) {
                description = getName();
            }
            setDescription(description);
        }

        return super.getDescription();
    }

    public String getChangeBy() {
        if (changeBy == null) {
            changeBy = AnnotationUtil.getAnnotationValueFormMembers(ChangeBy.class, ChangeBy::value,"", getMembers());
        }

        return changeBy;
    }

    public ReferConfigWrap getReferConfig() {
        if (referConfig == null) {
            ReferConfig referConfigAnno = AnnotationUtil.getAnnotationFormMembers(ReferConfig.class, getMembers());
            if (referConfigAnno != null) {
                referConfig = new ReferConfigWrap();
                referConfig.setNone(referConfigAnno.none());
                if (!getIsEnum()) {
                    String referName = null;
                    Class<?> bean = referConfigAnno.bean();
                    if (bean != Void.class) {
                        referName = bean.getSimpleName();
                    }
                    List<String> idSubfixs = Arrays.asList("Id", "Ids", "ID", "IDs");

                    if (StringUtil.isEmpty(referName)) {
                        referName = this.getName();
                        for (String idSubfix : idSubfixs) {
                            if (referName.endsWith(idSubfix)) {
                                referName = referName.substring(0, (referName.length() - idSubfix.length()));
                                break;
                            }
                        }
                    }

                    String api = referConfigAnno.api();
                    if (StringUtil.isBlank(api)) {
                        api = "get" + StringUtil.capfirst(referName) + "Options";
                    }
                    referConfig.setApi(api);

                    String referField = referConfigAnno.referField();
                    if (StringUtil.isBlank(referField)) {
                        referField = referName;
                        if (this.getIsArray()) {
                            referField = referName + "s";
                        }
                    }
                    referConfig.setReferField(referField);
                }
            } else if (getIsEnum()) {
                referConfig = new ReferConfigWrap();
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

}
