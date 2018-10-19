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
package javax.org.stategen.framework.lite;

/**
 *  这里继承RuntimeException异常 *.
 */
public class BusinessException extends BaseBusinessException {
    private static final long serialVersionUID = 8041796324444642375L;

    /***让fastjson反序列化*/
    public BusinessException() {
        this(null,null);
    }
    
    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message) {
        
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}