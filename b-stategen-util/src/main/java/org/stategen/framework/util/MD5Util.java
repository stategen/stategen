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

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MD5Util.
 *
 */
public class MD5Util {
    final static Logger logger = LoggerFactory.getLogger(MD5Util.class);
    private final static String md5="MD5";
    private final static String utf8="UTF-8";

    /***
     * 获得md5 String
     * 
     * @param dest
     * @return
     */
    public static String md5(String dest) {
        try {
            MessageDigest md = MessageDigest.getInstance(md5);
            md.update(dest.getBytes(utf8));
            byte[] digest = md.digest();
            StringBuffer md5 = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
                md5.append(Character.forDigit((digest[i] & 0xF), 16));
            }
            dest = md5.toString();
        } catch (Exception e) {
            logger.error(
                new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage())
                    .append(" \n").toString(), e);
        }
        return dest;
    }

    /**
     * Md5 byte.获得md5 byte
     *
     * @param dest the encrypt str
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] md5Byte(String dest) throws Exception {
        MessageDigest md = MessageDigest.getInstance(md5);
        md.update(dest.getBytes("UTF-8"));
        return md.digest();
    }

}
