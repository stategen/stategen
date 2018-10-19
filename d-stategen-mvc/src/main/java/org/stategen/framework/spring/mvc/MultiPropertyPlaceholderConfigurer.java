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
package org.stategen.framework.spring.mvc;

import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.stategen.framework.spring.util.CompatibilityPathUtil;

/**
 * The Class MultiPropertyPlaceholderConfigurer.
 * spring属性读取器，可以用来兼容windows上的开发，发布布置在linux下，而不用修改属性配置或编译参数，尽量让编译后的代码在
 * 测式环境下能跑，也就能在生产环境下跑
 *
 * @author Xia Zhengsheng
 * @version $Id: MultiPropertyPlaceholderConfigurer.java, v 0.1 2017-1-3 20:48:12 Xia zhengsheng Exp $
 */
public class MultiPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    final static Logger logger = LoggerFactory.getLogger(MultiPropertyPlaceholderConfigurer.class);

    /***
     * 同 setLocations ，该方法对应的属性没在生产中实际使用过
     * @see org.springframework.core.io.support.PropertiesLoaderSupport#setLocation(org.springframework.core.io.Resource)
     */
    @Override
    public void setLocation(Resource location) {
        location = CompatibilityPathUtil.getCompResource(location, null);
        super.setLocation(location);
    }

    /***
     * <pre>
     * &lt;property name="locations"&gt;
     *        &lt;list&gt;
     *           &lt;value&gt;classpath*:application.properties&lt;/value&gt;
     *          &lt;!--  该值在windows下指向当前ClassLoader所有盘对应的,如 D:/data/config/dalgenX.xml 
     *                 在linux或者类linux系统下即指向/data/config/dalgenX.xml
     *        --&gt;
     *        &lt;value&gt;file:/data/config/dalgenX.xml&lt;/value&gt;
     *   &lt;/list&gt;
     *&lt;/property&gt;
     *</pre>
     * @see org.springframework.core.io.support.PropertiesLoaderSupport#setLocations(org.springframework.core.io.Resource[])
     */
    @Override
    public void setLocations(Resource... locations) {
        locations = CompatibilityPathUtil.getCompResources(locations);
        super.setLocations(locations);
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        @SuppressWarnings("unchecked")
        HashMap<String, String> urls = (HashMap<String, String>) beanFactoryToProcess.getBean("urls");

        for (Object key : props.keySet()) {
            if (key != null && key instanceof String) {
                String keyName = (String) key;
                if (keyName.endsWith("_url")) {
                    Object value = props.get(keyName);
                    if (value != null) {
                        urls.put(keyName, value.toString());
                    }
                }
            }
        }
    }
}
