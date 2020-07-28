package org.stategen.framework.util;

/***
 * ID发生器接口,数据插入前调用
 * 
 * @author niaoge
 * @version $Id: IIDGenerator.java, v 0.1 2020年7月29日 上午12:01:45 XiaZhengsheng Exp $
 */
public interface IIDGenerator<K> {
    
    K generateId();
}
