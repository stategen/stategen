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

import javax.servlet.ServletException;

import com.alibaba.druid.support.http.StatViewServlet;

/***
 * *动态加载druid login的配置
 * 
 * @author niaoge
 * @version $Id: DruidServlet.java, v 0.1 2020年8月1日 上午12:51:35 XiaZhengsheng Exp $
 */
public class DruidServlet extends StatViewServlet {
    
    /**  */
    private static final long serialVersionUID = 1L;
    
    @Override
    public void init() throws ServletException {
        super.init();
        //如果startonload=0,将获取不到DruidLoginConfig.CONFIG
        if (DruidConfig.CONFIG != null) {
            this.username = DruidConfig.CONFIG.getLoginUsername();
            this.password = DruidConfig.CONFIG.getLoginPassword();
        }
    }
}
