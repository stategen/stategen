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

/**
 * *
 * 简单操作系统的工具类.
 *
 * @author XiaZhengsheng
 */
public class OSUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OSUtil.class);
    public static final String filePrefix = "file:";
    private static final String windows_lower = "windows";
    private static final String os_name = "os.name";

    /**
     * The Class OsPath.
     *
     * @version $Id: CompatibilityPathUtil.java, v 0.1 2016-3-16 14:17:36 xiazhengsheng Exp $
     */
    public static class OsPath {

        /**
         * *  The is windows.
         */
        boolean isWindows = false;

        /**
         * *  The disk path.
         */
        String diskPath = null;

        /**
         * *  The web context path.
         */
        String webContextPath = null;
    }

    public static OsPath getOsPath() {
        OsPath result = new OsPath();

        String osName = System.getProperties().getProperty(os_name);
        if (StringUtil.isNotEmpty(osName)) {
            osName = osName.toLowerCase();
            result.isWindows = osName.indexOf(windows_lower) > -1;
        }

        if (result.isWindows) {

//            String threadPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String jarPath = JarUtil.getPath(null);

            result.webContextPath = jarPath;
            int idx = result.webContextPath.indexOf(':');

            if (idx > -1) {
                result.diskPath = filePrefix + result.webContextPath.substring(0, idx + 1);
                result.webContextPath = filePrefix + result.webContextPath;
            }
        }

        return result;
    }

    public static String getRealUriPathByOs(String fileName, OsPath osPath) {
        if (osPath.isWindows && StringUtil.isNotEmpty(osPath.diskPath) && fileName.startsWith(filePrefix)
            && !fileName.startsWith(osPath.webContextPath) && !fileName.startsWith(osPath.diskPath)) {
            //file:
            fileName = fileName.substring(filePrefix.length());
            fileName = new StringBuffer(fileName.length() + osPath.diskPath.length()).append(osPath.diskPath)
                .append(fileName).toString();

        }
        return fileName;
    }

    public static String getRealUriPathByOs(String fileName) {
        OsPath osPath = getOsPath();
        return getRealUriPathByOs(fileName, osPath);
    }
}
