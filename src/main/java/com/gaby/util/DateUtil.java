package com.gaby.util;

import com.a121tongbu.common.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * 项目日期时间工具类 注意SimpleDateFormat不是线程安全
 *
 * @author zhengxinzao
 * @create 2017-07-21 18:18
 **/
public class DateUtil {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String PATTERN_YYYYMMDD_SPLIT_BY_SLASH = "yyyy/MM/dd";
    public static final String PATTERN_YYYYMMDDPOINT = "yyyy.MM.dd";
    public static final String PATTERN_YYYYyMMmDDd = "yyyy年MM月dd日";
    public static final String PATTERN_YYYYyMMmDDd_HH_MM = "yyyy年MM月dd日 HH:mm";
    public static final String PATTERN_MMmDDd = "MM月dd日";
    public static final String PATTERN_MMDD = "MMdd";
    public static final String PATTERN_YYYY_MM = "yyyy-MM";
    public static final String PATTERN_YYYY = "yyyy";
    public static final String PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String PATTERN_YYYYMMDD_HH_MM_SS = "yyyyMMdd HH:mm:ss";
    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
    public static final String PATTERN_YYYYMM = "yyyyMM";
    public static final String PATTERN_HHMM = "HH:mm";
    public static final String PATTERN_HH_MM = "HHmm";
    public static final String PATTERN_HH_MM_SS = "HHmmss";
    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String,
            ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @Title: getSdf
     * @Description:
     * @author zhengxz
     * @date 2017年7月21日
     * @since JDK 1.7
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    logger.info("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new
                    // SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            logger.info("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     *
     * @Title: format
     * @Description:date转string
     * @author zhengxz
     * @date 2017年7月21日
     * @since JDK 1.7
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     *
     * @Title: parse
     * @Description:string转date
     * @author zhengxz
     * @date 2017年7月21日
     * @since JDK 1.7
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            throw new BaseException("日期格式" + pattern + "错误：" + dateStr);
        }
    }

    /**
     * 第二天零点
     */
    public static Date getNextDateZeroHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 一点
     */
    public static Date getDateOneHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getNextNDays(Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getNextNDays(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getNextNMinutes(Date date, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 判断日期是否同一天，不进行 时分秒比较
     */
    public static boolean sameDate(Date d1, Date d2) {
        LocalDate localDate1 = ZonedDateTime.ofInstant(d1.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = ZonedDateTime.ofInstant(d2.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public static int getHourOfDate(Date d){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 指定日期基础上加几天
     */
    public static Date addDay(Date d, Integer day) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, day);
        return new Date(c.getTimeInMillis());
    }

    /**
     * 指定日期基础上加几个月
     */
    public static Date addMonth(Date d, Integer month) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MONTH, month);
        return new Date(c.getTimeInMillis());
    }

    /**
     * 修改时间为凌晨
     */
    public static Date conversionDate000000(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new Date(c.getTimeInMillis());
    }

    /**
     * 当天最后一秒
     */
    public static Date conversionDate235959(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return new Date(c.getTimeInMillis());
    }

    /**
     * @param second 与当天0点差多少秒
     * @Title: conversionSecond2Date
     * @Description:秒转为当天0点加指定秒
     * @author zhengxz
     * @date 2017年6月30日
     * @since JDK 1.7
     */
    public static Date conversionSecond2Date(Integer second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTimeInMillis() + second * 1000);
    }

    public static Date conversionSecond2Date(Date date, Integer second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTimeInMillis() + second * 1000);
    }
    /**
     * 功能描述: <br> 〈hh:mm 转为 秒值〉
     *
     * @param hhmm hh:mm
     * @return:java.lang.String 秒值
     * @Author:zhengxz
     * @Date: 2018/9/12 16:13
     */
    public static Integer conversionHHMM2Second(String hhmm) {
        LocalTime lt = conversionDate2LocalTime(parse(hhmm, PATTERN_HHMM));
        return lt.getHour() * 3600 + lt.getMinute() * 60 + lt.getSecond();
    }

    /**
     * 功能描述: <br> 〈hhmm 转为 秒值〉
     *
     * @param hhmm hhmm
     * @return:java.lang.String 秒值
     * @Author:zhengxz
     * @Date: 2018/9/12 16:13
     */
    public static Integer conversionHH_MM2Second(String hhmm) {
        LocalTime lt = conversionDate2LocalTime(parse(hhmm, PATTERN_HH_MM));
        return lt.getHour() * 3600 + lt.getMinute() * 60 + lt.getSecond();
    }
    /**
     * 功能描述: <br> 〈秒值 转为 hh:mm〉
     *
     * @param second 秒值
     * @return:java.lang.String hh:mm
     * @Author:zhengxz
     * @Date: 2018/5/17 16:13
     */
    public static String conversionSecond2HHMM(Integer second) {
        return format(conversionSecond2Date(second), PATTERN_HHMM);
    }

    /**
     * 功能描述: <br> 〈秒值 转为 hhmm〉
     *
     * @param second 秒值
     * @return:java.lang.String hhmm
     * @Author:zhengxz
     * @Date: 2018/5/17 16:13
     */
    public static String conversionSecond2HH_MM(Integer second) {
        return format(conversionSecond2Date(second), PATTERN_HH_MM);
    }

    /**
     * 功能描述: <br> 〈取出当月第几天〉
     *
     * @return:int
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:35
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述: <br> 〈取出当月最后一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:36
     */
    public static Date getMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出当月最后一天,23:59:59〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:36
     */
    public static Date getMaxMonthDate235959(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出当月的第一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:26
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出当月的第一天.00:00:00〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:26
     */
    public static Date getFirstDayOfMonth000000(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出上月的第一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/11/29 10:28
     */
    public static Date getPreFirstDayOfMonth(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出本周第一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/12/22 17:00
     */
    public static Date getWeekFirstDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {//周天
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出本周最后一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/12/22 17:00
     */
    public static Date getWeekLastDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_WEEK, 7 - calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出上月最后一天〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2017/12/22 17:00
     */
    public static Date getPreMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 功能描述: <br> 〈取出当天的去年〉
     *
     * @return:java.util.Date
     * @Author:zhengxinzao
     * @Date: 2018/03/26 10:00
     */
    public static Date getPreYearDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * @Title: conversionDate2Second
     * @Description:时间（时，分）转到与0点差几秒
     * @author zhengxz
     * @date 2017年6月30日
     * @since JDK 1.7
     */
    public static Integer conversionDate2Second(Date date) {
        Calendar calendarXC = Calendar.getInstance();
        calendarXC.setTime(date);
        calendarXC.set(Calendar.SECOND, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return ((Long) (getSecondDifference(calendarXC.getTime(), calendar.getTime()))).intValue();
    }

    /**
     * @Title: getSecondDifference
     * @Description: 两个时间差几秒
     * @author zhengxz
     * @date 2017年6月30日
     * @since JDK 1.7
     */
    public static long getSecondDifference(Date time1, Date time2) {
        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

        long t1 = 0L;
        long t2 = 0L;
        try {
            t1 = timeformat.parse(getDateNumberFormat(time1)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            t2 = timeformat.parse(getDateNumberFormat(time2)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (time1.after(time2)) {
            return (t1 - t2) / 1000;
        }

        return (t2 - t1) / 1000;
    }
    /**
     * @Title: getSecondDifference
     * @Description: 两个日期差几天
     * @author zhengxz
     * @date 2018年10月22日
     * @since JDK 1.7
     */
    public static long getDayDifference(Date time1, Date time2) {
        LocalDate beginDate=conversionDate2LocalDate(time1);
        LocalDate endDate=conversionDate2LocalDate(time2);
        return endDate.toEpochDay()-beginDate.toEpochDay();
    }
    public static String getDateNumberFormat(Date date) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(date);
    }

    //传入日期到0点的总秒数
    public static Integer getDateToSeconds(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar
                .get(Calendar
                        .SECOND);
    }

    /**
     * 功能描述: <br> 〈取出周一到周天，1-7〉
     *
     * @return:int
     * @Author:zhengxz
     * @Date: 2018/3/14 13:34
     */
    public static int dayForWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 获取日期是星期几<br>
     *
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 功能描述: <br> 〈java.util.Date --> java.time.LocalDateTime〉
     *
     * @param date 日期
     * @return:java.time.LocalDateTime
     * @Author:zhengxz
     * @Date: 2018/6/14 10:37
     */
    public static LocalDateTime conversionDate2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 功能描述: <br> 〈java.util.Date --> java.time.LocalDate〉
     *
     * @param date 日期
     * @return:java.time.LocalDate
     * @Author:zhengxz
     * @Date: 2018/6/14 10:38
     */
    public static LocalDate conversionDate2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * 功能描述: <br> 〈java.util.Date --> java.time.LocalTime〉
     *
     * @param date 日期
     * @return:java.time.LocalTime
     * @Author:zhengxz
     * @Date: 2018/6/14 10:39
     */
    public static LocalTime conversionDate2LocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    /**
     * 功能描述: <br> 〈java.time.LocalDateTime --> java.util.Date〉
     *
     * @param localDateTime 日期
     * @return:java.util.Date
     * @Author:zhengxz
     * @Date: 2018/6/14 10:40
     */
    public static Date conversionLocalDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }
    /**
     * 功能描述: <br>
     * 〈java.time.LocalDate --> java.util.Date〉
     *
     * @param localDate
     * @return:java.util.Date
     * @Author:zhengxz
     * @Date: 2018/6/14 10:41
     */
    public static Date conversionLocalDate2Date(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }
    /**
     * 功能描述: <br>
     * 〈java.time.LocalTime --> java.util.Date〉
     *
     * @param localDate
     * @param localTime
     * @return:java.util.Date
     * @Author:zhengxz
     * @Date: 2018/6/14 10:42
     */
    public static Date conversionLocalTime2Ddate(LocalDate localDate,LocalTime localTime) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.conversionHH_MM2Second("0801"));
        System.out.println(DateUtil.conversionHHMM2Second("08:00"));

        System.out.println(DateUtil.conversionHH_MM2Second("800"));
        System.out.println(DateUtil.conversionHHMM2Second("8:00"));
    }
}
