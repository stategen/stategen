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
package org.stategen.framework.web.cookie;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import org.stategen.framework.lite.ICookieType;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.response.ResponseStatusTypeHandler;
import org.stategen.framework.response.ResponseUtil;
import org.stategen.framework.spring.util.RequestUtil;
import org.stategen.framework.util.AssertUtil;
import org.stategen.framework.util.CollectionUtil;
import org.stategen.framework.util.GetOrCreateWrap;

import configs.Configration;

/**
 * The Class CookieGroup.
 * *可以用Enum代替字符串限定数据范围
 */
public class CookieGroup<E extends Enum<E>> extends ResponseStatusTypeHandler {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CookieGroup.class);

    //iCookieGroup与CookieGroupValidator的对应关系
    private static Map<ICookieType, CookieGroup<?>> COOKIE_GROUP_MAP = new ConcurrentHashMap<ICookieType, CookieGroup<?>>(2);
    
    private String _cookieNamePrefix = null;
    //校验的cookie名称
    private static String TOKEN_COOKIE_NAME = "_token_";

    //最后生成tocken的cookieName,值为：_cookieNamePrefix+_tokenCookieName
    private String _tokenCookieName = null;

    //cookie的生命
    private Integer secondsOfCookieAge = Configration.COOKIE_DEFAULT_AGE;

    //该项校验对的组别 TODO 要springboot 内部类先加载时，导致CookieType不能先加载，regist方法没有被调用，因此采用枚举方式，@see cookieType
