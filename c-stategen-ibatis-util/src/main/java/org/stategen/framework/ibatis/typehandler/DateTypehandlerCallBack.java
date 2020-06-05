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
package org.stategen.framework.ibatis.typehandler;

import java.sql.SQLException;
import java.util.Date;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 因为有些现有的数据库不规范，对于Date类型的字段，数据库存入的是long,但java需要获得的是Date
 * 该类用于ibatis中将long与Date互转的回调类,该类注册在gen_configs.xml的typeHandlers下
 */
public class DateTypehandlerCallBack implements TypeHandlerCallback {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DateTypehandlerCallBack.class);
    
    /**
     * 直接将Date存入，由ibatis内的 UNIX_TIMESTAMP(#Date进行转换#)*1000
     */
    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        setter.setObject(parameter);
    }

    /**
     * 取数据时，不管量Date还是BIGINT,jdbc都将转换为long,因些都需要转换为时间
     */
    @Override
    public Object getResult(ResultGetter getter) throws SQLException {
        Object object =getter.getObject();
        if (object!=null){
            if (object instanceof java.util.Date){
                return object;
            }
            
            Long value = getter.getLong();
            try {
                java.util.Date date = new Date(value);
                return date;
            } catch (Exception e) {
                logger.error(
                    new StringBuilder("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:")
                        .append(e.getMessage()).append(" \n").toString(), e);
            }
        }
        return null;
    }

    @Override
    public Object valueOf(String s) {
        return null;
    }

}
