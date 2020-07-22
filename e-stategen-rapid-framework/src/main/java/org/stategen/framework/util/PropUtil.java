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

import static org.stategen.framework.util.Consts.base_name_suffix;
import static org.stategen.framework.util.Consts.controller_name_suffix;
import static org.stategen.framework.util.Consts.dao_name_suffix;
import static org.stategen.framework.util.Consts.dto_name_suffix;
import static org.stategen.framework.util.Consts.impl_name_suffix;
import static org.stategen.framework.util.Consts.internal_service_suffix;
import static org.stategen.framework.util.Consts.javaFile;
import static org.stategen.framework.util.Consts.service_name_suffix;

import java.util.Properties;

import cn.org.rapid_framework.generator.GeneratorProperties;

/**
 * The Class<?> PropUtil.
 */
public class PropUtil {

    public static Properties pts = GeneratorProperties.getProperties();
    

    public static String get(String key) {
        String val = pts.getProperty(key);
        if (StringUtil.isEmpty(val)) {
            return "";
        }
        return val;
    }
    
    public static String getJavaSuffix(){
        return javaFile;
    }
    
    public static String getDTOSuffixJava(){
        return get(dto_name_suffix)+javaFile;
    }
    
    public static String getDaoSuffixJava(){
        return get(dao_name_suffix)+javaFile;
    }    
    
    public static String getDaoImplSuffixJava(){
        return get(dao_name_suffix)+get(impl_name_suffix)+javaFile;
    }    
    
    public static String getServiceSuffixJava(){
        return get(service_name_suffix)+get("projectName")+javaFile;
    }


    public static String getServiceSuffixInternalJava(){
        return get(service_name_suffix)+get(internal_service_suffix)+javaFile;
    }    
    
    public static String getServiceImplSuffixJava(){
        return get(service_name_suffix)+get(impl_name_suffix)+javaFile;
    }   
    
    public static String getControllerSuffixJava(){
        return get(controller_name_suffix)+javaFile;
    }    
    
    public static String getControllerSuffixBaseJava(){
        return get(controller_name_suffix)+get(base_name_suffix)+javaFile;
    }        
    
    

}
