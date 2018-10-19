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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * *
 * 分布式国际化中某一语言或资原被全部获，
 * 该资源会自带hash值签名，以方便版本号管理,减少数据传输量.
 *
 * @author XiaZhengsheng
 */
public class SignedResourceBundle extends ResourceBundle {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SignedResourceBundle.class);

    /***加载完毕只有读取，没有写入，所以用HashMap*/
    private Map<String, String>        lookUp                  = new HashMap<String, String>();
    
    /***随时往里面读写东西*/
    private Map<String, MessageFormat> lookUpFormat            = new ConcurrentHashMap<String, MessageFormat>();
    
    private boolean                    useCodeAsDefaultMessage = false;
    
    private int                        sign;
    
    private Object indexObject;

    public SignedResourceBundle(boolean useCodeAsDefaultMessage,Object indexObject) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
        this.indexObject =indexObject;
    }

    /****
     * 如果属性 useCodeAsDefaultMessage =true 查不到value时，将直接返回 key,避免messageResource throws 错误信息
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    @Override
    protected Object handleGetObject(String key) {
        String value = lookUp.get(key);
        if (value == null) {
            if (useCodeAsDefaultMessage) {
                value = key;
            }
            logger.warn(new StringBuffer().append("\"").append(key).append("\"").append("没找对应语言的翻译, ").append(indexObject).toString());
        }
        return value;
    }

    public MessageFormat getFormat(String code, Locale locale) {
        MessageFormat format = lookUpFormat.get(code);
        if (format == null) {
            String value = lookUp.get(code);
            if (value != null) {
                format = createMessageFormat(value, locale);
                lookUpFormat.put(code, format);
            }
        }
        return format;
    }

    @Override
    public Enumeration<String> getKeys() {
        return new ResourceBundleEnumeration(this.lookUp.keySet(), null);
    }

    public void put(String key, String value) {
        lookUp.put(key, value);
    }

    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        return new MessageFormat((msg != null ? msg : ""), locale);
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }
    
    public Map<String, String> getLookUp() {
        return lookUp;
    }

}
