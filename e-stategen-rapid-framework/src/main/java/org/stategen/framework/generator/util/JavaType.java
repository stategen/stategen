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
package org.stategen.framework.generator.util;

import org.stategen.framework.util.Consts;
import org.stategen.framework.util.Context;
import org.stategen.framework.util.PropUtil;
import org.stategen.framework.util.Setting;
import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.util.GLogger;

/**
 * The Enum JavaType.
 */
/**
 * 
 * @author XiaZhengsheng
 * @version $Id: JavaType.java, v 0.1 2019年3月15日 上午5:18:39 XiaZhengsheng Exp $
 */
public enum JavaType {
    
    /**
    *  <pre>  * The is entry.  </pre>
    */
    isEntry,
    
    /**
    *  <pre>  * The is dao.  </pre>
    */
    isDao,
    
    /**
    *  <pre>  * The is dao impl.  </pre>
    */
    isDaoImpl,
    
    /**
    *  <pre>  * The is service.  </pre>
    */
    isServiceFacade,
    
    isServiceInternal,
    /**
    *  <pre>  * The is service impl.  </pre>
    */
    isServiceImpl,
    /**
    *  <pre>  * The is controller.  </pre>
    */
    
    isControllerBase,
    
    isController,
    ;
    
    public static final String javaTypeKey = "javaType";
    
    /**
     * Gets the available auto include.
     *
     * @param conf the conf
     * @param autoIncludes the auto includes
     * @return the available auto include
     */
    
    public static JavaType getJavaType(String outFileName) {
        
        if (!outFileName.endsWith(".java") || outFileName.endsWith("SqlDaoSupportBase.java")) {
            return null;
        }
        
        JavaType javaType     = null;
        String   javaTypeName = (String) Context.get(javaTypeKey);
        if (StringUtil.isNotEmpty(javaTypeName)) {
            try {
                javaType = JavaType.valueOf(javaTypeName);
            } catch (Exception e) {
                GLogger.error(new StringBuilder("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage())
                        .append(" \n").toString(), e);
            }
            GLogger.info(outFileName+"--------"+javaType);
        }
        
        if (javaType != null) {
            return javaType;
        }
        //以下和之前保持兼容
        if (Consts.dao.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getDaoSuffixJava())) {
            return JavaType.isDao;
        }
        
        if (Consts.dao.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getDaoImplSuffixJava())) {
            return JavaType.isDaoImpl;
        }
        
        //TODO ,以后都改成 从Freemarker文件中塞入变量到 Context中，再判断，这样将非常准确
        if (Consts.service.equals(Setting.current_gen_name) && outFileName.endsWith(PropUtil.getServiceSuffixJava())) {
            return JavaType.isServiceFacade;
        }
        
        if (Consts.service.equals(Setting.current_gen_name)
                && outFileName.endsWith(PropUtil.getServiceSuffixInternalJava())) {
            return JavaType.isServiceInternal;
        }
        
        if (Consts.service.equals(Setting.current_gen_name)
                && outFileName.endsWith(PropUtil.getServiceImplSuffixJava())) {
            return JavaType.isServiceImpl;
        }
        
        if (Consts.controller.equals(Setting.current_gen_name)
                && outFileName.endsWith(PropUtil.getControllerSuffixJava())) {
            return JavaType.isController;
        }
        
        if (Consts.controller.equals(Setting.current_gen_name)
                && outFileName.endsWith(PropUtil.getControllerSuffixBaseJava())) {
            return JavaType.isControllerBase;
        }
        
        if (Consts.pojo.equals(Setting.current_gen_name)) {
            return JavaType.isEntry;
        }
        
        return JavaType.isEntry;
    }
    
}
