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
package org.stategen.framework.generator.util;

import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.provider.db.sql.model.SqlParameter;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;

/**
 * The Class TableHelper.
 */
public class TableHelper {
    
    public static boolean putParameterAsField(Table table,SqlParameter parameter,Sql sql  ){
        table.putParameterAsField(parameter);
        parameter.setSql(sql);
        return true;
    }
    
}
