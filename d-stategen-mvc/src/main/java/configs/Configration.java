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
package configs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.stategen.framework.util.NumberUtil;
import org.stategen.framework.web.cookie.CookieTokenGenerator;


/****
 * framework需要的一些配置，大部分统一在这个类下，这个类注册到 spring中，相关属性可以读 /data/config/dalgenX.xml中文件，以方便配置
 * 
 * @author XiaZhengsheng
 */
public class Configration {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Configration.class);
    
    public static CookieTokenGenerator TOKEN_GENERATOR = null;
    
    public static volatile boolean enableSwagger =false;
    
    public static volatile  String COOKIE_TOKEN_MIX="343f212b-2950-4852-b61c-34849e335afa";
    
//    public static volatile  String TOKEN_COOKIE_NAME="_token_";    
    
    //public static volatile String COOKIE_ANTI_FAKE_PREFIX="_ck_";
    
    public static volatile  Integer     MAX_REQUEST_PER_IP_SECOND = 20;
    
//    public static volatile  Boolean     CHECK_COOKIE_FAKE    = true;

//    public static volatile Boolean     STRONG             = true;
    
    public static volatile boolean     WRAPPER_REPONSE             = true;

    public static volatile Integer     COOKIE_DEFAULT_AGE =3600 * 24 * 30;

    public static Set<String> LOGIN_OUT_PATHS = new HashSet<String>(Arrays.asList("/user/login"));    
    
    public static Set<String> STATIC_RESOURCES = new HashSet<String>(Arrays.asList(".js" ,".png",".jpg","jpeg",".gif",".html",".ico",".css",".ttf",".woff",".woff2",".map"));    
    
//    private static Map<Class<? extends Annotation>, AnnotationChecker> annoCheckers =new ConcurrentHashMap<Class<? extends Annotation>, AnnotationChecker>();
//    
//    private static Map<Class<? extends Annotation>  ,FailResponse> failResonseImpls =new ConcurrentHashMap<Class<? extends Annotation>, FailResponse>();
    
    static {
//        new LoginCheckerDefault();
//        new CookieCheckerDefault();
////        new UserIdCheckerDefault();
//        
//        new LoginCheckFailResponseDefault();
////        new AuthorityCheckFailResponseDefault();
//        new CookieCheckFailResponseDefault();
//        new HandleErrorFailResponseDefault();
////        new TokenErrorFailResponseDefault();
//        new UserIdCheckFailResponseDefault();
    }
    
//    public static Map<Class<? extends Annotation>, AnnotationChecker> getAnnoCheckers(){
//        return annoCheckers;
//    }
//    
//    public static Map<Class<? extends Annotation>  ,FailResponse> getFailResonses(){
//        return failResonseImpls;
//    }
//    
//    public static <A extends Annotation> AnnotationChecker getAnnocationChecker(Class<A> annoType){
//        return annoCheckers.get(annoType);
//    }
//    
//    public static <A extends Annotation> void registAnnocationChecker(Class<A> annoType,AnnotationChecker annotationChecker){
//        annoCheckers.put(annoType, annotationChecker);
//    }
//    
//    
//    public static <A extends Annotation> FailResponse getFailResonse(Class<A> annoType){
//        return failResonseImpls.get(annoType);
//    }
//    
//    public static <A extends Annotation> void registFailResonse(Class<A> annoType,FailResponse failResponse){
//        failResonseImpls.put(annoType,failResponse);
//    }

    public void setMaxRequestPerIpSecond(Integer maxRequestPerIpSecond) {
        if (NumberUtil.isGreatZero(maxRequestPerIpSecond)) {
            Configration.MAX_REQUEST_PER_IP_SECOND = maxRequestPerIpSecond;
        }
    }

    public void setTokenAge(Integer tokenAge) {
        if (tokenAge <= 0) {
            logger.warn("输出warn信息： tokenAge设为<=0,设置不成功:" + tokenAge);
            return;
        }
        Configration.COOKIE_DEFAULT_AGE = tokenAge;
    }
    
    public void setEnableSwagger(boolean enableSwagger) {
        Configration.enableSwagger = enableSwagger;
    }
    
    public void setWrapperResponse(boolean wrapperResponse){
        WRAPPER_REPONSE =wrapperResponse;
    }
}
