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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 * The Class DatetimeUtil.
 * 用joda-time实现Date常用工具包的功能
 */
public class DatetimeUtil {

    /**
     *  <pre>   yyyy-MM-dd  </pre>.
     */
    public final static String DATE_FORMAT          = "yyyy-MM-dd";

    /**
     *  <pre>   yyyy年MM月dd日  </pre>.
     */
    public final static String DATE_FORMAT_CN       = "yyyy年MM月dd日";

    /**
     *  <pre>   yyyy-MM-dd HH:mm:ss  </pre>.
     */
    public final static String TIME_FORMAT          = "yyyy-MM-dd HH:mm:ss";

    /**
     *  <pre>   yyyy年MM月dd日 HH:mm:ss  </pre>.
     */
    public final static String TIME_FORMAT_CN       = "yyyy年MM月dd日 HH:mm:ss";

    /**
     *  <pre>   yyyy-MM-dd HH:mm:ss SSS  </pre>
     */
    public final static String MILLS_FORMAT         = "yyyy-MM-dd HH:mm:ss SSS";
    /**
     *  <pre>   yyyy年MM月dd日 HH:mm:ss SSS  </pre>.
     */
    public final static String MILLS_FORMAT_CN      = "yyyy年MM月dd日 HH:mm:ss SSS";

    /**
     *  <pre>   yyyy-MM </pre>.
     */
    public final static String MONTH_FORMAT         = "yyyy-MM";

    /**
     *  <pre>   yyyyMMdd  </pre>.
     */
    public final static String DAY_FORMAT           = "yyyyMMdd";

    /**
     *  <pre>   yyyy-MM-dd HH时 </pre>
     */
    public final static String HOUR_FORMAT          = "yyyy-MM-dd HH时";

    /**
     *  一天的毫秒数 1000*60*60*24 =86400000*.
     */
    public final static int    TIME_DAY_MILLISECONDS = 86400000;
    
    /**
     *  一天的毫秒数 60*60*24=86400 *.
     */
    public final static int    TIME_DAY_SECONDS = 86400;
    public final static int    TIME_MONTH_SECONDS = 86400*30;

    /**
     * The Enum DateType.
     *
     * @version $Id: DatetimeUtil.java, v 0.1 2016-1-18 15:47:05 xiazhengsheng Exp $
     */
    protected enum DateType {

        /**
         *  <pre>  * The year.  </pre>
         */
        YEAR,
        /**
         *  <pre>  * The month.  </pre>
         */
        MONTH,
        /**
         *  <pre>  * The week.  </pre>
         */
        WEEK,
        /**
         *  <pre>  * The day.  </pre>
         */
        DAY,
        /**
         *  <pre>  * The hour.  </pre>
         */
        HOUR,
        /**
         *  <pre>  * The startutes.  </pre>
         */
        MINUTE,
        /**
         *  <pre>  * The second.  </pre>
         */
        SECOND,
        /**
         *  <pre>  * The mills.  </pre>
         */
        MILLS

    }

    /**
     * Format.
     *
     * @param date the date
     * @param format the format
     * @return the string
     */
    public static String format(Date date, String format) {
        DateFormat dt = new SimpleDateFormat(format);
        return dt.format(date);
    }

