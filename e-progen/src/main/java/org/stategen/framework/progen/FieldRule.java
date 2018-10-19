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
package org.stategen.framework.progen;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.CollectionUtil;

/**
 * The Class FieldRule.
 */
public class FieldRule {
    
    private Boolean required =false;

    private Long max;

    private Long min;

    private String message;

    private Boolean whitespace;

    private String pattern;

    public static List<FieldRule> checkRules(AnnotatedElement... members) {
        if (CollectionUtil.isNotEmpty(members)) {
            List<FieldRule> result = new ArrayList<FieldRule>();
            NotNull notNull = AnnotationUtil.getAnnotationFormMembers(NotNull.class, members);
            if (notNull != null) {
                FieldRule rule = new FieldRule();
                rule.required = notNull != null;
                rule.message = notNull.message();
                result.add(rule);
            }

            Max maxAnno = AnnotationUtil.getAnnotationFormMembers(Max.class, members);
            if (maxAnno != null) {
                Long max = maxAnno != null ? maxAnno.value() : null;
                FieldRule rule = new FieldRule();
                rule.max = max;
                rule.message = maxAnno.message();
                result.add(rule);
            }

            Min minAnno = AnnotationUtil.getAnnotationFormMembers(Min.class, members);
            if (minAnno != null) {
                Long min = minAnno != null ? minAnno.value() : null;
                FieldRule rule = new FieldRule();
                rule.min = min;
                rule.message = minAnno.message();
                result.add(rule);
            }

            NotBlank notBlank = AnnotationUtil.getAnnotationFormMembers(NotBlank.class, members);
            if (notBlank != null) {
                FieldRule rule = new FieldRule();
                rule.whitespace = true;
                rule.message = notBlank.message();
                result.add(rule);
            }

            Email email = AnnotationUtil.getAnnotationFormMembers(Email.class, members);
            if (email != null) {
                createRegRule(result, email.regexp(), email.message());
            }

            Pattern pattern = AnnotationUtil.getAnnotationFormMembers(Pattern.class, members);
            if (pattern != null) {
                createRegRule(result, pattern.regexp(), pattern.message());
            }

            return result;
        }
        return CollectionUtil.newEmptyList();
    }

    private static void createRegRule(List<FieldRule> result, String regexp, String message) {
        FieldRule rule = new FieldRule();
        rule.pattern = regexp;
        rule.message = message;
        result.add(rule);
    }

    public Boolean getRequired() {
        return required;
    }

    public Long getMax() {
        return max;
    }

    public Long getMin() {
        return min;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getWhitespace() {
        return whitespace;
    }

    public String getPattern() {
        return pattern;
    }

}
