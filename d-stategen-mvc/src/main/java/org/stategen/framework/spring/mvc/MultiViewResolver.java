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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.stategen.framework.util.CollectionUtil;

/**
 * 该代码复制自网络，稍做优化，将原来的循环查找改为从HashMap中查找
 * 该spring mvc 本身不解析网页，而是将不同后缀的文件指向特定的解析器,如 .htm, .js, .ht, .jsp...
 * 
 * 自定义视图解析(通过配置实现多视图整合,如jsp,velocity,freemarker,pdf,excel...)
 * @author huligong
 *
 */
public class MultiViewResolver implements ViewResolver {

    private static Logger             logger              = LoggerFactory.getLogger(MultiViewResolver.class);

    private Map<String, ViewResolver> viewResolverMap     = null;

    private ViewResolver              defaultViewResolver = null;

    /**
     * 根据 spring mvc return 的返回置的 后缀名快速解析.
     *
     * @param viewName the view name
     * @param locale the locale
     * @return the view
     * @throws Exception the exception
     * @see org.springframework.web.servlet.ViewResolver#resolveViewName(java.lang.String, java.util.Locale)
     */
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        ViewResolver viewResolver = null;
        if (viewResolverMap != null && null != viewName) {
            int idx = viewName.lastIndexOf('.');
            if (idx > -1) {
                String suffix = viewName.substring(idx);
                viewResolver = viewResolverMap.get(suffix);
            }
        }

        if (viewResolver != null) {
//            if (logger.isDebugEnabled()) {
//                logger.debug(new StringBuffer("找到 viewResolver:").append(viewName).toString());
//            }
        } else {
            if (defaultViewResolver != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuffer("没找到 viewResolver:").append(viewName).append(",启用缺省的viewResolver")
                        .toString());
                }
                viewResolver = defaultViewResolver;
            }
        }

        if (viewResolver != null) {
            return viewResolver.resolveViewName(viewName, locale);
        }
        return null;
    }

    /**
     * 获得属性后加工为方便查询的map缓存起来,
     *
     * @param viewResolverMap the view resolver map
     */
    public void setViewResolverMap(Map<Set<String>, ViewResolver> viewResolverMap) {
        if (CollectionUtil.isNotEmpty(viewResolverMap)) {
            //将内容导入成一对一的Map,以便快速地查询
            this.viewResolverMap = new HashMap<String, ViewResolver>();
            for (Entry<Set<String>, ViewResolver> entry : viewResolverMap.entrySet()) {
                ViewResolver viewResolver = entry.getValue();
                Set<String> suffixs = entry.getKey();
                for (String suffix : suffixs) {
                    this.viewResolverMap.put(suffix, viewResolver);
                }
            }
        }
    }

    /**
     * Sets the default view resolver.
     *
     * @param defaultViewResolver the new default view resolver
     */
    public void setDefaultViewResolver(ViewResolver defaultViewResolver) {
        this.defaultViewResolver = defaultViewResolver;
    }
}