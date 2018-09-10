package com.polaris.comm.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: 提供常见的各种时间格式互相转换的方法</p>
 * <p>Description: 有些转换找不到是因为java api已经提供了，比如说：</p>
 * <p>Calendar calendar = Calendar.getInstance();</p>
 * <p>java.util.Date date = calendar.getTime();</p>
 * <p>Copyright: Copyright (c) 2002.6.24</p>
 * <p>Company: wondertek</p>
 *
 * @author tiansheng
 * @version 1.0
 */

public class TimeUtils {


    /**
     * <p>yyyy-MM-dd HH:mm:ss模式</p>
     */
    private static final int TYPE_DATE_TIME = 1;
    /**
     * <p>yyyy-MM-dd模式</p>
     */
    private static final int TYPE_DATE = 2;
    /**
     * <p>HH:mm:ss模式</p>
     */
    private static final int TYPE_TIME = 3;
    private static final String errPreFix = "com.wondertek.contract.util.TimeTool.";
    /**
     * <p>格式搜索的解析位置对象，也可以查询解析解析错误信息，-1表示正确</p>
     */
    private static ParsePosition parsePos = null;
    /**
     * <p>yyyy-MM-dd HH:mm:ss格式解析对象</p>
     */
    private SimpleDateFormat dateTimeFormat = null;
    /**
     * <p>yyyy-MM-dd格式解析对象</p>
     */
    private SimpleDateFormat dateFormat = null;
    /**
     * <p>HH:mm:ss格式解析对象</p>
     */
    private SimpleDateFormat timeFormat = null;

    private TimeUtils() {
    }

