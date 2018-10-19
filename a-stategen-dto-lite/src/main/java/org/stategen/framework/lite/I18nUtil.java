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
package org.stategen.framework.lite;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The Class I18nUtil.
 */
public class I18nUtil {
    private static I18nGenerator i18nGenerator = null;

    public static String getMessage(String message, Object... agrs) {
        if (!isBlank(message)){
            if (I18nUtil.i18nGenerator != null) {
                message = I18nUtil.i18nGenerator.genBundleMessage(message, agrs);
            }
        }
        return message;
    }

    public static void setI18nGenerator(I18nGenerator i18nGenerator) {
        I18nUtil.i18nGenerator = i18nGenerator;
    }


    public static <T> void changeToEnlsih(T dest, Function<? super T, String> enGetMethod, BiConsumer<T, String> setMethod, boolean isEnglish){
        if (dest!=null){
            if (isEnglish){
                String k = enGetMethod.apply(dest);
                if (k!=null){
                    setMethod.accept(dest, k);
                }
            }
        }
    }

    public static <T> void changeToEnlsihEnglish(Collection<? extends T> items, Function<? super T, String> enGetMethod, BiConsumer<T, String> setMethod,boolean isEnglish){
        if (items!=null){
            for (T t : items) {
                changeToEnlsih(t, enGetMethod, setMethod,isEnglish);
            }
        }
    }
    
    private static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
