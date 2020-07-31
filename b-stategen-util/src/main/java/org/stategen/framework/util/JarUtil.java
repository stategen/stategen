package org.stategen.framework.util;

import java.net.URL;
import java.net.URLDecoder;

/***
 * 
 * 
 * @author XiaZhengsheng
 * @version $Id: JarUtil.java, v 0.1 2020年6月2日 上午6:24:36 XiaZhengsheng Exp $
 */
public class JarUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JarUtil.class);
    final static String           JAR_   = ".jar";
    final static String           JAR    = "jar";
    final static String           WAR_   = ".war";

    public static String getPath(Class<?> clazz) {
        if (clazz == null) {
            clazz = JarUtil.class;
        }

        URL    url      = clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            //转化为utf-8编码
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            logger.error(new StringBuilder("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:")
                .append(e.getMessage()).append(" \n").toString(), e);

        }

        filePath = StringUtil.trimSubfix(filePath, "!/");
        filePath = StringUtil.trimPrefix(filePath, "file:");
        //可执行jar包运行的结果里包含".jar"
        if (filePath.endsWith(JAR_) || filePath.endsWith(WAR_)) {
            //截取路径中的jar包名
            filePath = filePath.substring(0, filePath.lastIndexOf('/') + 1);
        }

        return filePath;
    }

    /**
     * 在maven test中的jar，只要是本地代码，无论是否属于jar中，都是file://,而不是jar!，只有真正jar中的class才返回true
     * 
     * @param clazz
     * @return
     */
    public static boolean isInJar(Class<?> clazz) {
        URL     url          = clazz.getResource("");
        String  protocol     = url.getProtocol();
        boolean runningInJar = JAR.equals(protocol);
        return runningInJar;
    }
}
