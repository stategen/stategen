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

import java.util.Random;
/**
 * MockUtil只能用于测试，不能打包，执行 mvn package 由 插件 forbiddenapis 检测
 * @author niaoge
 * @version $Id: MockUtil.java, v 0.1 2021年1月7日 上午9:51:07 XiaZhengsheng Exp $
 */
public class MockUtil {
    /***模拟随机产生一个异常,不要在生产环境使用*/
    @Deprecated
    public static void throwRandomException(int seed) {
        Random rand = new Random();
        int randInt = rand.nextInt(seed)+1;
        AssertUtil.mustFalse(seed==randInt, String.format("这是一个随机模拟产生的错误码,机会为 1/%d",seed));
    }
    
    
    /***模拟随机产生一个异常,不要在生产环境使用*/
    @Deprecated
    public static void throwRandomException(int seed,String formatMsg) {
        Random rand = new Random();
        int randInt = rand.nextInt(seed)+1;
        AssertUtil.mustFalse(seed==randInt, String.format(formatMsg,seed));
    }

    
    /***线程休眠时间,不要在生产环境使用*/
    @Deprecated
    public static void slow(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //skip
        }
    }
    
    

}
