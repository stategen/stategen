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
package org.stategen.framework.spring.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.OSUtil;
import org.stategen.framework.util.OSUtil.OsPath;

/**
 * The Class CompatibilityPathUtil.
 * 开发(windows)与生产(linux)兼容性工具
 * file:/data/config/dalgenX.xml 在windows下取当前ClassLoader所在盘的下对应的文件，如 D:/data/config/dalgenX.xml 
 * 在linux或mac下指向 /data/config/dalgenX.xml
 */
public class CompatibilityPathUtil {

    /**
     * The Constant logger.
     */
    final static Logger logger = LoggerFactory.getLogger(CompatibilityPathUtil.class);

    /**
     * Com resource.
     *
     * @param source the source
     * @param osPath the os path
     * @return the resource
     */
    public static Resource getCompResource(Resource source, OsPath osPath) {
        if (osPath == null) {
            osPath = OSUtil.getOsPath();
        }
        Resource result = source;
        if (source != null) {
            if (source instanceof UrlResource) {
                UrlResource urlResource = (UrlResource) source;
                try {
                    String fileName = urlResource.getURI().toString();
                    String newFileName = OSUtil.getRealUriPathByOs(fileName, osPath);

                    if (!fileName.equals(newFileName)) {
                        result = (Resource) new UrlResource(newFileName);
                    }
                } catch (Exception e) {
                    logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:")
                        .append(e.getMessage()).append(" \n").toString(), e);
                }
            }
        }
        return result;

    }

    /**
     * Com resources.
     *
     * @param sources the sources
     * @return the resource
     */
    public static Resource[] getCompResources(Resource... sources) {
        Resource[] result = null;
        if (CollectionUtil.isNotEmpty(sources)) {
            OsPath osPath = OSUtil.getOsPath();
            result = new Resource[sources.length];
            for (int i = 0; i < sources.length; i++) {
                result[i] = getCompResource(sources[i], osPath);
            }
        } else {
            if (sources == null) {
                result = new Resource[0];
            }
        }
        return result;
    }

}
