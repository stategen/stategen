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
package org.stategen.framework.checker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.util.AssertUtil;

/**
 * The Class AbstractMethodChecker.
 *
 * @param <A> the generic type
 */
public abstract class AbstractMethodChecker<A extends Annotation> implements InitializingBean  {
    /**用HashMap,这个在运行后只读取，没有写入*/
    public static Map<Class<? extends Annotation>, AbstractMethodChecker<?>> CHECKER_CACHE =new HashMap<Class<? extends Annotation>, AbstractMethodChecker<?>>();
    
    public abstract <T extends Enum<T> & IResponseStatus> T doCheck(Method method ,A checkAnno, Class<? extends IResponseStatus> defaultResponseStatusTypeClzOfCheckFail);
    
    public abstract Class<A> getCheckAnnoClz();
    
    public Class<? extends IResponseStatus> getResponseStatusTypeClzOfNoPassed(){
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Class<A> checkAnnoClz = getCheckAnnoClz();
        
        AssertUtil.mustNotNull(checkAnnoClz);
        
        //注册自己
        CHECKER_CACHE.put(checkAnnoClz, this);
    }

    @SuppressWarnings("unchecked")
    public static  AbstractMethodChecker<Annotation> getChecker(Class<? extends Annotation> checkAnnoClz) {
        AbstractMethodChecker<Annotation> abstractMethodChecker = (AbstractMethodChecker<Annotation>) CHECKER_CACHE.get(checkAnnoClz);
        return abstractMethodChecker;
    }
    
}
