package com.dsbie.rearend.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author lsl
 * @version 1.0
 * @date 2024年03月27日 15:20
 */
public class LocalDateUtil {

    private LocalDateUtil() {
    }

    public static DateTimeFormatter getFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * 根据默认格式获取当前时间
     *
     * @return 当前时间，如：2022-09-06
     */
    public static LocalDate getNow() {
        return LocalDate.now();
    }

    /**
     * 获取指定格式时间
     *
     * @param localDate 待格式化的时间，如：2022-09-06
     * @param pattern   日期格式，如："yyyyMMdd"
     * @return 当前时间，如：20220906
     */
    public static String getFormatDate(LocalDate localDate, String pattern) {
        return localDate.format(getFormatter(pattern));
    }

    /**
     * 获取Date格式时间
     *
     * @param localDate 待格式化的时间，如：2022-09-06
     * @return 当前时间，如：Thu Dec 29 00:00:00 CST 2022
     */
    public static Date parseDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将字符串形式的时间解析成LocalDate
     *
     * @param formatDate 待解析的时间，需为默认格式，如：2022-09-06
     * @return LocalDate，如：2022-09-06
     */
    public static LocalDate parseFormatDate(String formatDate) {
        return LocalDate.parse(formatDate);
    }

    /**
     * 将指定格式的字符串形式的时间解析成LocalDate
     *
     * @param formatDate 待解析的时间，如：2022/09/06
     * @param pattern    日期格式，如："yyyy/MM/dd"
     * @return LocalDate，如：2022-09-06
     */
    public static LocalDate parseFormatDate(String formatDate, String pattern) {
        return LocalDate.parse(formatDate, getFormatter(pattern));
    }

    /**
     * 解析10位时间戳成LocalDate(北京时间)
     *
     * @param secondTimeStamp 10位时间戳，如：1662393600L
     * @return LocalDate，如：2022-09-06
     */
    public static LocalDate parseSecondTimeStamp(Long secondTimeStamp) {
        return Instant.ofEpochSecond(secondTimeStamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 解析13位时间戳成LocalDate(北京时间)
     *
     * @param milliTimeStamp 13位时间戳，如：1662393600000L
     * @return LocalDate，如：2022-09-06
     */
    public static LocalDate parseMilliTimeStamp(Long milliTimeStamp) {
        return Instant.ofEpochMilli(milliTimeStamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 获取指定时间附近的时间
     *
     * @param localDate 指定时间，如：2022-09-06
     * @param addNumber 增量数量，如：-1
     * @param addUnit   增量单位，如：ChronoUnit.WEEKS
     * @return LocalDate，如：2022-09-06 -> 2022-08-30
     */
    public static LocalDate getNear(LocalDate localDate, Integer addNumber, ChronoUnit addUnit) {
        return localDate.plus(addNumber, addUnit);
    }

    /**
     * 获取时间间隔
     *
     * @param standard 基准时间，如：2021-09-06
     * @param target   目标时间，如：2022-09-05
     * @param unit     单位，如：ChronoUnit.MONTHS
     * @return 时间间隔，如：11
     */
    public static Long getZone(LocalDate standard, LocalDate target, ChronoUnit unit) {
        return standard.until(target, unit);
    }

    /**
     * 获取本周第一天(周一)
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本周第一天，如：2022-09-05
     */
    public static LocalDate getFirstDateOfWeek(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取本周最后一天(周日)
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本周最后一天，如：2022-09-11
     */
    public static LocalDate getLastDateOfWeek(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取本月第一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本月第一天，如：2022-09-01
     */
    public static LocalDate getFirstDateOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本月最后一天，如：2022-09-30
     */
    public static LocalDate getLastDateOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本年第一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本年第一天，如：2022-01-01
     */
    public static LocalDate getFirstDateOfYear(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取本年最后一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本年最后一天，如：2022-12-31
     */
    public static LocalDate getLastDateOfYear(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 获取本季度第一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本季度第一天，如：2022-07-01
     */
    public static LocalDate getFirstDateOfSeason(LocalDate localDate) {
        int month = (localDate.getMonthValue() - 1) / 3 * 3 + 1;
        return LocalDate.of(localDate.getYear(), month, 1);
    }

    /**
     * 获取本季度最后一天
     *
     * @param localDate 指定时间，如：2021-09-06
     * @return 本季度最后一天，如：2022-09-30
     */
    public static LocalDate getLastDateOfSeason(LocalDate localDate) {
        int month = (localDate.getMonthValue() - 1) / 3 * 3 + 3;
        LocalDate firstDateOfMonth = LocalDate.of(localDate.getYear(), month, 1);
        return getLastDateOfMonth(firstDateOfMonth);
    }

}