//    @Deprecated
//    private Class<? extends ICookieType> cookieTypeClz;
    
    private ICookieType cookieType;

    //栓查token
    private boolean checkCookieFake = true;

    //当cookie没通过后，是否强制拦截
    private boolean strong = true;

    //生成cookie是否是服务端cookie,如果是，则客户端脚本不能读取，该项设置为true,可防止XSS
    private boolean httpOnly = true;

    public static Map<ICookieType, CookieGroup<?>> getCookieGroupMap() {
        return COOKIE_GROUP_MAP;
    }

    public static CookieGroup<?> getCookieGroup(ICookieType cookieType) {
        return COOKIE_GROUP_MAP.get(cookieType);
    }
    
    public static CookieGroup<?> getCookieGroup(Class<? extends ICookieType> cookieTypeClz) {
        if (CollectionUtil.isEmpty(COOKIE_GROUP_MAP)) {
            return null;
        }
        
        for (Entry<ICookieType, CookieGroup<?>> entry :COOKIE_GROUP_MAP.entrySet()){
            ICookieType key = entry.getKey();
            if (key.getRegisterClass()==cookieTypeClz) {
                return entry.getValue();
            }
        }
        return null;
    }


    public String getCookieNamePrefix() {
        if (_cookieNamePrefix == null) {
            _cookieNamePrefix = cookieType.getCookiePrefixName();
        }

        return _cookieNamePrefix;
    }

    public boolean startsWithPrefix(String cookieName) {
        return cookieName.startsWith(getCookieNamePrefix());
    }

    public CookiesHolder getCookiesHolder() {
        Map<String, CookiesHolder> valiCookieHolderMap = CollectionUtil.getOrCreateMapFormThreadLocal(
            ServletContextUtil.PREFIX_NAME_COOKIESHOLDERMAP_THREADLOCAL, ConcurrentHashMap.class).getValue();

        GetOrCreateWrap<CookiesHolder> getOrCreateWrap = CollectionUtil.getOrCreateObjectFromMap(getCookieNamePrefix(),
            valiCookieHolderMap, CookiesHolder.class);

        CookiesHolder cookiesHolder = getOrCreateWrap.getValue();
        if (getOrCreateWrap.isNew()) {
            cookiesHolder.setHttpOnly(httpOnly);
        }
        return cookiesHolder;
    }

    /**
    *
    * 过滤出不能被篡改的cookie.
    *
    * @param cookie the cookie
    * @return the cookie
    */
    public boolean filterRequestCookie(Cookie cookie) {
        String cookieName = cookie.getName();
        if (!startsWithPrefix(cookieName)) {
            return false;
        }

        CookiesHolder cookiesHolder = getCookiesHolder();
        if (cookieName.startsWith(_tokenCookieName)) {
            cookiesHolder.setTokenCookie(cookie);
            return true;
        }

        cookiesHolder.addRequestCookie(cookie);
        return true;
    }

    /**
     *
     * 校验 cookie 令牌是否被正确（不被篡改).
     *
     * @return the string
     */
    public boolean checkTokenIfNotWritResponse() {
        if (!checkCookieFake) {
            logger.warn(new StringBuilder("没有检查到:").append(getCookieNamePrefix()).append(" 的cookies").toString());
            return true;
        }
        boolean tokenPass = checkTokenEqual();
        //cookie伪造后，返回json
        if (!tokenPass) {
            String requestPath = RequestUtil.getRequestPath();
            if (strong) {
                //清空所有不正确的cookie
                this.expireAllCookies();
                if (logger.isDebugEnabled()) {
                    logger
                        .debug(new StringBuilder(requestPath).append("输出info信息: 请求被拦截:").append(requestPath).toString());
                }
                //验证没有通过，返回json对象
                IResponseStatus responseStatusOfTokenError = getResponseStatus();
                ResponseUtil.writhResponse(true,null, responseStatusOfTokenError);
                return false;
            } else {
                logger.warn(
                    new StringBuilder(requestPath).append("输出warn信息：cooke校验没有通过，但是由于不是强制拦截，本次请求不会被拦截，只打印日志").toString());
            }
        }
        return true;
    }

    public boolean checkTokenEqual() {
        boolean result = false;
        if (Configration.TOKEN_GENERATOR != null) {
            CookiesHolder cookiesHolder = this.getCookiesHolder();
            Map<String, Cookie> requestCookieMap = cookiesHolder.getCookieMap();

            String calcuToken = Configration.TOKEN_GENERATOR.genToken(requestCookieMap);
            Cookie tokenCookie = cookiesHolder.getTokenCookie();
            String cookieToken = null;
            if (calcuToken == null) {
                result = true;
            } else if (calcuToken != null) {
                if (tokenCookie != null) {
                    cookieToken = tokenCookie.getValue();
                    result = calcuToken.equals(cookieToken);
                }
            }

            if (logger.isDebugEnabled()) {
                String requestPath = RequestUtil.getRequestPath();
                logger.debug(
                    new StringBuilder("path:").append(requestPath).append(", token校验，用户 token:").append(cookieToken)
                        .append(", 计算token:").append(calcuToken).append("相等？").append(result).toString());
            }

        }
        return result;
    }

    private String getWholeCookieName(String name) {
        return new StringBuilder(getCookieNamePrefix()).append(name).toString();
    }

    private Cookie internalAddCookie(String name, Object value, Integer cookieAge) {
        CookiesHolder cookiesHolder = getCookiesHolder();
        Cookie addCookie = cookiesHolder.appendNormalCookie(getWholeCookieName(name), value, cookieAge);
        appendTokenCookie();
        return addCookie;
    }

    /***
     * 增加cookie前，对value进行url编码
     * 
     * @param name
     * @param value
     * @return
     */
    public Cookie addCookie(String name, Object value) {
        return internalAddCookie(name, value, this.secondsOfCookieAge);
    }

    public Cookie addCookie(E nameName, Object value) {
        return addCookie(nameName.toString(), value);
    }

    public Cookie deleteCookie(String name) {
        return internalAddCookie(name, null, -1);
    }

    public Cookie deleteCookie(E nameName) {
        return deleteCookie(nameName.toString());
    }

    /**
     * 创建cookie令牌的算法,调用 Configration.TOKEN_GENERATOR 设置的类生成
     * 生成令牌分为,当cookie中有需要不能被篡改的cookie时，生成令牌，当cookie中没有需要不被篡改的 cookie时，删除令牌
     */
    protected Cookie appendTokenCookie() {
        if (Configration.TOKEN_GENERATOR != null) {
            CookiesHolder cookiesHolder = getCookiesHolder();
            String calcuToken = Configration.TOKEN_GENERATOR.genToken(cookiesHolder.getCookieMap());
            Cookie tokenCookie = cookiesHolder.appendTokenCookie(this._tokenCookieName, calcuToken,
                this.secondsOfCookieAge);
            return tokenCookie;
        }
        return null;
    }

    /**
     * 该cookie在get后被解码
     * 
     * @param name
     * @return
     */
    public String getCookieValue(String name) {
        CookiesHolder cookiesHolder = this.getCookiesHolder();
        return cookiesHolder.readAndDecodeCookieValue(getWholeCookieName(name));
    }

    public String getCookieValue(E enumName) {
        return getCookieValue(enumName.name());
    }

    public void expireAllCookies() {
        if (checkCookieFake) {
            CookiesHolder cookiesHolder = this.getCookiesHolder();
            Map<String, Cookie> requestCookieMap = cookiesHolder.getCookieMap();
            if (CollectionUtil.isNotEmpty(requestCookieMap)) {
                List<Cookie> cookies = CollectionUtil.newArrayList(requestCookieMap.values());
                for (Cookie cookie : cookies) {
                    //删除,直接调用，没有触犯 appendTokenCookie
                    cookiesHolder.appendNormalCookie(cookie.getName(), null, -1);
                }
            }
            cookiesHolder.appendTokenCookie(_tokenCookieName, null, -1);
        }
    }

    public Cookie getCookie(String cookieName) {
        String valiCookieName = this.getWholeCookieName(cookieName);
        CookiesHolder cookiesHolder = this.getCookiesHolder();
        return cookiesHolder.getValiCookie(valiCookieName);
    }

    public Cookie getCookie(E enm) {
        return getCookie(enm.name());
    }

    /**
     * 不等于0 的数， 小于0，内存中的cookie,浏览器关闭，失效，大于0，按实际秒数存活
     * Sets the seconds of cookie age.
     *
     * @param secondsOfCookieAge the new seconds of cookie age
     */
    public void setSecondsOfCookieAge(Integer secondsOfCookieAge) {
        AssertUtil.mustTrue(secondsOfCookieAge != null && (secondsOfCookieAge > 0 | secondsOfCookieAge < 0),
            "secondsOfCookieAge must great or less than zero!");
        this.secondsOfCookieAge = secondsOfCookieAge;
    }

    public Integer getSecondsOfCookieAge() {
        return secondsOfCookieAge;
    }

    public void setCheckCookieFake(boolean checkCookieFake) {
        this.checkCookieFake = checkCookieFake;
    }

    public void setStrong(boolean strong) {
        this.strong = strong;
    }



    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    public <T extends Enum<T> & ICookieType> void setCookieType(T cookieType) {
        this.cookieType = cookieType;
    }
    
    
    public ICookieType getCookieType() {
        return cookieType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtil.mustTrue(cookieType!=null, "one of cookieTypeClz or cookieTypeEnum can not be null");
        

        CookieGroup<?> cookieGroup = COOKIE_GROUP_MAP.get(cookieType);
        AssertUtil.mustNull(cookieGroup, "cookieGroup has bean register:" + cookieType);

        setResponseStatus(cookieType.getResponseStatusOfTokenError());
        String cookiePrefixName = cookieType.getCookiePrefixName();
        
        
        for (CookieGroup<?> cg : COOKIE_GROUP_MAP.values()) {
            ICookieType cooType = cg.getCookieType();
            AssertUtil.mustFalse(cookiePrefixName.equals(cooType.getCookiePrefixName()),
                "prefix cookieType has bean used:" + cookiePrefixName);
        }

        COOKIE_GROUP_MAP.put(cookieType, this);
        _tokenCookieName = new StringBuilder(cookiePrefixName).append(TOKEN_COOKIE_NAME).toString();

        super.afterPropertiesSet();
    }

}
