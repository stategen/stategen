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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * The Class MultiLocaleResolver.
 */
public class MultiLocaleResolver extends CookieLocaleResolver{
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MultiLocaleResolver.class);
    
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale LocaleResolved = super.resolveLocale(request);
        if (logger.isDebugEnabled()) {
            logger.debug(new StringBuilder("输出info信息: LocaleResolved:").append(LocaleResolved).toString());
        }
        return LocaleResolved;
    }
    
}