    /**
     * yyyy-MM-dd.
     *
     * @param date the date
     * @return the string
     */
    public static String format(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * yyyy年MM月dd日.
     *
     * @param date the date
     * @return the string
     */
    public static String formatCn(Date date) {
        return format(date, DATE_FORMAT_CN);
    }

    /**
     * yyyy-MM-dd HH:mm:ss.
     *
     * @param date the date
     * @return the string
     */
    public static String formatTime(Date date) {
        return format(date, TIME_FORMAT);
    }

    /**
     * yyyy年MM月dd日 HH:mm:ss  .
     *
     * @param date the date
     * @return the string
     */
    public static String formatTimeCn(Date date) {
        return format(date, TIME_FORMAT_CN);
    }

    /**
     * yyyy-MM-dd HH:mm:ss SSS.
     *
     * @param date the date
     * @return the string
     */
    public static String formatMills(Date date) {
        return format(date, MILLS_FORMAT);
    }

    /**
     * yyyy年MM月dd日 HH:mm:ss SSS .
     *
     * @param date the date
     * @return the string
     */
    public static String formatMillsCN(Date date) {
        return format(date, MILLS_FORMAT_CN);
    }

    /**
     * yyyy-MM.
     *
     * @param date the date
     * @return the string
     */
    public static String formatMonth(Date date) {
        return format(date, MONTH_FORMAT);
    }

    /**
     * yyyyMMdd.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDay(Date date) {
        return format(date, DAY_FORMAT);
    }

    /**
     * yyyy-MM-dd HH时.
     *
     * @param date the date
     * @return the string
     */
    public static String formatHour(Date date) {
        return format(date, HOUR_FORMAT);
    }

    /**
     * Current.
     *
     * @return the date
     */
    public static Date current() {
        return currentDateTime().toDate();
    }

    /**
     * Current date time.
     *
     * @return the date time
     */
    protected static DateTime currentDateTime() {
        return new DateTime();
    }

    /**
     * Gets the start datetime.
     *
     * @param curr the curr
     * @param type the type
     * @return the start datetime
     */
    protected static DateTime startDateTime(DateTime curr, DateType type) {
        DateTime result = curr;
        Property pr = null;
        if (DateType.YEAR.equals(type)) {
            pr = curr.dayOfYear();
            result = pr.withMinimumValue().withTimeAtStartOfDay();
        } else if (DateType.MONTH.equals(type)) {
            pr = curr.dayOfMonth();
            result = pr.withMinimumValue().withTimeAtStartOfDay();
        } else if (DateType.WEEK.equals(type)) {
            pr = curr.dayOfWeek();
            result = pr.withMinimumValue().withTimeAtStartOfDay();
        } else if (DateType.DAY.equals(type)) {
            pr = curr.hourOfDay();
            result = pr.withMinimumValue().withTimeAtStartOfDay();
        } else if (DateType.HOUR.equals(type)) {
            pr = curr.minuteOfHour();
            result = pr.withMinimumValue().withMinuteOfHour(0).withSecondOfMinute(0)
                .withMillisOfSecond(0);
        } else if (DateType.MINUTE.equals(type)) {
            pr = curr.secondOfMinute();
            result = pr.withMinimumValue().withSecondOfMinute(0).withMillisOfSecond(0);
        } else if (DateType.SECOND.equals(type)) {
            pr = curr.millisOfSecond();
            result = pr.withMinimumValue().withMillisOfSecond(0);
        }
        return result;
    }

    /**
     * Gets the interval date time.
     *
     * @param curr the curr
     * @param type the type
     * @param interval the interval
     * @return the interval date time
     */
    protected static DateTime plusDateTime(DateTime curr, DateType type, int interval) {
        DateTime result = curr;
        if (DateType.YEAR.equals(type)) {
            result = curr.plusYears(interval);
        } else if (DateType.MONTH.equals(type)) {
            result = curr.plusMonths(interval);
        } else if (DateType.WEEK.equals(type)) {
            result = curr.plusWeeks(interval);
        } else if (DateType.DAY.equals(type)) {
            result = curr.plusDays(interval);
        } else if (DateType.HOUR.equals(type)) {
            result = curr.plusHours(interval);
        } else if (DateType.MINUTE.equals(type)) {
            result = curr.plusMinutes(interval);
        } else if (DateType.SECOND.equals(type)) {
            result = curr.plusSeconds(interval);
        }
        return result;
    }

    protected static DateTime plusDateTime(Date curr, DateType type, int interval) {
        DateTime dateTime = new DateTime(curr);
        return plusDateTime(dateTime, type, interval);
    }

    /**
     * Gets the start year datetime.
     *
     * @param curr the curr
     * @return the start year datetime
     */
    protected static DateTime startYearDateTime(DateTime curr) {
        return startDateTime(curr, DateType.YEAR);
    }

    /**
     * Gets the start month datetime.
     *
     * @param curr the curr
     * @return the start month datetime
     */
    protected static DateTime startMonthDateTime(DateTime curr) {
        return startDateTime(curr, DateType.MONTH);
    }

    /**
     * Gets the start week datetime.
     *
     * @param curr the curr
     * @return the start week datetime
     */
    protected static DateTime startWeekDateTime(DateTime curr) {
        return startDateTime(curr, DateType.WEEK);
    }

    /**
     * Gets the start day datetime.
     *
     * @param curr the curr
     * @return the start day datetime
     */
    protected static DateTime startDayDateTime(DateTime curr) {
        return startDateTime(curr, DateType.DAY);
    }

    /**
     * Gets the start hour datetime.
     *
     * @param curr the curr
     * @return the start hour datetime
     */
    protected static DateTime startHourDateTime(DateTime curr) {
        return startDateTime(curr, DateType.HOUR);
    }

    /**
     * Gets the start startutes datetime.
     *
     * @param curr the curr
     * @return the start startutes datetime
     */
    protected static DateTime startMinuteDateTime(DateTime curr) {
        return startDateTime(curr, DateType.MINUTE);
    }

    /**
     * Gets the start second datetime.
     *
     * @param curr the curr
     * @return the start second datetime
     */
    protected static DateTime startSecondDateTime(DateTime curr) {
        return startDateTime(curr, DateType.SECOND);
    }

    /**
     * Start year datetime.
     *
     * @return the date time
     */
    protected static DateTime startYearDateTime() {
        return startYearDateTime(currentDateTime());
    }

    /**
     * Start month datetime.
     *
     * @return the date time
     */
    protected static DateTime startMonthDateTime() {
        return startMonthDateTime(currentDateTime());
    }

    /**
     * Start week datetime.
     *
     * @return the date time
     */
    protected static DateTime startWeekDateTime() {
        return startWeekDateTime(currentDateTime());
    }

    /**
     * Start day datetime.
     *
     * @return the date time
     */
    protected static DateTime startDayDateTime() {
        return startDayDateTime(currentDateTime());
    }

    /**
     * Start hour datetime.
     *
     * @return the date time
     */
    protected static DateTime startHourDateTime() {
        return startHourDateTime(currentDateTime());
    }

    /**
     * Start startutes datetime.
     *
     * @return the date time
     */
    protected static DateTime startMinuteDateTime() {
        return startMinuteDateTime(currentDateTime());
    }

    /**
     * Start second datetime.
     *
     * @return the date time
     */
    protected static DateTime startSecondDateTime() {
        return startSecondDateTime(currentDateTime());
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 年起始:2016年01月01日 00:00:00 000.
     *
     * @return the date
     */
    protected static Date startYear() {
        return startYearDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 月起始:2016年02月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startMonth() {
        return startMonthDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 周起始:2016年02月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startWeek() {
        return startWeekDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 天起始:2016年02月04日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startDay() {
        return startDayDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 时起始:2016年02月04日 13:00:00 000.
     *
     * @return the date
     */
    public static Date startHour() {
        return startHourDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 分起始:2016年02月04日 13:55:00 000.
     *
     * @return the date
     */
    public static Date startMinute() {
        return startMinuteDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 秒起始:2016年02月04日 13:55:21 000.
     *
     * @return the date
     */
    public static Date startSecond() {
        return startSecondDateTime().toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 年起始:2016年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the start year
     */
    public static Date startYear(Date curr) {
        DateTime dest = new DateTime(curr);
        return startYearDateTime(dest).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     *         月起始:2016年02月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the start month
     */
    public static Date startMonth(Date curr) {
        DateTime dest = new DateTime(curr);
        return startMonthDateTime(dest).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     *         周起始:2016年02月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the start week
     */
    public static Date startWeek(Date curr) {
        DateTime dest = new DateTime(curr);
        return startWeekDateTime(dest).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     *         天起始:2016年02月04日 00:00:00 000.
     *
     * @param curr the curr
     * @return the start day
     */
    public static Date startDay(Date curr) {
        DateTime dest = new DateTime(curr);
        return startDayDateTime(dest).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     *         时起始:2016年02月04日 13:00:00 000.
     *
     * @param curr the curr
     * @return the start hour
     */
    public static Date startHour(Date curr) {
        DateTime dest = new DateTime(curr);
        return startHourDateTime(dest).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     *         分起始:2016年02月04日 13:55:00 000.
     *
     * @param curr the curr
     * @return the start startutes
     */
    public static Date startMinute(Date curr) {
        DateTime dest = new DateTime(curr);
        return startMinuteDateTime(dest).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     *         秒起始:2016年02月04日 13:55:21 000.
     *
     * @param curr the curr
     * @return the start second
     */
    public static Date startSecond(Date curr) {
        DateTime dest = new DateTime(curr);
        return startSecondDateTime(dest).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2年:2018年02月04日 13:55:21 111
     * 则 -3年:2013年02月04日 13:55:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval year
     */
    public static Date plusYear(Date curr, int interval) {
        return plusDateTime(curr, DateType.YEAR, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2月:2016年04月04日 13:55:21 111
     * 则 -3月:2015年11月04日 13:55:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval month
     */
    public static Date plusMonth(Date curr, int interval) {
        return plusDateTime(curr, DateType.MONTH, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2周:2016年02月18日 13:55:21 111
     * 则 -3周:2016年01月14日 13:55:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval week
     */
    public static Date plusWeek(Date curr, int interval) {
        return plusDateTime(curr, DateType.WEEK, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2天:2016年02月06日 13:55:21 111
     * 则 -3天:2016年02月01日 13:55:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval day
     */
    public static Date plusDay(Date curr, int interval) {
        return plusDateTime(curr, DateType.DAY, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2时:2016年02月04日 15:55:21 111
     * 则 -3时:2016年02月04日 10:55:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval hour
     */
    public static Date plusHour(Date curr, int interval) {
        return plusDateTime(curr, DateType.HOUR, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2分:2016年02月04日 13:57:21 111
     * 则 -3分:2016年02月04日 13:52:21 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval startutes
     */
    public static Date plusMinute(Date curr, int interval) {
        return plusDateTime(curr, DateType.MINUTE, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2秒:2016年02月04日 13:55:23 111
     * 则 -3秒:2016年02月04日 13:55:18 111.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the interval second
     */
    public static Date plusSecond(Date curr, int interval) {
        return plusDateTime(curr, DateType.SECOND, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2年:2018年02月04日 13:55:21 111
     * 则 -3年:2013年02月04日 13:55:21 111.
     *
     * @param interval the interval
     * @return the curr interval year
     */
    public static Date plusYear(int interval) {
        return plusDateTime(currentDateTime(), DateType.YEAR, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2月:2016年04月04日 13:55:21 111
     * 则 -3月:2015年11月04日 13:55:21 111.
     *
     * @param interval the interval
     * @return the curr interval month
     */
    public static Date plusMonth(int interval) {
        return plusDateTime(currentDateTime(), DateType.MONTH, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2周:2016年02月18日 13:55:21 111
     * 则 -3周:2016年01月14日 13:55:21 111.
     *
     * @param interval the interval
     * @return the curr interval week
     */
    public static Date plusWeek(int interval) {
        return plusDateTime(currentDateTime(), DateType.WEEK, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2天:2016年02月06日 13:55:21 111
     * 则 -3天:2016年02月01日 13:55:21 111.
     *
     * @param interval the interval
     * @return the curr interval day
     */
    public static Date plusDay(int interval) {
        return plusDateTime(currentDateTime(), DateType.DAY, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2时:2016年02月04日 15:55:21 111
     * 则 -3时:2016年02月04日 10:55:21 111.
     *
     * @param interval the interval
     * @return the curr interval hour
     */
    public static Date plusHour(int interval) {
        return plusDateTime(currentDateTime(), DateType.HOUR, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2分:2016年02月04日 13:57:21 111
     * 则 -3分:2016年02月04日 13:52:21 111.
     *
     * @param interval the interval
     * @return the curr interval startutes
     */
    public static Date plusMinute(int interval) {
        return plusDateTime(currentDateTime(), DateType.MINUTE, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 则 +2秒:2016年02月04日 13:55:23 111
     * 则 -3秒:2016年02月04日 13:55:18 111.
     *
     * @param interval the interval
     * @return the curr interval second
     */
    public static Date plusSecond(int interval) {
        return plusDateTime(currentDateTime(), DateType.SECOND, interval).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一年:2017年02月04日 13:55:21 111.
     *
     * @return the date
     */
    public static Date nextYear() {
        return plusDateTime(currentDateTime(), DateType.YEAR, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一月:2016年03月04日 13:55:21 111.
     *
     * @return the date
     */
    public static Date nextMonth() {
        return plusDateTime(currentDateTime(), DateType.MONTH, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一周:2016年02月11日 13:55:21 111.
     *
     * @return the date
     */
    public static Date nextWeek() {
        return plusDateTime(currentDateTime(), DateType.WEEK, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一天:2016年02月05日 13:55:21 111.
     *
     * @return the date
     */
    public static Date nextDay() {
        return plusDateTime(currentDateTime(), DateType.DAY, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一时:2016年02月04日 14:55:21 111.
     *
     * @return the date
     */
    public static Date nextlHour() {
        return plusDateTime(currentDateTime(), DateType.HOUR, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一分:2016年02月04日 13:56:21 111.
     *
     * @return the date
     */
    public static Date nextMinute() {
        return plusDateTime(currentDateTime(), DateType.MINUTE, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一秒:2016年02月04日 13:55:22 111.
     *
     * @return the date
     */
    public static Date nextSecond() {
        return plusDateTime(currentDateTime(), DateType.SECOND, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一年:2017年02月04日 13:55:21 111.
     *
     * @param curr the curr
     * @return the next year
     */
    public static Date nextYear(Date curr) {
        return plusDateTime(curr, DateType.YEAR, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一月:2016年03月04日 13:55:21 111.
     *
     * @param curr the curr
     * @return the next month
     */
    public static Date nextMonth(Date curr) {
        return plusDateTime(curr, DateType.MONTH, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一周:2016年02月11日 13:55:21 111.
     *
     * @param curr the curr
     * @return the next week
     */
    public static Date nextWeek(Date curr) {
        return plusDateTime(curr, DateType.WEEK, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一天:2016年02月05日 13:55:21 111.
     *
     * @param curr the curr
     * @return the next day
     */
    public static Date nextDay(Date curr) {
        return plusDateTime(curr, DateType.DAY, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一时:2016年02月04日 14:55:21 111.
     *
     * @param curr the curr
     * @return the next hour
     */
    public static Date nextHour(Date curr) {
        return plusDateTime(curr, DateType.HOUR, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一分:2016年02月04日 13:56:21 111.
     *
     * @param curr the curr
     * @return the next startutes
     */
    public static Date nextMinute(Date curr) {
        return plusDateTime(curr, DateType.MINUTE, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一秒:2016年02月04日 13:55:22 111.
     *
     * @param curr the curr
     * @return the next second
     */
    public static Date nextSecond(Date curr) {
        return plusDateTime(curr, DateType.SECOND, 1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一年:2017年02月04日 13:55:21 111.
     *
     * @return the date
     */
    public static Date prevYear() {
        return plusDateTime(currentDateTime(), DateType.YEAR, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一月:2016年03月04日 13:55:21 111.
     *
     * @return the date
     */
    public static Date prevtMonth() {
        return plusDateTime(currentDateTime(), DateType.MONTH, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一周:2016年02月11日 13:55:21 111.
     *
     * @return the date
     */
    public static Date prevWeek() {
        return plusDateTime(currentDateTime(), DateType.WEEK, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一天:2016年02月05日 13:55:21 111.
     *
     * @return the date
     */
    public static Date prevDay() {
        return plusDateTime(currentDateTime(), DateType.DAY, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一时:2016年02月04日 14:55:21 111.
     *
     * @return the date
     */
    public static Date prevHour() {
        return plusDateTime(currentDateTime(), DateType.HOUR, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一分:2016年02月04日 13:56:21 111.
     *
     * @return the date
     */
    public static Date prevMinute() {
        return plusDateTime(currentDateTime(), DateType.MINUTE, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一秒:2016年02月04日 13:55:22 111.
     *
     * @return the date
     */
    public static Date prevSecond() {
        return plusDateTime(currentDateTime(), DateType.SECOND, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一年:2017年02月04日 13:55:21 111.
     *
     * @param curr the curr
     * @return the prev year
     */
    public static Date prevYear(Date curr) {
        return plusDateTime(curr, DateType.YEAR, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一月:2016年03月04日 13:55:21 111.
     *
     * @param curr the curr
     * @return the prev month
     */
    public static Date prevMonth(Date curr) {
        return plusDateTime(curr, DateType.MONTH, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一周:2016年02月11日 13:55:21 111.
     *
     * @param curr the curr
     * @return the prev week
     */
    public static Date prevWeek(Date curr) {
        return plusDateTime(curr, DateType.WEEK, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一天:2016年02月05日 13:55:21 111.
     *
     * @param curr the curr
     * @return the prev day
     */
    public static Date prevDay(Date curr) {
        return plusDateTime(curr, DateType.DAY, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一时:2016年02月04日 14:55:21 111.
     *
     * @param curr the curr
     * @return the prev hour
     */
    public static Date prevHour(Date curr) {
        return plusDateTime(curr, DateType.HOUR, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一分:2016年02月04日 13:56:21 111.
     *
     * @param curr the curr
     * @return the prev startutes
     */
    public static Date prevMinute(Date curr) {
        return plusDateTime(curr, DateType.MINUTE, -1).toDate();
    }

    /**
     * 设时间:2016年02月04日 13:55:21 111
     * 下一秒:2016年02月04日 13:55:22 111.
     *
     * @param curr the curr
     * @return the prev second
     */
    public static Date prevSecond(Date curr) {
        return plusDateTime(curr, DateType.SECOND, -1).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一年起始:2017年01月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfNextYear() {
        return startYearDateTime(plusDateTime(currentDateTime(), DateType.YEAR, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一月起始:2016年03月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfNextMonth() {
        return startMonthDateTime(plusDateTime(currentDateTime(), DateType.MONTH, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一周起始:2016年02月08日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfNextWeek() {
        return startWeekDateTime(plusDateTime(currentDateTime(), DateType.WEEK, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一天起始:2016年02月05日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfNextDay() {
        return startDayDateTime(plusDateTime(currentDateTime(), DateType.DAY, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一时起始:2016年02月04日 14:00:00 000.
     *
     * @return the date
     */
    public static Date startOfNextlHour() {
        return startHourDateTime(plusDateTime(currentDateTime(), DateType.HOUR, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一分起始:2016年02月04日 13:56:00 000.
     *
     * @return the date
     */
    public static Date startOfNextMinute() {
        return startMinuteDateTime(plusDateTime(currentDateTime(), DateType.MINUTE, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一秒起始:2016年02月04日 13:55:22 000.
     *
     * @return the date
     */
    public static Date startOfNextSecond() {
        return startSecondDateTime(plusDateTime(currentDateTime(), DateType.SECOND, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一年起始:2017年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the next start year
     */
    public static Date startOfNextYear(Date curr) {
        return startYearDateTime(plusDateTime(curr, DateType.YEAR, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一月起始:2016年03月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the next start month
     */
    public static Date startOfNextMonth(Date curr) {
        return startMonthDateTime(plusDateTime(curr, DateType.MONTH, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一周起始:2016年02月08日 00:00:00 000.
     *
     * @param curr the curr
     * @return the next start week
     */
    public static Date startOfNextWeek(Date curr) {
        return startWeekDateTime(plusDateTime(curr, DateType.WEEK, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一天起始:2016年02月05日 00:00:00 000.
     *
     * @param curr the curr
     * @return the next start day
     */
    public static Date startOfNextDay(Date curr) {
        return startDayDateTime(plusDateTime(curr, DateType.DAY, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一时起始:2016年02月04日 14:00:00 000.
     *
     * @param curr the curr
     * @return the next start hour
     */
    public static Date startOfNextHour(Date curr) {
        return startHourDateTime(plusDateTime(curr, DateType.HOUR, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一分起始:2016年02月04日 13:56:00 000.
     *
     * @param curr the curr
     * @return the next start startutes
     */
    public static Date startOfNextMinute(Date curr) {
        return startMinuteDateTime(plusDateTime(curr, DateType.MINUTE, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 下一秒起始:2016年02月04日 13:55:22 000.
     *
     * @param curr the curr
     * @return the next start second
     */
    public static Date startOfNextSecond(Date curr) {
        return startSecondDateTime(plusDateTime(curr, DateType.SECOND, 1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一年起始:2015年01月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfPrevYear() {
        return startYearDateTime(plusDateTime(currentDateTime(), DateType.YEAR, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一月起始:2016年01月01日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfPrevMonth() {
        return startMonthDateTime(plusDateTime(currentDateTime(), DateType.MONTH, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一周起始:2016年01月25日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfPrevWeek() {
        return startWeekDateTime(plusDateTime(currentDateTime(), DateType.WEEK, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一天起始:2016年02月03日 00:00:00 000.
     *
     * @return the date
     */
    public static Date startOfPprevDay() {
        return startDayDateTime(plusDateTime(currentDateTime(), DateType.DAY, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一时起始:2016年02月04日 12:00:00 000.
     *
     * @return the date
     */
    public static Date startOfPprevlHour() {
        return startHourDateTime(plusDateTime(currentDateTime(), DateType.HOUR, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一分起始:2016年02月04日 13:54:00 000.
     *
     * @return the date
     */
    public static Date startOfPrevMinute() {
        return startMinuteDateTime(plusDateTime(currentDateTime(), DateType.MINUTE, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一秒起始:2016年02月04日 13:55:20 000.
     *
     * @return the date
     */
    public static Date startOfPrevSecond() {
        return startSecondDateTime(plusDateTime(currentDateTime(), DateType.SECOND, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一年起始:2015年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the prev start year
     */
    public static Date startOfPrevYear(Date curr) {
        return startYearDateTime(plusDateTime(curr, DateType.YEAR, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一月起始:2016年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @return the prev start month
     */
    public static Date startOfPrevMonth(Date curr) {
        return startMonthDateTime(plusDateTime(curr, DateType.MONTH, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一周起始:2016年01月25日 00:00:00 000.
     *
     * @param curr the curr
     * @return the prev start week
     */
    public static Date startOfPrevWeek(Date curr) {
        return startWeekDateTime(plusDateTime(curr, DateType.WEEK, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一天起始:2016年02月03日 00:00:00 000.
     *
     * @param curr the curr
     * @return the prev start day
     */
    public static Date startOfPrevDay(Date curr) {
        return startDayDateTime(plusDateTime(curr, DateType.DAY, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一时起始:2016年02月04日 12:00:00 000.
     *
     * @param curr the curr
     * @return the prev start hour
     */
    public static Date startOfPrevHour(Date curr) {
        return startHourDateTime(plusDateTime(curr, DateType.HOUR, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一分起始:2016年02月04日 13:54:00 000.
     *
     * @param curr the curr
     * @return the prev start startutes
     */
    public static Date startOfPrevMinute(Date curr) {
        return startMinuteDateTime(plusDateTime(curr, DateType.MINUTE, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 上一秒起始:2016年02月04日 13:55:20 000.
     *
     * @param curr the curr
     * @return the prev start second
     */
    public static Date startOfPrevSecond(Date curr) {
        return startSecondDateTime(plusDateTime(curr, DateType.SECOND, -1)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2年始:2018年01月01日 00:00:00 000
     * 则：-3年始:2013年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextYear(Date curr, int interval) {
        return startYearDateTime(plusDateTime(curr, DateType.YEAR, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2月始:2016年04月01日 00:00:00 000
     * 则：-3月始:2015年11月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextMonth(Date curr, int interval) {
        return startMonthDateTime(plusDateTime(curr, DateType.MONTH, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2周始:2016年02月15日 00:00:00 000
     * 则：-3周始:2016年01月11日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextWeek(Date curr, int interval) {
        return startWeekDateTime(plusDateTime(curr, DateType.WEEK, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2天始:2016年02月06日 00:00:00 000
     * 则：-3天始:2016年02月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextDay(Date curr, int interval) {
        return startDayDateTime(plusDateTime(curr, DateType.DAY, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2时始:2016年02月04日 15:00:00 000
     * 则：-3时始:2016年02月04日 10:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextHour(Date curr, int interval) {
        return startHourDateTime(plusDateTime(curr, DateType.HOUR, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2分始:2016年02月04日 13:57:00 000
     * 则：-3分始:2016年02月04日 13:52:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextMinute(Date curr, int interval) {
        return startMinuteDateTime(plusDateTime(curr, DateType.MINUTE, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2秒始:2016年02月04日 13:55:23 000
     * 则：-3秒始:2016年02月04日 13:55:18 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextSecond(Date curr, int interval) {
        return startSecondDateTime(plusDateTime(curr, DateType.SECOND, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2年始:2018年01月01日 00:00:00 000
     * 则：-3年始:2013年01月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextYear(int interval) {
        return startYearDateTime(plusDateTime(currentDateTime(), DateType.YEAR, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2月始:2016年04月01日 00:00:00 000
     * 则：-3月始:2015年11月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date StartOfPlusMonth(int interval) {
        return startMonthDateTime(plusDateTime(currentDateTime(), DateType.MONTH, interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2周始:2016年02月15日 00:00:00 000
     * 则：-3周始:2016年01月11日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextWeek(int interval) {
        return startWeekDateTime(plusDateTime(currentDateTime(), DateType.WEEK, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2天始:2016年02月06日 00:00:00 000
     * 则：-3天始:2016年02月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextDay(int interval) {
        return startDayDateTime(plusDateTime(currentDateTime(), DateType.DAY, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2时始:2016年02月04日 15:00:00 000
     * 则：-3时始:2016年02月04日 10:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextHour(int interval) {
        return startHourDateTime(plusDateTime(currentDateTime(), DateType.HOUR, interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2分始:2016年02月04日 13:57:00 000
     * 则：-3分始:2016年02月04日 13:52:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextMinute(int interval) {
        return startMinuteDateTime(plusDateTime(currentDateTime(), DateType.MINUTE, interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2秒始:2016年02月04日 13:55:23 000
     * 则：-3秒始:2016年02月04日 13:55:18 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfNextSecond(int interval) {
        return startSecondDateTime(plusDateTime(currentDateTime(), DateType.SECOND, interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2年始:2018年01月01日 00:00:00 000
     * 则：-3年始:2013年01月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevYear(Date curr, int interval) {
        return startYearDateTime(plusDateTime(curr, DateType.YEAR, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2月始:2016年04月01日 00:00:00 000
     * 则：-3月始:2015年11月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevMonth(Date curr, int interval) {
        return startMonthDateTime(plusDateTime(curr, DateType.MONTH, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2周始:2016年02月15日 00:00:00 000
     * 则：-3周始:2016年01月11日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevWeek(Date curr, int interval) {
        return startWeekDateTime(plusDateTime(curr, DateType.WEEK, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2天始:2016年02月06日 00:00:00 000
     * 则：-3天始:2016年02月01日 00:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevDay(Date curr, int interval) {
        return startDayDateTime(plusDateTime(curr, DateType.DAY, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2时始:2016年02月04日 15:00:00 000
     * 则：-3时始:2016年02月04日 10:00:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevHour(Date curr, int interval) {
        return startHourDateTime(plusDateTime(curr, DateType.HOUR, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2分始:2016年02月04日 13:57:00 000
     * 则：-3分始:2016年02月04日 13:52:00 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevMinute(Date curr, int interval) {
        return startMinuteDateTime(plusDateTime(curr, DateType.MINUTE, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2秒始:2016年02月04日 13:55:23 000
     * 则：-3秒始:2016年02月04日 13:55:18 000.
     *
     * @param curr the curr
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevSecond(Date curr, int interval) {
        return startSecondDateTime(plusDateTime(curr, DateType.SECOND, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2年始:2018年01月01日 00:00:00 000
     * 则：-3年始:2013年01月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevYear(int interval) {
        return startYearDateTime(plusDateTime(currentDateTime(), DateType.YEAR, -interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2月始:2016年04月01日 00:00:00 000
     * 则：-3月始:2015年11月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date StartOfPrevMonth(int interval) {
        return startMonthDateTime(plusDateTime(currentDateTime(), DateType.MONTH, -interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2周始:2016年02月15日 00:00:00 000
     * 则：-3周始:2016年01月11日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevWeek(int interval) {
        return startWeekDateTime(plusDateTime(currentDateTime(), DateType.WEEK, -interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2天始:2016年02月06日 00:00:00 000
     * 则：-3天始:2016年02月01日 00:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevDay(int interval) {
        return startDayDateTime(plusDateTime(currentDateTime(), DateType.DAY, -interval)).toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2时始:2016年02月04日 15:00:00 000
     * 则：-3时始:2016年02月04日 10:00:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevHour(int interval) {
        return startHourDateTime(plusDateTime(currentDateTime(), DateType.HOUR, -interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2分始:2016年02月04日 13:57:00 000
     * 则：-3分始:2016年02月04日 13:52:00 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevMinute(int interval) {
        return startMinuteDateTime(plusDateTime(currentDateTime(), DateType.MINUTE, -interval))
            .toDate();
    }

    /**
     * 假设时间:2016年02月04日 13:55:21 111
     * 则：+2秒始:2016年02月04日 13:55:23 000
     * 则：-3秒始:2016年02月04日 13:55:18 000.
     *
     * @param interval the interval
     * @return the date
     */
    public static Date startOfPrevSecond(int interval) {
        return startSecondDateTime(plusDateTime(currentDateTime(), DateType.SECOND, -interval))
            .toDate();
    }

    protected static int dateDiff(Date beginDate, Date endDate, DateType type) {
        //Interval interval = new Interval(beginDate.getTime(), endDate.getTime());
        //Period p = interval.toPeriod();
        DateTime start =new DateTime(beginDate);
        DateTime end =new DateTime(endDate);
        if (DateType.YEAR.equals(endDate)) {
             return Years.yearsBetween(start, end).getYears();
        } else if (DateType.MONTH.equals(type)) {
            return Months.monthsBetween(start, end).getMonths();
        } else if (DateType.WEEK.equals(type)) {
            return Weeks.weeksBetween(start, end).getWeeks();
        } else if (DateType.DAY.equals(type)) {
            return Days.daysBetween(start, end).getDays();
        } else if (DateType.HOUR.equals(type)) {
            return Hours.hoursBetween(start, end).getHours();
        } else if (DateType.MINUTE.equals(type)) {
            return Minutes.minutesBetween(start, end).getMinutes();
        } else if (DateType.SECOND.equals(type)) {
            return Seconds.secondsBetween(start, end).getSeconds();
        } else {
            return 0;
        }
    }

    public static int yearDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.YEAR);
    }

    public static int monthDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.MONTH);
    }

    public static int weekDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.WEEK);
    }

    public static int dayDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.DAY);
    }

    public static int hourDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.HOUR);
    }

    public static int minuteDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.MINUTE);
    }

    public static int secondDiff(Date beginDate, Date endDate) {
        return dateDiff(beginDate, endDate, DateType.SECOND);
    }

}
