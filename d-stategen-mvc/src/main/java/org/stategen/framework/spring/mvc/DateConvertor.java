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
package org.stategen.framework.spring.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.stategen.framework.util.StringUtil;

//通用时间转换器

/**
 * The Class DateConvertor.
 */
public class DateConvertor implements Converter<String, Date> {

    final static org.slf4j.Logger                       logger                  = org.slf4j.LoggerFactory.getLogger(DateConvertor.class);
    private static final Map<Pattern, SimpleDateFormat> DATE_PATTERN_FORMAT_MAP = new HashMap<Pattern, SimpleDateFormat>();
    private static final Map<Pattern, SimpleDateFormat> TIME_PATTERN_FORMAT_MAP = new HashMap<Pattern, SimpleDateFormat>();
    private static final Map<Pattern, SimpleDateFormat> MIC_PATTERN_FORMAT_MAP  = new HashMap<Pattern, SimpleDateFormat>();
    private static final Pattern UNIX_PATTERN=Pattern.compile("^\\d{10}$");
    private static final Pattern UNIX_PATTERN_MILLS=Pattern.compile("^\\d{13}$");
    

    static {
        genPattern(DATE_PATTERN_FORMAT_MAP, "yyyy-MM-dd", "^\\d{4}-\\d{2}-\\d{2}");
        genPattern(DATE_PATTERN_FORMAT_MAP, "yyyy/MM/dd", "^\\d{4}/\\d{2}/\\d{2}");
        genPattern(DATE_PATTERN_FORMAT_MAP, "yyyyMMdd", "^\\d{4}\\d{2}\\d{2}$");
        genPattern(DATE_PATTERN_FORMAT_MAP, "yyyy.MM.dd", "^\\d{4}\\.\\d{2}\\.\\d{2}");

        genPattern(TIME_PATTERN_FORMAT_MAP, "HH-mm-ss", "^\\d{2}-\\d{2}-\\d{2}");
        genPattern(TIME_PATTERN_FORMAT_MAP, "HH:mm:ss", "^\\d{2}:\\d{2}:\\d{2}");
        genPattern(TIME_PATTERN_FORMAT_MAP, "HH/mm/ss", "^\\d{2}/\\d{2}/\\d{2}");
        genPattern(TIME_PATTERN_FORMAT_MAP, "HHmmss", "^\\d{2}\\d{2}\\d{2}");
        genPattern(TIME_PATTERN_FORMAT_MAP, "HH.mm.ss", "^\\d{2}\\.\\d{2}\\.\\d{2}");

        genPattern(MIC_PATTERN_FORMAT_MAP, ".SSS", "^\\.\\d{3}$");
        genPattern(MIC_PATTERN_FORMAT_MAP, "SSS", "^\\d{3}$");


        //        genPattern("yyyy-MM-dd","^\\d{4}-\\d{2}-\\d{2}$");
        //        genPattern("yyyy-MM-dd HH-mm-ss","^\\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}-\\d{2}$");
        //        genPattern("yyyy-MM-dd HH-mm-ss.SSS","^\\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}-\\d{2}.\\d{3}$");
        //        genPattern("yyyy-MM-dd HH:mm:ss","^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
        //        genPattern("yyyy-MM-dd HH:mm:ss.SSS","^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$");
        //        genPattern("yyyy/MM/dd","^\\d{4}/\\d{2}/\\d{2}$");
        //        genPattern("yyyy/MM/dd HH/mm/ss","^\\d{4}/\\d{2}/\\d{2} \\d{2}/\\d{2}/\\d{2}$");
        //        genPattern("yyyy/MM/dd HH/mm/ss.SSS","^\\d{4}/\\d{2}/\\d{2} \\d{2}/\\d{2}/\\d{2}.\\d{3}$");
        //        genPattern("yyyyMMdd","^\\d{4}\\d{2}\\d{2}$");
        //        genPattern("yyyyMMdd HHmmss","^\\d{4}\\d{2}\\d{2} \\d{2}\\d{2}\\d{2}$");
        //        genPattern("yyyyMMdd HHmmss.SSS","^\\d{4}\\d{2}\\d{2} \\d{2}\\d{2}\\d{2}.\\d{3}$");
        //        genPattern("yyyy.MM.dd","^\\d{4}\\.\\d{2}\\.\\d{2}$");
        //        genPattern("yyyy.MM.dd HH.mm.ss","^\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}\\.\\d{2}\\.\\d{2}$");
        //        genPattern("yyyy.MM.dd HH.mm.ss.SSS","^\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}\\.\\d{2}\\.\\d{2}.\\d{3}$");
        //        //1473048265
        //        genPattern("SSSSSSSSSS","");
        //        genPattern("SSSSSSSSSSSSS","^\\d{13}$");
    }

    private static void genPattern(Map<Pattern, SimpleDateFormat> datePatternFormatMap, String format, String reg) {
        datePatternFormatMap.put(Pattern.compile(reg), new SimpleDateFormat(format));
    }

    public Date convert(String source) {
        if (StringUtil.isEmpty(source)) {
            return null;
        }

        source = source.trim();
        if (StringUtil.isNotEmpty(source)){
            int length = source.length();
            if (length==10){
                if (UNIX_PATTERN.matcher(source).find()){
                    return new Date(Long.parseLong(source)*1000);
                }
            }
            
            if (length==13){
                if (UNIX_PATTERN_MILLS.matcher(source).find()){
                    return new Date(Long.parseLong(source));
                }
            }
    
            for (Entry<Pattern, SimpleDateFormat> dateEntry : DATE_PATTERN_FORMAT_MAP.entrySet()) {
                Pattern datePattern = dateEntry.getKey();
                Matcher dateMatcher = datePattern.matcher(source);
                if (dateMatcher.find()) {
                    String dateStr = dateMatcher.group();
                    try {
                        SimpleDateFormat yearFormat = dateEntry.getValue();
                        Date date = yearFormat.parse(dateStr);
                        DateTime dateTime = new DateTime(date);
                        String timeStr = source.substring(dateMatcher.end());
                        dateTime = parserMills(dateTime, timeStr, 0, TIME_PATTERN_FORMAT_MAP, MIC_PATTERN_FORMAT_MAP);
                        return dateTime.toDate();
                    } catch (ParseException e) {
                        logger.error("", e);
                    }
                    break;
                }
            }
        }
        return null;
    }

    @SafeVarargs
    private static DateTime parserMills(DateTime dateTime, String timeStr, int mapIdx, Map<Pattern, SimpleDateFormat>... maps) throws ParseException {
        DateTime result = dateTime;
        if (timeStr != null) {
            timeStr = timeStr.trim();
            if (StringUtil.isNotEmpty(timeStr)) {
                for (Entry<Pattern, SimpleDateFormat> entry : maps[mapIdx].entrySet()) {
                    Pattern pattern = entry.getKey();
                    Matcher matcher = pattern.matcher(timeStr);
                    if (matcher.find()) {
                        String matcherGroup = matcher.group();
                        SimpleDateFormat format = entry.getValue();
                        Date date = format.parse(matcherGroup);
                        DateTime timeDateTime = new DateTime(date);
                        result = result.plusMillis(timeDateTime.getMillisOfDay());
                        timeStr = timeStr.substring(matcher.end());
                        mapIdx++;
                        if (mapIdx < maps.length) {
                            result = parserMills(result, timeStr, mapIdx, maps);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }
}
