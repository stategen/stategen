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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * *
 * 该类被spring注册后，可拿到 applicationContext对象
 * 以及从applicationContext获得bean.
 *
 * @author XiaZhengsheng
 */
public class SpringContextHolder implements ApplicationContextAware {
    final static org.slf4j.Logger     logger = org.slf4j.LoggerFactory.getLogger(SpringContextHolder.class);
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        Object bean = null;
        try {
            bean = applicationContext.getBean(beanName);
        } catch (BeansException e) {
            logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage()).append(" \n")
                .toString(), e);
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Object... agrs) {
        Object bean = null;
        try {
            bean = applicationContext.getBean(beanName, agrs);
        } catch (BeansException e) {
            logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage()).append(" \n")
                .toString(), e);
        }
        return (T) bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        Object bean = null;
        try {
            bean = applicationContext.getBean(clazz);
        } catch (BeansException e) {
            logger.error(new StringBuffer("在运行时产生错误信息,此错误信息表示该相应方法已将相关错误catch了，请尽快修复!\n以下是具体错误产生的原因:").append(e.getMessage()).append(" \n")
                    .toString(), e);
        }
        return (T) bean;
    }

}
