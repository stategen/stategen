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
package org.stategen.framework.spring.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;

import org.springframework.context.support.AbstractMessageSource;
import org.stategen.framework.util.AssertUtil;

/**
 * *
 * 抽象的分布式国际化 messageSource
 * 当类被spring 注册后 bundleProvider 方法需要被赋值.
 *
 * @author XiaZhengsheng
 */
public class AbstractDistributeMessageSource extends AbstractMessageSource {
    
    private BundleProvider bundleProvider ;
    
    private boolean useCodeAsDefaultMessage =false;
    
    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        try {
            return bundleProvider.resolveCodeWithoutArguments(code, locale);
        } catch (MissingResourceException e) {
            if (useCodeAsDefaultMessage){
                return code;
            }
            throw e;
        }
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return bundleProvider.resolveCode(code, locale);
    }
    
    public void setBundleProvider(BundleProvider bundleProvider) {
        AssertUtil.mustNotNull(bundleProvider,"bundleProvider can not be null!");
        this.bundleProvider =bundleProvider;
        if (this.useCodeAsDefaultMessage && bundleProvider.getUseCodeAsDefaultMessage()==null){
            bundleProvider.setUseCodeAsDefaultMessage(true);
        }
    }
    
    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage){
        super.setUseCodeAsDefaultMessage(useCodeAsDefaultMessage);
        this.useCodeAsDefaultMessage=useCodeAsDefaultMessage;
    }

}
