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
package org.stategen.framework.lite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/****
 * 枚举和类对照关系，枚举实现以接口，初注册枚举和类的字典
 * 
 * @author niaoge
 * @version $Id: RegisterEnum.java, v 0.1 2020年12月27日 上午1:56:44 XiaZhengsheng Exp $
 */
public interface ClassedEnum<T> {
    static Map<Class<? extends ClassedEnum<?>>, ClassedEnum<?>> CLASS_ENUM_MAP =new ConcurrentHashMap<>();
    
    /*** 不让fastjson序列化 */
    Class<? extends T> getRegisterClass();
    
    @SuppressWarnings("unchecked")
    public default  void register() {
        CLASS_ENUM_MAP.put((Class<? extends ClassedEnum<?>>) this.getRegisterClass(), this);
    }
    
    @SuppressWarnings({ "unchecked" })
    public static <R,T extends Enum<T> & ClassedEnum<R>> T getEnum(Class<? extends R> registerClass){
        return (T) CLASS_ENUM_MAP.get(registerClass);
    }
    
    
}
