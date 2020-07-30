package org.stategen.framework.lite;

/***
 * ID发生器接口,数据插入前调用
 * 
 * @author niaoge
 * @version $Id: IIdGenerator.java, v 0.1 2020年7月29日 上午12:01:45 XiaZhengsheng Exp $
 */
public interface IIdGenerator {
    
    /****
     * *返回主键
     * 
     * @param idClz 主建类型
     * @param bizTagClz 业务类型，用 class避免硬编码，可再转换成其它类型比如String(Leaf Id 需要String业务标识)
     * @return
     */
    <K,T> K generateId(Class<K> idClz,Class<T> bizTagClz);

}