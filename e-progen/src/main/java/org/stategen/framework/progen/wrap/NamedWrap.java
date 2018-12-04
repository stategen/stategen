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

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.stategen.framework.annotation.Editor;
import org.stategen.framework.lite.Image;
import org.stategen.framework.lite.enums.EditorType;
import org.stategen.framework.progen.FieldRule;
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
    

    public List<FieldRule> getRules() {
        if (rules == null) {
            rules = genRules();
        }
        return rules;
    }

    public List<FieldRule> genRules() {
        List<FieldRule> checkRules = FieldRule.checkRules(getMembers());
        return checkRules;
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
            EditorType edType = AnnotationUtil.getAnnotationValueFormMembers(Editor.class, Editor::value, getMembers());
            if (edType!=null){
                editorType=edType.name();
            } else {
                editorType ="";
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
            hidden = AnnotationUtil.getAnnotationValueFormMembers(ApiModelProperty.class, false,ApiModelProperty::hidden, getMembers());
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
    
    @Override
    public boolean getIsEnum() {
        return super.getIsEnum();
    }

}
