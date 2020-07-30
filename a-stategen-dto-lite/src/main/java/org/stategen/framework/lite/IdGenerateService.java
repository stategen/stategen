package org.stategen.framework.lite;


public interface IdGenerateService<K> {
   /***
    *  
    * 
    * @param bizTagClz 业务类型，用 class避免硬编码，可再转换成其它类型比如String(Leaf Id 需要String业务标识)
    * @return
    */
   <T> K generateId(Class<T> bizTagClz);
    
}
