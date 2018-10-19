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
package org.stategen.framework.util;

import java.util.Locale;

import org.springframework.util.StringUtils;


/**
 * The Class LocaleUtil.
 */
public class LocaleUtil {
    /***en_US*/
    public final static Locale locale_US_supported=Locale.US;
    
    /***zh_CN*/
    public final static Locale locale_CHINA_supported=Locale.CHINA;
    
    public static Locale getSupportLocale(String locale){
        Locale destLocale=null;
        if (StringUtil.isNotBlank(locale) ){
            destLocale=StringUtils.parseLocaleString(locale);
        }
        return getSupportLocale(destLocale); 
    }

    public static Locale getSupportLocale(Locale destLocale) {
        Locale theLocale  =null ;
        if (destLocale!=null){
            if (destLocale.getLanguage().equals(locale_CHINA_supported.getLanguage())){
                theLocale= locale_CHINA_supported;
            } else if (destLocale.getLanguage().equals(locale_US_supported.getLanguage())){
                theLocale= locale_US_supported;
            } 
        }
        
        if (theLocale==null){
            theLocale=locale_US_supported;
        }
        return theLocale;
    }
    
    public static String getSupportedLocale(String locale){
        return getSupportLocale(locale).toString();
    }
    
    public static boolean getIsEnglish(Locale locale){
        return locale_US_supported.equals(getSupportLocale(locale));
    }

}
