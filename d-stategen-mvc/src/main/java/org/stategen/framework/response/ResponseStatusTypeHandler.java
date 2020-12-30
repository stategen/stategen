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
package org.stategen.framework.response;

import org.springframework.beans.factory.InitializingBean;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.util.AssertUtil;

/**
 * The Class ResponseStatusTypeHandler.
 */
public abstract class ResponseStatusTypeHandler implements InitializingBean {
    
    /***用枚举实现*/
    private IResponseStatus responseStatus = null;
    
    public IResponseStatus getResponseStatus() {
        return responseStatus;
    }
    
    protected void setResponseStatus(IResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtil.mustNotNull(responseStatus);
    }
    
}
