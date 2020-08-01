/*
 * Copyright (C) 2020  niaoge<78493244@qq.com>
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
package org.stategen.framework.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * *导出字段名称为常量到前端，编码手写硬编码,
 * *@Id 字段可以不用设置也可以导出到前端
 * *以前为全量导出，考虑到前端使用场量不多，反而占用一定的编译空间，故改为指定FieldNameConst才导出
 * 
 * @author niaoge
 * @version $Id: FieldNameConst.java, v 0.1 2020年7月29日 上午3:53:08 XiaZhengsheng Exp $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, FIELD, ANNOTATION_TYPE })
@Inherited
public @interface FieldNameConst {
    
}