    /**
     * <p>初始化</p>
     *
     * @param type 初始化类型
     */
    private TimeUtils(int type) {
        if (type == TYPE_DATE_TIME) {
            dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (type == TYPE_DATE) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (type == TYPE_TIME) {
            timeFormat = new SimpleDateFormat("HH:mm:ss");
        } else {
            throw new IllegalArgumentException(errPreFix + "type error:type = "
                    + type);
        }
        parsePos = new ParsePosition(0);
    }

    /**
     * 根据数据库中的年月得到页面显示的年月字符串（swallow专用）
     *
     * @param year  年
     * @param month 月，0-11
     * @return 年月的字符串
     */
    public static String getYearMonthStr(String year, String month) {
        int intMonth = Integer.parseInt(month) + 1;
        return year + "-" + (intMonth < 10 ? "0" + intMonth : Integer.toString(intMonth));
    }

    /**
     * <p>得到当前时间的Calendar，time部分清0</p>
     *
     * @return 当前时间的Calendar，time部分清0
     */
    public static Calendar getCalenarDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    /**
     * 判断第一个年月是否小于第二个年月
     *
     * @param yearMonth01 第一个年月
     * @param yearMonth02 第二个年月
     * @return true or false
     */
    public static boolean before(int[] yearMonth01, int[] yearMonth02) {
        return yearMonth01[0] < yearMonth02[0]
                || yearMonth01[0] == yearMonth02[0] && yearMonth01[1] < yearMonth02[1];
    }

    /**
     * 判断第一个年月是否大于第二个年月
     *
     * @param yearMonth01 第一个年月
     * @param yearMonth02 第二个年月
     * @return true or false
     */
    public static boolean after(int[] yearMonth01, int[] yearMonth02) {
        return yearMonth01[0] > yearMonth02[0]
                || yearMonth01[0] == yearMonth02[0] && yearMonth01[1] > yearMonth02[1];
    }

    /**
     * 判断第一个年月日是否等于第二个年月日
     *
     * @param calendar01 第一个年月日
     * @param calendar02 第二个年月日
     * @return true or false
     */
    public static boolean equalsYearMonthDay(Calendar calendar01, Calendar calendar02) {
        return calendar01.get(Calendar.YEAR) == calendar02.get(Calendar.YEAR)
                && calendar01.get(Calendar.MONTH) == calendar02.get(Calendar.MONTH)
                && calendar01.get(Calendar.DAY_OF_MONTH) == calendar02.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * <p>根据参数得到星期的中文表示</p>
     *
     * @param week 星期参数，参照Calendar.DAY_OF_WEEK，1-7，表示星期日到星期六
     * @return 星期的中文表示
     */
    public static String getWeekShow(int week) {
        if (week < 1 || week > 7) {
            return null;
        }
        String[] WEEK = {
                "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
        };
        return WEEK[week - 1];
    }

    /**
     * 计算年和月的加法（参数为负就是减法）
     *
     * @param yearMonth 原始年月，月的范围是0-11
     * @param amount    增加或者减少的月，负的就是减少
     */
    public static void addMonth(int[] yearMonth, int amount) {
        int year = yearMonth[0];
        int month = yearMonth[1];
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException(
                    errPreFix + "addMonth month is invaid:month = " + month);
        }
        year += amount / 12;
        month += amount % 12;
        if (month < 0) {
            month += 12;
            year--;
        } else if (month > 11) {
            month -= 12;
            year++;
        }
        yearMonth[0] = year;
        yearMonth[1] = month;
    }


    /**
     * <p>计算两个日期的天数之差</p>
     *
     * @param calendar1 日期1
     * @param calendar2 日期2
     * @return 两个日期的天数之差
     */
    public static int diffOfDay(Calendar calendar1, Calendar calendar2) {
        long time = diffOfMillis(calendar1, calendar2);
        return (int) (time / (1000 * 60 * 60 * 24));
    }

    /**
     * <p>计算两个日期指定域值下之间的差</p>
     *
     * @param calendar1 日期1
     * @param calendar2 日期2
     * @param field     Calendar中的域值
     * @return 指定域值下之间的差
     */
    public static int diffOfField(Calendar calendar1, Calendar calendar2, int field) {
        return Math.abs(calendar1.get(field) - calendar2.get(field));
    }

    /**
     * <p>计算两个日期的毫秒数之差</p>
     *
     * @param calendar1 日期1
     * @param calendar2 日期2
     * @return 两个日期的毫秒数之差
     */
    public static long diffOfMillis(Calendar calendar1, Calendar calendar2) {
        return Math.abs(calendar1.getTimeInMillis() - calendar2.getTimeInMillis());
    }

    /**
     * <p>long时间类型转换成“yyyy-mm-dd HH:mm:ss”时间格式</p>
     *
     * @param time long类型的时间
     * @return “yyyy-mm-dd”时间格式
     */
    public static String longToStrDateTime(long time) {
        return dateToStrDateTime(new Date(time));
    }

    /**
     * <p>long时间类型转换成Calendar格式</p>
     *
     * @param time long类型的时间
     * @return Calendar时间格式
     */
    public static Calendar longToCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    /**
     * <p>“yyyy-mm-dd HH:mm:ss”时间格式转换成long时间类型</p>
     *
     * @param str “yyyy-mm-dd”时间格式
     * @return long类型的时间
     */
    public static long strDateTimeToLong(String str) {
        if (StringUtil.isEmpty(str)) {
            return 0L;
        } else {
            Date date = strDateTimeToDate(str);
            if (date == null) {
                return 0L;
            } else {
                return date.getTime();
            }
        }
    }

    /**
     * <p>Calendar格式转换成long时间类型</p>
     *
     * @param calendar Calendar时间格式
     * @return long类型的时间
     */
    public static long calendarToLong(Calendar calendar) {
        if (calendar == null) {
            return -1;
        }
        return calendar.getTimeInMillis();
    }

    /**
     * <p>得到当前时间，“yyyy-MM-dd HH:mm:ss”</p>
     *
     * @return 当前时间
     */
    public static String getDateTime() {
        return dateToStrDateTime(new Date());
    }

    /**
     * <p>得到当前时间，“yyyy-MM-dd”</p>
     *
     * @return 当前时间
     */
    public static String getDate() {
        return dateToStrDate(new Date());
    }

    /**
     * <p>得到当前时间，“HH:mm:ss”</p>
     *
     * @return 当前时间
     */
    public static String getTime() {
        return dateToStrTime(new Date());
    }

    /**
     * <p>“yyyy-MM-dd HH:mm:ss”时间格式转换成Date格式</p>
     *
     * @param str “yyyy-MM-dd HH:mm:ss”时间格式
     * @return 处理后的Date时间格式
     */
    public static Date strDateTimeToDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 10) {
            str += " 00:00:00";
        }
        return new TimeUtils(TYPE_DATE_TIME).dateTimeFormat.parse(str, parsePos);
    }

    /**
     * <p>“yyyy-MM-dd HH:mm:ss”时间格式转换成Calendar格式</p>
     *
     * @param str “yyyy-MM-dd HH:mm:ss”时间格式
     * @return 处理后的Calendar时间格式
     */
    public static Calendar strDateTimeToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        Date date = strDateTimeToDate(str);
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        return calendar;
    }

    /**
     * <p>多种时间格式转换成Calendar格式，缺的地方自动补0</p>
     *
     * @param str 多种时间格式
     * @return 处理后的Calendar时间格式
     */
    public static Calendar strToCalendar(String str) {
        return strDateTimeToCalendar(str);
    }

    /**
     * <p>“yyyy-MM-dd”时间格式转换成Date格式</p>
     *
     * @param str “yyyy-MM-dd”时间格式
     * @return 处理后的Date时间格式
     */
    public static Date strDateToDate(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        if (str.length() == 7) {
            str += "-01 00:00:00";
        }
        return new TimeUtils(TYPE_DATE).dateFormat.parse(str, parsePos);
    }

    /**
     * <p>“yyyy-MM-dd”时间格式转换成Calendar格式</p>
     *
     * @param str “yyyy-MM-dd”时间格式
     * @return 处理后的Calendar时间格式
     */
    public static Calendar strDateToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        Date date = strDateToDate(str);
        if (date == null) {
            return null;
        }
        calendar.setTime(date);
        return calendar;
    }

    /**
     * <p>Date转换成“yyyy-MM-dd HH:mm:ss”时间格式</p>
     *
     * @param date Date格式的时间
     * @return “yyyy-MM-dd HH:mm:ss”时间格式
     */
    public static String dateToStrDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new TimeUtils(TYPE_DATE_TIME).dateTimeFormat.format(date);
    }

    /**
     * <p>Calendar转换成“yyyy-MM-dd HH:mm:ss”时间格式</p>
     *
     * @param calendar Calendar格式的时间
     * @return “yyyy-MM-dd HH:mm:ss”时间格式
     */
    public static String calendarToStrDateTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrDateTime(calendar.getTime());
    }

    /**
     * <p>Date转换成“yyyy-MM-dd”时间格式</p>
     *
     * @param date Date格式的时间
     * @return “yyyy-MM-dd”时间格式
     */
    public static String dateToStrDate(Date date) {
        if (date == null) {
            return null;
        }
        return new TimeUtils(TYPE_DATE).dateFormat.format(date);
    }

    /**
     * <p>Calendar转换成“yyyy-MM-dd”时间格式</p>
     *
     * @param calendar Calendar格式的时间
     * @return “yyyy-MM-dd”时间格式
     */
    public static String calendarToStrDate(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrDate(calendar.getTime());
    }

    /**
     * <p>Date转换成“HH:mm:ss”时间格式</p>
     *
     * @param date Date格式的时间
     * @return “HH:mm:ss”时间格式
     */
    public static String dateToStrTime(Date date) {
        if (date == null) {
            return null;
        }
        return new TimeUtils(TYPE_TIME).timeFormat.format(date);
    }

    /**
     * <p>Calendar转换成“HH:mm:ss”时间格式</p>
     *
     * @param calendar Calendar格式的时间
     * @return “HH:mm:ss”时间格式
     */
    public static String calendarToStrTime(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return dateToStrTime(calendar.getTime());
    }

    /**
     * <p>将“yyyy-MM-dd HH:mm:ss”转换成yyyy-MM-dd 字符串时间格式</p>
     *
     * @return “yyyy-MM-dd” 字符串时间格式
     */

    public static String getShortDataByDateStr(String assign_deadline_date) {
        if (StringUtils.isBlank(assign_deadline_date)) {
            return null;
        }
        return assign_deadline_date.substring(0, assign_deadline_date.indexOf(' '));
    }

    /**
     * @return 加 年
     */

    public static String addYear(String day, int add) {
        return String.valueOf((Integer.parseInt(day.substring(0, 4)) + add)) + day.substring(4, day.length());
    }

    /**
     * @return 加 年
     */
    public static String getYear(String day, int add) {
        return String.valueOf((Integer.parseInt(day.substring(0, 4)) + add));
    }

    /**
     * <p>获取日期中的年份</p>
     *
     * @return
     * @author: XuChuanHou
     */
    public static int getYear(Date date) {
        if (date == null) {
            return -1;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }


    /**
     * @return 年月数组
     */
    public static List<String> getYmList(String ym_from, String ym_to) {
        List<String> listYm = new ArrayList<>();
        int ym_from_int = Integer.parseInt(ym_from);
        int ym_to_int = Integer.parseInt(ym_to);
        while (ym_from_int <= ym_to_int) {
            listYm.add(String.valueOf(ym_from_int));
            if ("12".equals(String.valueOf(ym_from_int).substring(4, 6))) {
                ym_from_int = Integer.parseInt(String.valueOf((Integer.parseInt(ym_from.substring(0, 4)) + 1)) + "01");
            } else {
                ym_from_int++;
            }
        }
        return listYm;
    }

    /**
     * @return 获取中文月
     */
    public static String getChineseMonth(String ymd) {
        String result = null;
        String month = "";
        if (ymd != null && ymd.length() >= 6) {
            month = ymd.substring(4, 6);
        }
        switch (month) {
            case "01":
                result = "一月";
                break;
            case "02":
                result = "二月";
                break;
            case "03":
                result = "三月";
                break;
            case "04":
                result = "四月";
                break;
            case "05":
                result = "五月";
                break;
            case "06":
                result = "六月";
                break;
            case "07":
                result = "七月";
                break;
            case "08":
                result = "八月";
                break;
            case "09":
                result = "九月";
                break;
            case "10":
                result = "十月";
                break;
            case "11":
                result = "十一月";
                break;
            case "12":
                result = "十二月";
                break;
            default:
        }
        return result;
    }

    public static Date getShortDateByStrDate(String assign_deadline_date) {
        if (StringUtils.isBlank(assign_deadline_date)) {
            return null;
        }
        String strDate = TimeUtils.getShortDataByDateStr(assign_deadline_date);
        return TimeUtils.strDateToDate(strDate);
    }

    /**
     * 获取日期月份中的天数
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    public static Date createDateByFormat(String date, String format) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date result = null;
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    /**
     * 获得时间的当天开始时间
     */
    public static Date getDayBeginDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获得时间的当天结束时间
     */
    public static Date getDayEndDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
}