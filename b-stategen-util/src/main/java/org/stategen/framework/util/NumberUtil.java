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

import java.math.BigDecimal;

/**
 * The Class NumberUtil.
 * 处理数字的工具类
 */
public class NumberUtil {
    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NumberUtil.class);

    /***不为空且不等于0*/
    public static boolean isNotZero(Number num) {
        return (null != num && num.doubleValue() != 0);
    }

    /***不为空且大于0*/
    public static boolean isGreatZero(Number num) {
        return (null != num && num.doubleValue() > 0);
    }

    /***不为空且大于等于0*/
    public static boolean isGreatOrEqualZero(Number num) {
        return (null != num && num.doubleValue() >= 0);
    }

    /***为空或小于等于0*/
    public static boolean isNullOrLessEqualZero(Number num) {
        return null == num || num.doubleValue() <= 0;
    }

    /***为空或小于0*/
    public static boolean isNullOrZero(Number num) {
        return null == num || num.doubleValue() == 0;
    }

    /***两于不为空且相等*/
    public static boolean isEqual(Number source, Number dest) {
        return (source != null && dest != null && source.doubleValue() == dest.doubleValue());
    }

    public static Long parseLong(String dest) {
        if (dest != null && dest.length() > 0) {
            try {
                return Long.parseLong(dest);
            } catch (NumberFormatException e) {
                logger.error(new StringBuffer("转换为Long时出错: e:").append(dest).toString());
            }
        }
        return null;
    }

    public static Integer parseInt(String dest) {
        if (dest != null && dest.length() > 0) {
            try {
                return Integer.parseInt(dest);
            } catch (NumberFormatException e) {
                logger.error(new StringBuffer("转换为Integer时出错: e:").append(dest).toString());
            }
        }
        return null;
    }

    //    public static int getPageNum(int offset,int limit){
    //        int pageNum =(limit!=0? (offset/limit):0)+1;
    //        int last =limit!=0? (offset % limit):0;
    //        if (last>0){
    //            pageNum++;
    //        }
    //        return pageNum;
    //    }
    public static int getPageNum(int offset, int limit) {
        limit = limit <= 0 ? 25 : limit;
        int pageNum = offset / limit + 1;
        int last    = offset % limit;
        if (last > 0) {
            pageNum++;
        }
        return pageNum;
    }

    public static int getPageNum1(int offset, int limit) {
        limit = limit <= 0 ? 25 : limit;
        double pageN   = ((double) (offset)) / limit;
        int    pageNum = (int) (Math.ceil(pageN)) + 1;
        return pageNum;
    }

    public static BigDecimal addNumbers(Number... sources) {
        BigDecimal result = new BigDecimal("0.00");
        if (CollectionUtil.isNotEmpty(sources)) {
            for (Number source : sources) {
                if (source != null) {
                    BigDecimal sourceBigDecimal = null;
                    if (source instanceof BigDecimal) {
                        sourceBigDecimal = (BigDecimal) source;
                    } else {
                        sourceBigDecimal = new BigDecimal(source.toString());
                    }
                    result = result.add(sourceBigDecimal);
                }
            }
        }
        return result;
    }

    public static BigDecimal multiplyNumbers(Number... sources) {
        BigDecimal result = null;
        if (CollectionUtil.isNotEmpty(sources)) {
            for (Number source : sources) {
                if (NumberUtil.isNullOrZero(source)) {
                    return BigDecimal.ZERO;
                }

                if (source != null) {
                    BigDecimal sourceBigDecimal = null;
                    if (source instanceof BigDecimal) {
                        sourceBigDecimal = (BigDecimal) source;

                    } else {
                        sourceBigDecimal = new BigDecimal(source.toString());
                    }
                    if (result == null) {
                        result = sourceBigDecimal;
                    } else {
                        result = result.multiply(sourceBigDecimal);
                    }
                }
            }
        }

        if (result == null) {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    public static Long add(Long v1, Long v2) {
        if (v1 == null) {
            return v2;
        }
        if (v2 == null) {
            return v1;
        }
        return v1 + v2;
    }

    public static Integer add(Integer v1, Integer v2) {
        if (v1 == null) {
            return v2;
        }
        if (v2 == null) {
            return v1;
        }
        return v1 + v2;
    }

    //    public static BigDecimal addBigDecimals(BigDecimal...sources){
    //        BigDecimal result =new BigDecimal("0.00");
    //        if (CollectionUtil.isNotEmpty(sources)){
    //            for (BigDecimal source : sources) {
    //                if (source!=null){
    //                    result =result.add(source);
    //                }
    //            }
    //        }
    //        return result;
    //    }

}
