/*
 * copy from rapid-framework<code.google.com/p/rapid-framework> and modify by niaoge
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
package cn.org.rapid_framework.generator.provider.db.sql.model;

import org.stategen.framework.util.StringUtil;

import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.StringHelper;
import cn.org.rapid_framework.generator.util.typemapping.JavaPrimitiveTypeMapping;

public class SqlParameter extends Column {
        /**  */
    private static final long serialVersionUID = 1L;
        String parameterClass;
        String paramName;
        boolean isListParam = false;
        Column column;
        
        Sql sql;
        
        public SqlParameter() {}

        public SqlParameter(Column param) {
            super(param);
            BeanHelper.copyProperties(this, param);
            this.column =param;
        }
        
        public SqlParameter(SqlParameter param) {
            super(param);
            this.isListParam = param.isListParam;
            this.paramName = param.paramName;
            this.column =param;
        }
        
        public Column getColumn() {
            return column;
        }

        public String getParameterClass() {
            if(StringUtil.isNotBlank(parameterClass)) {
                return parameterClass;
            }
            return getPossibleShortJavaType();
        }
        
        public void setParameterClass(String parameterClass) {
            this.parameterClass = parameterClass;
        }

        public String getPreferredParameterJavaType() {
            return toListParam(getParameterClass());
        }
        
        String toListParam(String parameterClassName) {
            if(isListParam) {
                if(parameterClassName.indexOf("[]") >= 0){
                    return parameterClassName;
                }
                if(parameterClassName.indexOf("List") >= 0){
                    return parameterClassName;
                }
                if(parameterClassName.indexOf("Set") >= 0){
                    return parameterClassName;
                }
                return "java.util.List<"+JavaPrimitiveTypeMapping.getWrapperType(parameterClassName)+">";
            }else {
                //FIXME 增加 param.setDynamicParam(true) 以便sql查询中生成的对象为: Long而不是long,因为 long无法动态构建条件, 现在为固定使用: JavaPrimitiveTypeMapping.getWrapperType()
                return JavaPrimitiveTypeMapping.getWrapperType(parameterClassName);
            }
        }

        /**
         * 参数名称
         */
        public String getParamName() {
            return paramName;
        }
        public void setParamName(String paramName) {
            this.paramName = paramName;
        }
        /**
         * 是否是列表参数,主要是in语句,如 username in (:usernames) 则为true, username = :username则false
         * @return
         */
        public boolean isListParam() {
            return isListParam;
        }
        public void setListParam(boolean isListParam) {
            this.isListParam = isListParam;
        }
        
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null) return false;
            if(obj instanceof SqlParameter) {
                SqlParameter other = (SqlParameter)obj;
                return paramName.equals(other.getParamName());
            }else {
                return false;
            }
        }
        public int hashCode() {
            return paramName.hashCode();
        }
        public String toString() {
            return "paramName:"+paramName+" preferredParameterJavaType:"+getPreferredParameterJavaType();
        }
        
        public Sql getSql() {
            return sql;
        }
        
        public void setSql(Sql sql) {
            this.sql = sql;
        }
        
        @Override
        public String getSimple() {
           return StringHelper.toSimple(getPreferredParameterJavaType());
        }
    }