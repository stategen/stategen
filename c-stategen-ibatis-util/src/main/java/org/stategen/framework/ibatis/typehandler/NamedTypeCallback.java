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

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * *
 * 由于技术的改进，该类暂时用不上.
 *
 * @author XiaZhengsheng
 */
public class NamedTypeCallback implements TypeHandlerCallback {

    @Override
    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
    }

    @Override
    public Object getResult(ResultGetter getter) throws SQLException {
        return null;
    }

    @Override
    public Object valueOf(String s) {
        return null;
    }

}
