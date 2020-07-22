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
package org.stategen.framework.spring.util;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * The listener interface for receiving driversShutdown events.
 * The class that is interested in processing a driversShutdown
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addDriversShutdownListener<code> method. When
 * the driversShutdown event occurs, that object's appropriate
 * method is invoked.
 *
 * @see DriversShutdownEvent
 */
public class DriversShutdownListener implements ServletContextListener {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DriversShutdownListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("DubboShutDownListener.contextDestroyed called!");
       try {
//           final Class protocolConfig = Class.forName("com.alibaba.dubbo.config.ProtocolConfig");
           final Class<?> dubboShutdownHook = Class.forName("org.apache.dubbo.config.DubboShutdownHook");
           final Method method = dubboShutdownHook.getMethod("destroyAll");
           
           method.invoke(dubboShutdownHook);
       } catch (Exception e){
            sce.getServletContext().log("destory dubbo failed:"+e.getMessage(), e);

       }
       
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(String.format("deregistering jdbc driver: %s", driver));
            }
        }
        
        
    }

}
