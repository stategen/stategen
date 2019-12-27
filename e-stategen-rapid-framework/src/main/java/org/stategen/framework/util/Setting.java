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
package org.stategen.framework.util;

import java.util.Map;

/**
 * The Class Setting.
 */
public class Setting {
    public static Boolean date_long_field_to_date = null;
    
    public static Boolean gen_select_create_date_field = null;
    public static Boolean gen_select_update_date_field = null;
    public static Boolean gen_select_delete_flag_field = null;
    
    public static String current_gen_name = null;


    public static Map<String, String> updated_date_fields = null;
    public static Map<String, String> created_date_fields = null;
    public static Map<String, String> soft_delete_fields = null;



    public static Map<String, String> getUpdated_date_fields() {
        return updated_date_fields;
    }

    public static Map<String, String> getCreated_date_fields() {
        return created_date_fields;
    }

    public static Map<String, String> getSoft_delete_fields() {
        return soft_delete_fields;
    }
    

}
