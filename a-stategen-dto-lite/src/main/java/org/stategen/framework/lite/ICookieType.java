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

import java.util.HashMap;
import java.util.Map;

/**
 * The Interface ICookieGroup.
 *
 * @author niaoge
 * @version $Id: ICookieGroup.java.java, v 0.1 2018-5-3 5:10:32 niaoge Exp \$
 */
public interface ICookieType {

    static Map<Class<? extends ICookieType>, ICookieType> CookieTypeCacheMap = new HashMap<Class<? extends ICookieType>, ICookieType>();

    String getCookiePrefixName();

    Class<? extends ICookieType> _getCookieTypeClz();
    
    Class<? extends IResponseStatus> getResponseStatusClzOfTokenError();

    public default void register() {
        CookieTypeCacheMap.put(this._getCookieTypeClz(), this);
    }
    
    public static ICookieType getCookieType(Class<? extends ICookieType> cookieTypeClz){
        return CookieTypeCacheMap.get(cookieTypeClz);
    }

    

}
