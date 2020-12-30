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

/**
 * The Interface ICookieGroup.
 *
 * @author niaoge
 * @version $Id: ICookieGroup.java.java, v 0.1 2018-5-3 5:10:32 niaoge Exp \$
 */
public interface ICookieType extends ClassedEnum<ICookieType> {
    
    String getCookiePrefixName();
    
    IResponseStatus getResponseStatusOfTokenError();
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & IResponseStatus> T getResponseStatusOfTokenError(Class<? extends ICookieType> cookieTypeClz) {
        ICookieType errorCookieType =  (ICookieType) ClassedEnum.getEnum( cookieTypeClz);
        return (T) errorCookieType.getResponseStatusOfTokenError();
    }
    
}
