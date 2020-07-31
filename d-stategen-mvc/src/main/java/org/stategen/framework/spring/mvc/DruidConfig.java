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

package org.stategen.framework.spring.mvc;
/***
 * * 该类配置到spring context中，用来动态配置druid login 的username和password
 * @author niaoge
 * @version $Id: DruidConfig.java, v 0.1 2020年8月1日 上午12:54:14 XiaZhengsheng Exp $
 */
public class DruidConfig {
    
    public static DruidConfig CONFIG;
    
    private String loginUsername;
    
    private String loginPassword;
    
    DruidConfig(){
        CONFIG =this;
    }
    
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
    
    
    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }
    
    public String getLoginPassword() {
        return loginPassword;
    }
    
    
    public String getLoginUsername() {
        return loginUsername;
    }
    
}
