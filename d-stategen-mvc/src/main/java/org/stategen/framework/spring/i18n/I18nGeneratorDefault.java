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
package org.stategen.framework.spring.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.stategen.framework.lite.I18nGenerator;
import org.stategen.framework.lite.I18nUtil;
import org.stategen.framework.spring.util.RequestUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.StringUtil;

/**
 * *
 * 默认 fail后，需要用国际化输出结果默认处理类 ,该类需要注册到spring bean中 ,并且设置属性 messageSource.
 *
 * @author XiaZhengsheng
 */
public class I18nGeneratorDefault implements I18nGenerator {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(I18nGeneratorDefault.class);

    private MessageSource         messageSource;

    public I18nGeneratorDefault() {
        I18nUtil.setI18nGenerator(this);
    }

    @Override
    public String genBundleMessage(String destMessage, Object... agrs) {
        Locale locale = RequestUtil.getLocale();
        Object[] newAgrs=null;
        if (CollectionUtil.isNotEmpty(agrs)) {
            newAgrs = new Object[agrs.length];
            for (int i = 0; i < agrs.length; i++) {
                Object agr = agrs[i];
                //将字符串替换为国际化中的字符中，其它不变
                if (agr != null && agr instanceof String) {
                    String agrString = (String) agr;
                    if (agrString.startsWith("$")) {
                        agrString = agrString.substring(1);
                    }
                    if (StringUtil.isNotEmpty(agrString)) {
                        agr = messageSource.getMessage(agrString, null, locale);
                    }
                }
                newAgrs[i] = agr;
            }
        }

        String result = messageSource.getMessage(destMessage, newAgrs, locale);
        return result;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
