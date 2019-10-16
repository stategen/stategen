package org.stategen.framework.util;

import java.net.URL;
import java.net.URLDecoder;

public class JarUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JarUtil.class);

    public static String getPath(Class<?> clazz) {
        if (clazz==null){
          clazz=JarUtil.class;
        }
        
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");//转化为utf-8编码
        } catch (Exception e) {
            logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:")
                .append(e.getMessage()).append(" \n").toString(), e);

        }
        
        filePath =StringUtil.trimSubfix(filePath, "!/");
        filePath =StringUtil.trimPrefix(filePath, "file:");
        if (filePath.endsWith(".jar") || filePath.endsWith(".war") ) {//可执行jar包运行的结果里包含".jar"
            //截取路径中的jar包名
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        
        return filePath;
    }
}
