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

/**
 * The Class SimpleResponse.
 */
public class SimpleResponse {

    private String message;
    
    private Boolean success = true;
    
    public SimpleResponse() {
        
    }
    
    public SimpleResponse(Boolean success,String message) {
        this();
        setSuccess(success);
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success==null?false:success;
    }

    public void setMessage(String message, Object... agrs) {
        this.message = I18nUtil.getMessage(message, agrs);
    }
    
    public static SimpleResponse success(String message){
        SimpleResponse result =new SimpleResponse(true,message);
        return result;
    }
    
    public static SimpleResponse error(String message){
        SimpleResponse result =new SimpleResponse(false,message);
        return result;
    }
}
