package org.stategen.framework.util;

/****
 * 由dao层强制调用，ide或编译器检则，防止人工忘了写 水平权限新数据时，必须往水平表中插入数据
 * 
 * @author niaoge
 * @version $Id: AfterInsertService.java, v 0.1 2020年7月27日 下午5:29:43 XiaZhengsheng Exp $
 */
public interface AfterInsertService<T> {
    
    /*** 在新数据创建之后处理数据,比如水平权限 */
    void afterInsert(T t);
    
}
