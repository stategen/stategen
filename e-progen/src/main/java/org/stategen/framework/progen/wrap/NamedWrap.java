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

import java.util.Arrays;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.stategen.framework.annotation.ChangeBy;
import org.stategen.framework.annotation.Editor;
import org.stategen.framework.annotation.Image;
import org.stategen.framework.annotation.OptionConfig;
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

    private OptionConfigWrap optionConfig;

    private Boolean isSimple;
    
    private String changeBy;

    public List<FieldRule> getRules() {
        if (rules == null) {
            rules = FieldRule.checkRules(getMembers());
        }
        return rules;
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
            isImage = AnnotationUtil.getAnnotationFormMembers(Image.class, getMembers()) != null;
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
            Class<? extends EditorType> editorTypeClass = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::value, getMembers());
            if (editorTypeClass != null) {
                editorType = editorTypeClass.getSimpleName();
            } else {
                editorType = "";
            }
        }
        return editorType;
    }

    public String getTemporalType() {
        if (!_hasGentemporalType && temporalType == null) {
            TemporalType temporalTypeEnum = AnnotationUtil.getAnnotationValueFormMembers(Temporal.class, Temporal::value, getMembers());
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
            hidden = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, false, ApiModelProperty::hidden, getMembers());
        }

        return hidden;
    }

    @Override
    public String getDescription() {
        String description = super.getDescription();
        if (StringUtil.isEmpty(description)) {
            description = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, ApiModelProperty::value, getMembers());
            if (StringUtil.isEmpty(description)) {
                description = getName();
            }
            setDescription(description);
        }

        return super.getDescription();
    }
    
    public String getChangeBy() {
        if (changeBy==null){
            changeBy = AnnotationUtil.getAnnotationValueFormMembers(ChangeBy.class, ChangeBy::value, getMembers());
            if (StringUtil.isEmpty(changeBy)){
                changeBy ="";
            }
        }
        
        return changeBy;
    }

    public OptionConfigWrap getOptionConfig() {
        if (optionConfig == null) {
            OptionConfig optionConfigAnno = AnnotationUtil.getAnnotationFormMembers(OptionConfig.class, getMembers());
            if (optionConfigAnno != null) {
                optionConfig = new OptionConfigWrap();
                optionConfig.setNone(optionConfigAnno.none());
                if (!getIsEnum()) {
                    String optionName = null;
                    Class<?> bean = optionConfigAnno.bean();
                    if (bean != Void.class) {
                        optionName = bean.getSimpleName();
                    }
                    List<String> idSubfixs = Arrays.asList("Id", "Ids", "ID", "IDs");

                    if (StringUtil.isEmpty(optionName)) {
                        optionName = this.getName();
                        for (String idSubfix : idSubfixs) {
                            if (optionName.endsWith(idSubfix)) {
                                optionName = optionName.substring(0, (optionName.length() - idSubfix.length()));
                                break;
                            }
                        }
                    }

                    String api = optionConfigAnno.api();
                    if (StringUtil.isBlank(api)) {
                        api = "get" + StringUtil.capfirst(optionName) + "Options";
                    }
                    optionConfig.setApi(api);

                    String defaultOption = optionConfigAnno.defaultOption();
                    if (StringUtil.isBlank(defaultOption)) {
                        defaultOption = optionName;
                        if (this.getIsArray()) {
                            defaultOption = optionName + "s";
                        }
                    }
                    optionConfig.setDefaultOption(defaultOption);
                }
            } else if (getIsEnum()){
                optionConfig = new OptionConfigWrap();
            }
        }
        return optionConfig;
    }

    public Boolean getIsSimple() {
        if (isSimple == null) {
            SimpleWrap simpleWrap = GenContext.wrapContainer.checkAndGetFromSimple(this.getClazz());
            isSimple = simpleWrap != null;
        }
        return isSimple;
    }

}
