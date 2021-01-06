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

public class MockUtil {
    /***模拟随机产生一个异常,不要在生产环境使用*/
    @Deprecated
    public static void throwRandomException(int seed) {
        Random rand = new Random();
        int randInt = rand.nextInt(seed)+1;
        AssertUtil.mustFalse(seed==randInt, String.format("演示降级：当随机数为%d抛出异常",seed));
    }
    
    /***线程休眠时间,不要在生产环境使用*/
    @SuppressWarnings("static-access")
    @Deprecated
    public static void slow(long millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException e) {
            //skip
        }
    }
    
    

}
