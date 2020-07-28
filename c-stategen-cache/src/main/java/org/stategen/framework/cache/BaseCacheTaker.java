package org.stategen.framework.cache;

/***
 *  输入上下文件，用来传递值
 * * 用Enum代替字符串限定数据范围
 * @author niaoge
 * @version $Id: BaseCacheTaker.java, v 0.1 2020年7月28日 下午3:44:26 XiaZhengsheng Exp $
 */
public interface BaseCacheTaker<E extends Enum<E>> {
    
    
    public <V>  V get(E enm, Class<? extends V> clz);
    
    public <V>  void set(E enm ,V v);
    
}
