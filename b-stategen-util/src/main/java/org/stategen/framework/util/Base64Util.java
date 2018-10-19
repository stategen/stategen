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

import java.security.Key;

import org.springframework.util.Base64Utils;

/**
 * The Class Base64Util.
 */
public class Base64Util {
    
    public static byte[] decodeFromString(String src) {
        return Base64Utils.decodeFromString(src);
    }
    
    public static String encodeToString(byte[] src) {
        return Base64Utils.encodeToString(src);
    }
    
    public static String encode(String src) {
        src =StringUtil.toStringIfNullThenEmpty(src);
        return Base64Utils.encodeToString(src.getBytes());
    }
    
    public static String encodeKey(Key key){
        AssertUtil.mustNotNull(key);
        return encodeToString(key.getEncoded());
    }

}
