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
import java.sql.Types;
import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.util.StringUtil;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;
/***
 * ibatis中 spring与 Currency互转回调类，该类注册在gen_configs.xml的typeHandlers中
 * 
 * @author XiaZhengsheng
 */
public class CurrencyHandlerCallback  implements TypeHandlerCallback{
    final static Logger logger = LoggerFactory.getLogger(CurrencyHandlerCallback.class);


    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
        if (null == parameter) {
            setter.setNull(Types.VARCHAR);
        } else {
            String enumName = ((Currency)parameter).getCurrencyCode();
            setter.setObject(enumName);
        }
    }

    public Object getResult(ResultGetter getter) throws SQLException {
        Object result = null;
        
//        if (getter.wasNull()){
//            return null;
//        }
        
        Object value =getter.getObject();
        if (value!=null) {
            String currStr =getter.getString();
            if (StringUtil.isNotBlank(currStr)){
                Currency currency =getCurrency(currStr);
                return currency;
            }
        }
        return result;
    }

    public Object valueOf(String name) {
        Object result = null;
        result = getCurrency(name);
        return result;
    }
    
    private Currency getCurrency(String currencyCode){
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency;
        } catch (Exception e) {
            logger
                .error(
                    new StringBuffer("\"").append(currencyCode).append("\"  不能转换为货币!").toString(), e);
            return null;
        }
    }


}
