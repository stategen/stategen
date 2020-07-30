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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.stategen.framework.util.AnnotationUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

import lombok.Cleanup;

/**
 * The Class FieldRule.
 */
public class FieldRule {

    private Boolean required = false;

    private Long max;

    private Long min;

    private String message;

    private Boolean whitespace;

    private String pattern;

    static Properties messageProppties;

    public static Properties getMessageProppties() {
        if (messageProppties == null) {
            messageProppties = new Properties();


            try {
                @Cleanup
                InputStream ips = FieldRule.class.getResourceAsStream("ValidationMessages_zh_CN.xml");
//                InputStream ips = HibernateValidator.class.getResourceAsStream("ValidationMessages_zh_CN.properties");
                @Cleanup
                InputStream in = new BufferedInputStream(ips);
                messageProppties.loadFromXML(in);
//                messageProppties.load(in);
//                for (Entry<Object, Object> entry :messageProppties.entrySet()){
//                    System.out.println(MessageFormat.format("<entry key=\"{0}\">{1}</entry>", entry.getKey(),entry.getValue()));
//                }
                return messageProppties;
            } catch (Exception e) {
                AssertUtil.throwException(e.getMessage(), e);
            }
        }
        return messageProppties;
    }
    
    private static String convertMessage(String message){
        if (StringUtil.isNotEmpty(message) && message.startsWith("{") && message.endsWith("}")){
            message =message.substring(1,message.length()-1);
            Properties messageProppties = getMessageProppties();
            message= messageProppties.getProperty(message);
        }
        return message;
        
    }

    public static List<FieldRule> checkRules(Set<Class<? extends Annotation>> excludeAnnos, AnnotatedElement... members) {
        

        if (CollectionUtil.isNotEmpty(members)) {
            List<FieldRule> result = new ArrayList<FieldRule>();
            List<AnnotatedElement> paramsAnnotatedElementList = new ArrayList<AnnotatedElement>(members.length);
            if (CollectionUtil.isNotEmpty(excludeAnnos)) {
                for (AnnotatedElement annotatedElement : members) {
                    if (annotatedElement instanceof Parameter) {
                        paramsAnnotatedElementList.add(annotatedElement);
                    }
                }
            }

            AnnotatedElement[] paramsAnnotatedElements = new AnnotatedElement[paramsAnnotatedElementList.size()];
            paramsAnnotatedElementList.toArray(paramsAnnotatedElements);

            NotNull notNull = AnnotationUtil.getAnnotationFormMembers(NotNull.class,
                excludeAnnos.contains(NotNull.class) ? paramsAnnotatedElements : members);
            if (notNull != null) {
                FieldRule rule = new FieldRule();
                rule.required = notNull != null;
                rule.message = convertMessage(notNull.message());
                result.add(rule);
            }

            Max maxAnno = AnnotationUtil.getAnnotationFormMembers(Max.class, excludeAnnos.contains(Max.class) ? paramsAnnotatedElements : members);
            if (maxAnno != null) {
                Long max = maxAnno != null ? maxAnno.value() : null;
                FieldRule rule = new FieldRule();
                rule.max = max;
                rule.message = convertMessage(maxAnno.message());
                result.add(rule);
            }

            Min minAnno = AnnotationUtil.getAnnotationFormMembers(Min.class, excludeAnnos.contains(Min.class) ? paramsAnnotatedElements : members);
            if (minAnno != null) {
                Long min = minAnno != null ? minAnno.value() : null;
                FieldRule rule = new FieldRule();
                rule.min = min;
                rule.message = convertMessage(minAnno.message());
                result.add(rule);
            }

            NotBlank notBlank = AnnotationUtil.getAnnotationFormMembers(NotBlank.class,
                excludeAnnos.contains(NotBlank.class) ? paramsAnnotatedElements : members);
            if (notBlank != null) {
                FieldRule rule = new FieldRule();
                rule.whitespace = true;
                rule.message = convertMessage(notBlank.message());
                result.add(rule);
            }

            Email email = AnnotationUtil.getAnnotationFormMembers(Email.class,
                excludeAnnos.contains(Email.class) ? paramsAnnotatedElements : members);
            if (email != null) {
                createRegRule(result, email.regexp(), convertMessage(email.message()));
            }

            Pattern pattern = AnnotationUtil.getAnnotationFormMembers(Pattern.class,
                excludeAnnos.contains(Pattern.class) ? paramsAnnotatedElements : members);
            if (pattern != null) {
                createRegRule(result, pattern.regexp(), convertMessage(pattern.message()));
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
