package org.stategen.framework.util;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/***
 * 该工具用在线程池中，如tomcat,线程使用完成放回线程池中，对象应该清空,防止泄露
 * 
 * @author XiaZhengsheng
 * @version $Id: ThreadLocalUtil.java, v 0.1 2020年6月5日 上午1:32:36 XiaZhengsheng Exp $
 */
public class ThreadLocalUtil {

    /**threadLocal创建后，基本都是程序退出时清空，因此读多写少*/
    private static Set<ThreadLocal<?>> threadLocals = new CopyOnWriteArraySet<ThreadLocal<?>>();

    public static <T> void registThreadLocal(ThreadLocal<T> threadLocal) {
        threadLocals.add(threadLocal);
    }

    public static <T> ThreadLocal<T> createLocalThread() {
        ThreadLocal<T> threadLocal = new ThreadLocal<T>();
        registThreadLocal(threadLocal);
        return threadLocal;
    }

    public static <T> void removeThreadLocal(ThreadLocal<T> threadLocal) {
        threadLocals.remove(threadLocal);
    }

    /**由filter或拦截器最后清空，防止内存泄露*/
    public static void cleanValuesOnThreadLocals() {
        for (ThreadLocal<?> threadLocal : threadLocals) {
            threadLocal.remove();
        }
    }

}
