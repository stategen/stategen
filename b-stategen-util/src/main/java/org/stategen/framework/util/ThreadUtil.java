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
package org.stategen.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ThreadUtil.
 * 简单的线程池，
 * @author Xia Zhengsheng
 * @version $Id: ThreadUtil.java, v 0.1 2016-12-26 14:30:50 Xia zhengsheng Exp $
 */
public class ThreadUtil {

    final static Logger logger = LoggerFactory.getLogger(ThreadUtil.class);
    public static ExecutorService es = null;

    
    public static ExecutorService getExecutorService() {
        if (es == null) {
            es = Executors.newCachedThreadPool();
        }
        return es;
    }


    @SuppressWarnings("unchecked")
    public static  <T> List<T> invokeAll(Collection<? extends Callable<T>> tasks){
        try {
            List<Future<T>> futures = getExecutorService().invokeAll(tasks);
            List<T> list = new ArrayList<T>();
            for (Future f: futures){
                try {
                    list.add((T) f.get());
                } catch (ExecutionException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            return list;
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
