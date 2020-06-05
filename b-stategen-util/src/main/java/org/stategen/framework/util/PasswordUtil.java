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

import java.util.UUID;

/**
 * The Class PasswordUtil.
 */
public class PasswordUtil {
    
    
    public static String calcuMd5Password(String password,String passwordRandom){
        StringBuilder sb =new StringBuilder().append(password);
        if (StringUtil.isNotBlank(passwordRandom)){
            sb.append(passwordRandom);
        }
        return MD5Util.md5(sb.toString());
    }
    
    public static String getPasswordRandom(){
        String randomUUID = UUID.randomUUID().toString();
        randomUUID = randomUUID.replaceAll("-", "");
        randomUUID=randomUUID.substring(0,16);
        return randomUUID;
    }
    
}
