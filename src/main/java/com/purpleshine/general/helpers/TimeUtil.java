package com.purpleshine.general.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public final class TimeUtil {
    
    static public Instant toInstant(final String time, final DateTimeFormatter formatter, final ZoneId zone) {
        return LocalDateTime.parse(time, formatter).atZone(zone).toInstant();
    }
    
    static public LocalDateTime toLocalDateTime(final long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Timestamp to LocalDateTime
     * 
     * @param timestamp
     * @param zone
     * @return
     */
    static public LocalDateTime toLocalDateTime(final long timestamp, final ZoneId zone) {
        return Instant.ofEpochMilli(timestamp).atZone(zone).toLocalDateTime();
    }
    
    /**
     * 取得當前的timestamp
     * 
     * @return
     */
    static public long getCurrentTimestamp() {
        final LocalDateTime ldt = LocalDateTime.now();
        final ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }
    
    /**
     * 取得當前的timestamp
     * 
     * @param zone 時區
     * @return
     */
    static public long getCurrentTimestamp(final ZoneId zone) {
        final LocalDateTime ldt = LocalDateTime.now();
        final ZonedDateTime zdt = ldt.atZone(zone);
        return zdt.toInstant().toEpochMilli();
    }
    
    /**
     * 將字串時間轉成long
     * ex: 1970/01/01 -> 00000001
     * 
     * @param time 原始字串時間
     * @param timesplite 原始字串日期格式
     * @param locale
     * @return 轉換後long
     * @throws ParseException 
     */
    static public Date convertTimestamp(final String time, final String timesplite, final Locale locale) throws ParseException {
            return new SimpleDateFormat(timesplite, locale).parse(time);
    }
    
    /**
     * 取得當前時間日期
     * @return 日期字串
     */
    static public String getNowTime() {
        return LocalDateTime.now().toString();
    }
    
    /**
     * 取得當前時間日期
     * @param pattern - 指定格時 ex yyyy/MM/dd xx:xx:xx
     * @return 日期字串
     */
    static public String getNowTime(final String pattern) {
        final LocalDateTime now = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(Objects.requireNonNull(pattern)).format(now);
    }
    
    /**
     * 取得當前時間日期重新加減後的時間
     * @param pattern
     * @param amountToAdd
     * @return
     */
    static public String plusTime(final String pattern, final long amountToAdd) {
        return plusTime(pattern, ZoneId.systemDefault(), amountToAdd);
    }
    
    /**
     * 取得當前時間日期重新加減後的時間(加入時區轉換)
     * @param pattern
     * @param amountToAdd
     * @return
     */
    static public String plusTime(final String pattern, final ZoneId zone, final long amountToAdd) {
        LocalDateTime now = LocalDateTime.now(zone).plus(amountToAdd, ChronoUnit.MILLIS);
        return pattern == null ? now.toString() : DateTimeFormatter.ofPattern(pattern).format(now);
    }
    
    /**
     * 取得當前時間日期重新加減後的時間(加入時區轉換)
     * @param pattern
     * @param amountToAdd
     * @return
     */
    static public String plusZonedTime(final String pattern, final ZoneId zone, final long amountToAdd) {
        LocalDateTime now = LocalDateTime.now(zone).plus(amountToAdd, ChronoUnit.MILLIS);
        return pattern == null ? now.toString() : now.atZone(zone).format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 取得當前時間日期重新加減後的時間
     * @param pattern
     * @param amountToAdd
     * @return
     */
    static public String plusZoneTime(final String pattern, final long amountToAdd) {
        return plusZoneTime(pattern, ZoneId.systemDefault(), amountToAdd);
    }
    
    /**
     * 取得當前時間日期重新加減後的時間(加入時區轉換)
     * @param pattern
     * @param amountToAdd
     * @return
     */
    static public String plusZoneTime(final String pattern, final ZoneId zone, final long amountToAdd) {
        final ZonedDateTime now = ZonedDateTime.now(zone).plus(amountToAdd, ChronoUnit.MILLIS);
        return pattern == null ? now.toString() : DateTimeFormatter.ofPattern(pattern).format(now);
    }
    
    /**
     * 取得今日已消逝的時間
     * @param zoneId
     * @return
     */
    static public Duration getTodayDisappearTime(final String zoneId) {
        final LocalDateTime today = LocalDate.now(Clock.system(ZoneId.of(zoneId))).atStartOfDay();
        final LocalDateTime now = LocalDateTime.now(Clock.system(ZoneId.of(zoneId)));
        return Duration.between(today, now);
    }
    
    /**
     * 檢查現在是否為剛跨日時間
     * @param intervalTime 與今日間隔多久內為剛跨日 (秒)
     * @return
     */
    static public boolean isCrossDayTime(final String zoneId, final long intervalTime) {
        final boolean isCrossDayTime = getTodayDisappearTime(zoneId).getSeconds() <= intervalTime;
        return isCrossDayTime;
    }
    
    /**
     * 檢查現在是否為剛跨日時間
     * @param intervalTime 與今日間隔多久內為剛跨日 (秒)
     * @return
     */
    static public boolean isCrossHour(final String zoneId, final int hour, final long intervalTime) {
        final boolean isCrossHour = getTodayDisappearTime(zoneId).getSeconds() <= (hour * 3600 + intervalTime);
        return isCrossHour;
    }
    
    /**
     * 垮當日哪個時區
     * @param zoneId
     * @param hour
     * @return
     */
    static public boolean isCrossHourTime(final String zoneId, final int hour) {
        return LocalDateTime.now(Clock.system(ZoneId.of(zoneId))).getHour() >= hour;
    }
    
    /**
     * 
     * @param date
     * @return
     */
    static public LocalDateTime atStartOfDay(LocalDate date) {
        return date.atTime(LocalTime.MIN);
    }
    
    /**
     * 
     * @param date
     * @return
     */
    static public LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
    
    /**
     * 
     * @param date
     * @return
     */
    static public Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    /**
     * 
     * @param date
     * @return
     */
    static public Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    /**
     * 
     * @param date
     * @return
     */
    static private LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 
     * @param localDateTime
     * @return
     */
    static private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}