/* ============================================================ 
 * StringUtil.java
 * Created: [2008-10-9] by YangHongyuan
 * ============================================================ 
 * ProjectName :ehr
 * Description: 
 * 
 * ==========================================================*/

package com.polaris.core.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONNull;

/**
 * <p>
 * 字符串工具类
 * </p>
 *
 */

public class StringUtil {
    private static final String RTN_CODE = "\\r|\\n";

    private StringUtil() {
    }

    /**
     * 返回一个对象的toString()
     *
     * @param obj 被处理的对象
     * @return 如果obj!=null 返回 obj.toString(),如果obj==null 返回 "";
     */
    public static String notNullString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 返回一个对象的toString()
     *
     * @param obj 被处理的对象
     * @param dft 当obj为null时的默认值
     * @return 如果obj!=null 返回 obj.toString(),如果obj==null 返回 notNullString(dft);
     */
    public static String notNullString(Object obj, Object dft) {
        return obj == null ? StringUtil.notNullString(dft) : obj.toString();
    }

    /**
     * 去掉以 suffix 结尾的部分
     *
     * @param original 原字符串
     * @param suffix   后缀
     * @return 返回去掉后缀的字符串
     */
    public static String cutSuffix(String original, String suffix) {
        if (original == null) {
            return null;
        }
        if (original.endsWith(suffix)) {
            int pos = original.lastIndexOf(suffix);
            return original.substring(0, pos);
        }
        return original;
    }

    /**
     * 根据文本框的显示长度来输出相应的字符串,文本显示长度是中文1个占位,西文0.5个占位
     *
     * @param original 原字串
     * @param size     文本框的长度
     * @return 结果字串
     */
    public static String cutWithTextSize(String original, int size) {
        if (original == null) {
            return null;
        }
        if (size < 0) {
            return original;
        }
        if (size == 0) {
            return "";
        }

        if (original.length() > size * 2) {
            original = original.substring(0, size * 2);
        }
        int lenofByte = original.getBytes().length;
        char[] chars = original.toCharArray();
        int len = chars.length;
        while (lenofByte > size * 2) {
            if (chars[--len] > 256) {
                lenofByte -= 2;
            } else {
                lenofByte -= 1;
            }
        }
        return original.substring(0, len);
    }

    /**
     * 字符串的首字符大写
     *
     * @param original 原字符串
     * @return 结果字串
     */
    public static String upperCaseFirstCharacter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        char[] chrs = original.toCharArray();
        chrs[0] = Character.toUpperCase(chrs[0]);
        return new String(chrs);
    }

    /**
     * 字符串的首字符小写
     *
     * @param original 原字符串
     * @return 结果字串
     */
    public static String lowerCaseFirstCharacter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        char[] chrs = original.toCharArray();
        chrs[0] = Character.toLowerCase(chrs[0]);
        return new String(chrs);
    }

    /**
     * 得到数据库中用来表识true和false的值
     *
     * @param value Boolean
     * @return String
     */
    public static String toSQLBooleanChar(Boolean value) {
        if (value == null) {
            return null;
        }
        if (value.booleanValue()) {
            return "Y";
        }
        return "N";
    }

    /**
     * 解析字符串为boolean值
     *
     * @param s            原字符串
     * @param defaultValue 默认值
     * @return 当 s=="true" 或 s=="yes" 时, 返回 true; 当 s==null, 返回 defaultValue;
     * 否则, 返回 false;
     */
    public static boolean parseBoolean(String s, boolean defaultValue) {
        boolean b = defaultValue;
        if (s != null) {
            if ("true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s)) {
                b = true;
            } else {
                b = false;
            }
        }
        return b;
    }

    /**
     * 解析字符串为int值
     *
     * @param s            原字符串
     * @param defaultValue 默认值
     * @return 当s可以被解析时, 返回Integer.parseInt(s) 否则, 返回 defaultValue;
     */
    public static int parseInt(String s, int defaultValue) {
        int i = defaultValue;
        try {
            i = Integer.parseInt(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return i;
    }

    /**
     * 解析字符串为long值
     *
     * @param s            原字符串
     * @param defaultValue 默认值
     * @return 当s可以被解析时, 返回Integer.parseInt(s) 否则, 返回 defaultValue;
     */
    public static long parseLong(String s, long defaultValue) {
        long i = defaultValue;
        try {
            i = Long.parseLong(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return i;
    }

    /**
     * 解析字串为日期
     *
     * @param s String
     * @return java.util.Date
     * @throws java.text.ParseException
     */
    public static java.util.Date parseDate(String s) throws java.text.ParseException {
        if (s == null) {
            return null;
        }
        java.text.DateFormat f = java.text.DateFormat.getDateInstance(java.text.DateFormat.DEFAULT);
        return f.parse(s);
    }

    /**
     * 解析字符串为float值
     *
     * @param s            原字符串
     * @param defaultValue 默认值
     * @return 当s可以被解析时, 返回Float.parseFloat(s) 否则, 返回 defaultValue;
     */
    public static float parseFloat(String s, float defaultValue) {
        float i = defaultValue;
        try {
            i = Float.parseFloat(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return i;
    }

    /**
     * 解析字符串为double值
     *
     * @param s            原字符串
     * @param defaultValue 默认值
     * @return 当s可以被解析时, 返回Integer.parseInt(s) 否则, 返回 defaultValue;
     */
    public static double parseDouble(String s, double defaultValue) {
        double i = defaultValue;
        try {
            i = Double.parseDouble(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return i;
    }

    /**
     * 将字符串转换为Integer对象
     *
     * @param s 原字符串
     * @return 当符合10进制整数, 并且值不越界时, 返回相应对象, 否则返回null
     */
    public static Integer toInteger(String s) {
        try {
            return Integer.valueOf(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转换为Double对象
     *
     * @param s 原字符串
     * @return 当符合10进制浮点数, 并且值不越界时, 返回相应对象, 否则返回null
     */
    public static Double toDouble(String s) {
        try {
            return Double.valueOf(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转换为Float对象
     *
     * @param s 原字符串
     * @return 当符合10进制浮点数, 并且值不越界时, 返回相应对象, 否则返回null
     */
    public static Float toFloat(String s) {
        try {
            return Float.valueOf(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转换为Long对象
     *
     * @param s 原字符串
     * @return 当符合10进制整数, 并且值不越界时, 返回相应对象, 否则返回null
     */
    public static Long toLong(String s) {
        try {
            return Long.valueOf(s);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转换为Boolean对象
     *
     * @param s 原字符串
     * @return 当 s=="true" 或 s=="yes" 时, 返回 Boolean.True; 否则, 返回 Boolean.False;
     */
    public static Boolean toBoolean(String s) {
        if (s == null) {
            return Boolean.FALSE;
        }
        if ("true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s)) {
            return Boolean.TRUE;
        }
        Integer i = toInteger(s);
        if (i != null && i.intValue() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public static BigDecimal toBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }

    }

    /**
     * 字符串替换(在jdk1.4 中有实现,这里是对jdk1.4之前版本的支持);
     *
     * @param str    原字符串
     * @param target 被替换的字符串
     * @param with   替换成的字符串
     * @return 返回结果, 当with为null时, 被处理为空字符串;
     */
    public static String replace(String str, String target, String with) {
        if (str == null) {
            return null;
        } else if (str == "") {
            return "";
        } else if (target == null || "".equals(target)) {
            return str;
        }
        if (with == null) {
            with = "";
        }
        int len = target.length();
        int pos = str.indexOf(target);
        if (pos == -1) {
            return str;
        } else {
            return str.substring(0, pos) + with + replace(str.substring(pos + len), target, with);
        }
    }

    /**
     * 将 txt 文本以 HTML 格式输出,主要是对空格,换行和tab的替换,1tab=4空格
     *
     * @param str 原字符串
     * @return 格式化结果字符串, 当 str==null 时,返回""
     */
    public static String htmlFormat(String str) {
        if (str == null) {
            return "";
        }
        char[] chars = str.toCharArray();
        int len = chars.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (chars[i] == 10) {
                sb.append("<br>");
            } else if (chars[i] == 32) {
                sb.append("&nbsp;");
            } else if (chars[i] == 9) {
                sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 得到字符串的字节数
     *
     * @param str 字符串
     * @return 字符串的字节数, str为null时返回0;
     */
    public static int getByteCount(String str) {
        if (str == null) {
            return 0;
        }
        return str.getBytes().length;
    }

    /**
     * 得到字符串的字符数
     *
     * @param str 字符串
     * @return 字符串的字符数, str为null时返回0;
     */
    public static int getCharacterCount(String str) {
        if (str == null) {
            return 0;
        }
        return str.toCharArray().length;
    }

    /**
     * 格式化输出日期
     *
     * @param datetime 待格式化的日期
     * @param pattern  格式化的样式,如 "yyyy.MM.dd 'at' hh:mm:ss z"
     * @return 符合格式的字符串
     * @see java.text.SimpleDateFormat.format(Date);
     */
    public static String format(java.util.Date datetime, String pattern) {
        if (datetime == null) {
            return "";
        }
        SimpleDateFormat f;
        if (pattern != null) {
            f = new SimpleDateFormat(pattern);
        } else {
            f = new SimpleDateFormat();
        }
        return f.format(datetime);
    }

    /**
     * 格式化输出日期
     *
     * @param datetime 待格式化的日期
     * @param pattern  格式化的样式,如 "yyyy.MM.dd 'at' hh:mm:ss z"
     * @return 符合格式的字符串
     * @see format(java.util.Date, String );
     */
    public static String format(java.sql.Timestamp datetime, String pattern) {
        if (datetime == null) {
            return "";
        }
        return format(new java.util.Date(datetime.getTime()), pattern);
    }

    /**
     * 格式化输出日期yyyymmdd
     *
     * @param date 待格式化的日期
     * @return 符合格式的字符串
     */
    public static String format(java.util.Date date) {
        if (date == null) {
            return "";
        }
        String rs = format(date, "yyyy-MM-dd");
        rs = rs.replaceAll("-", "");
        return rs;
    }

    /**
     * 格式化输出日期yyyymmdd
     *
     * @param datetime 待格式化的日期
     * @return 符合格式的字符串
     */
    public static String format(java.sql.Timestamp datetime) {
        if (datetime == null) {
            return "";
        }
        String rs = format(new java.util.Date(datetime.getTime()));
        return rs;
    }

    /**
     * 格式化输出日期
     *
     * @param datetime 待格式化的日期
     * @param pattern  格式化的样式,如 "yyyy.MM.dd 'at' hh:mm:ss z"
     * @return 符合格式的字符串
     * @see format(java.util.Date, String );
     */
    public static String format(java.util.Calendar datetime, String pattern) {
        if (datetime == null) {
            return "";
        }
        return format(datetime.getTime(), pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "$#,###"
     * @return 符合格式的字符串
     * @see java.text.DecimalFormat.format(long number);
     */
    public static String format(long number, String pattern) {
        DecimalFormat f = null;
        if (pattern != null) {
            f = new DecimalFormat(pattern);
        } else {
            f = new DecimalFormat();
        }
        return f.format(number);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "$#,###"
     * @return 符合格式的字符串
     * @see format(long, String);
     */
    public static String format(Integer number, String pattern) {
        if (number == null) {
            return "";
        }
        return format(number.longValue(), pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "$#,###"
     * @return 符合格式的字符串
     * @see format(long, String);
     */
    public static String format(int number, String pattern) {
        return format((long) number, pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "$#,###"
     * @return 符合格式的字符串
     * @see format(long, String);
     */
    public static String format(Long number, String pattern) {
        if (number == null) {
            return "";
        }
        return format(number.longValue(), pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "#,##0.0#"
     * @return 符合格式的字符串
     * @see java.text.DecimalFormat.format(double);
     */
    public static String format(double number, String pattern) {
        DecimalFormat f = null;
        if (pattern != null) {
            f = new DecimalFormat(pattern);
        } else {
            f = new DecimalFormat();
        }
        return f.format(number);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "#,##0.0#"
     * @return 符合格式的字符串
     * @see format(Double, String)
     */
    public static String format(Double number, String pattern) {
        if (number == null) {
            return "";
        }
        return format(number.doubleValue(), pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "#,##0.0#"
     * @return 符合格式的字符串
     * @see format(Double, String)
     */
    public static String format(Float number, String pattern) {
        if (number == null) {
            return "";
        }
        return format(number.doubleValue(), pattern);
    }

    /**
     * 格式化输出数字
     *
     * @param number  待格式化的数字
     * @param pattern 格式化的样式,如 "#,##0.0#"
     * @return 符合格式的字符串
     * @see format(Double, String)
     */
    public static String format(float number, String pattern) {
        return format((double) number, pattern);
    }

    /**
     * 将数组对象以字符串方式输出
     *
     * @param objs Object[]
     * @param dlim String 分隔符
     * @return String
     */
    public static String combine(Object[] objs, String dlim) {
        if (objs == null || objs.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < objs.length; i++) {
                sb.append(objs[i]).append(dlim);
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    /**
     * 将集合以字符串方式输出
     *
     * @param col  Collection
     * @param dlim String 分隔符
     * @return String
     */
    @SuppressWarnings("rawtypes")
    public static String combine(java.util.Collection col, String dlim) {
        if (col == null || col.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Iterator iter = col.iterator(); iter.hasNext(); ) {
                sb.append((Object) iter.next()).append(dlim);
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    @SuppressWarnings("rawtypes")
    public static String combine(java.util.Map map, String dlim) {
        if (map == null || map.size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                Object item = (Object) iter.next();
                sb.append(item).append('=').append(map.get(item)).append(dlim);
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    /**
     * 判断一个字串是否为空字串, null或0长度
     *
     * @param s String
     * @return boolean
     */
    public static boolean isEmpty(String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断多个字符串中是否至少有一个为空字串, null或0长度
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String... s) {
        if (null == s || s.length == 0) {
            return false;
        }
        for (String str : s) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字串是否为非空字串, null或0长度
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isEmptyOfStrict(String str) {
        boolean flag = false;
        if (StringUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 打印字符串的编码
     *
     * @param s   String
     * @param out PrintStream
     */
    public static void printStringCode(String s, java.io.PrintStream out) {
        if (s == null || s.length() == 0) {
            out.println("The String is empty");
            return;
        }
        out.print("The String is:");
        out.println(s);
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            out.print(Integer.toHexString((short) chars[i]));
            out.print(",");
        }
        out.println();
    }

    /**
     * 得到本地编码的字符串
     *
     * @param s String
     * @return String
     */
    public static String nativeEncode(String s) {
        if (s == null) {
            return null;
        }
        try {
            return new String(s.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException ex) {
        	ex.printStackTrace();
            return s;
        }
    }

    /**
     * 金额转换所用方法 供NumToRMBStr调用
     *
     * @param NumStr String
     * @return String
     */
    public static String positiveIntegerToHanStr(String NumStr) { // 输入字符串必须正整数，只允许前导空格(必须右对齐)，不宜有前导零
        String HanDigiStr[] = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        String HanDiviStr[] = new String[]{"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰",
                "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾",
                "佰", "仟"};

        StringBuilder sb = new StringBuilder();
        boolean lastzero = false;
        boolean hasvalue = false; // 亿、万进位前有数值标记
        int len, n;
        len = NumStr.length();
        if (len > 15) {
            return "数值过大!";
        }
        for (int i = len - 1; i >= 0; i--) {
            if (NumStr.charAt(len - i - 1) == ' ') {
                continue;
            }
            n = NumStr.charAt(len - i - 1) - '0';
            if (n < 0 || n > 9) {
                return "输入含非数字字符!";
            }

            if (n != 0) {
                if (lastzero) {
                    sb.append(HanDigiStr[0]); // 若干零后若跟非零值，只显示一个零
                }
                // 除了亿万前的零不带到后面
                // 如十进位前有零也不发壹音用此行
                if (!(n == 1 && (i % 4) == 1 && i == len - 1))  { // 十进位处于第一位不发壹音
                    sb.append(HanDigiStr[n]);
                }
                sb.append(HanDiviStr[i]); // 非零值后加进位，个位为空
                hasvalue = true; // 置万进位前有值标记

            } else {
                if ((i % 8) == 0 || ((i % 8) == 4 && hasvalue)) { // 亿万之间必须有非零值方显示万
                    sb.append(HanDiviStr[i]); // “亿”或“万”
                    hasvalue = false;
                }
            }
            if ((i % 8) == 0 || (i % 8) == 4) {
                hasvalue = false;
            }
            lastzero = (n == 0) && (i % 4 != 0); // 亿万前有零后不加零，如：拾万贰仟
        }

        if (sb.length() == 0) {
            return HanDigiStr[0]; // 输入空字符或"0"，返回"零"
        }
        return sb.toString();
    }

    /**
     * 金额由数值型转为人民币大写
     *
     * @param val double
     * @return String
     */
    public static String numToRMBStr(double val) {
        String HanDigiStr[] = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        String SignStr = "";
        String TailStr = "";
        long fraction, integer;
        int jiao, fen;

        if (val < 0) {
            val = -val;
            SignStr = "负";
        }
        if (val > 99999999999999.999 || val < -99999999999999.999) {
            return "数值位数过大!";
        }
        // 四舍五入到分
        long temp = Math.round(val * 100);
        integer = temp / 100;
        fraction = temp % 100;
        jiao = (int) fraction / 10;
        fen = (int) fraction % 10;
        if (jiao == 0 && fen == 0) {
            TailStr = "整";
        } else {
            TailStr = HanDigiStr[jiao];
            if (jiao != 0) {
                TailStr += "角";
            }
            if (integer == 0 && jiao == 0) {// 零圆后不写零几分
                TailStr = "";
            }
            if (fen != 0) {
                TailStr += HanDigiStr[fen] + "分";
            }
        }

        // 下一行可用于非正规金融场合，0.03只显示“叁分”而不是“零圆叁分”

        return SignStr + positiveIntegerToHanStr(String.valueOf(integer)) + "圆" + TailStr;
    }

    /**
     * 将对象转换为指定长度的字符串,对象toString之后的长度小于length,则自动用defaultChar填充
     *
     * @param o
     * @param length
     * @param defaultChar
     * @return
     */
    public static String convertToString(Object o, int length, String defaultChar) {
        String r = o.toString();
        int currentLength = r.length();
        if (currentLength < length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length - currentLength; i++) {
                sb.append(defaultChar);
            }
            sb.append(r);
            r = sb.toString();
        }
        return r;
    }

    /**
     * 取得一个字符串在另一个字符串中出现的次数
     *
     * @param source
     * @param toFind
     * @return
     */
    public static int countStr(String source, String toFind) {
        if (source == null) {
            return 0;
        }
        int count = 0;
        int index = source.indexOf(toFind);
        while (index != -1) {
            source = source.substring(index + toFind.length());
            index = source.indexOf(toFind);
            count++;
        }
        return count;
    }

    /**
     * <pre>
     * 在字符串中每隔一定长度(英文1个字符，中文2个字符)插入某个字符
     * </pre>
     *
     * @param resource
     * @param size
     * @param insertStr
     * @return
     */
    public static String insertStr2Str(String resource, int size, String insertStr) {
        StringBuilder sb = new StringBuilder();
        char[] chars = resource.toCharArray();
        int i = 0;
        for (char c : chars) {
            sb.append(c);

            i = i + (c > 256 ? 2 : 1);
            if (i >= size) {
                sb.append(insertStr);
                i = 0;
            }
        }
        return sb.toString();
    }

    /**
     * <pre>
     * 判断第一个参数是否等于后续的参数中的某一个
     * </pre>
     *
     * @param value
     * @param value1
     * @param strs
     * @return
     */
    public static boolean isIn(String value, String value1, String... strs) {
        if (value.equals(value1)) {
            return true;
        }

        if (strs != null && strs.length > 0) {
            for (String str : strs) {
                if (str.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <pre>
     * 判断第一个参数是否等于后续的参数中的某一个
     * </pre>
     *
     * @param value
     * @param value1
     * @param strs
     * @return
     */
    public static boolean isInArrays(String value, String... strs) {
        if (strs != null && strs.length > 0) {
            for (String str : strs) {
                if (str.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <pre>
     * 判断第一个参数是否不等于后续的所有参数
     * </pre>
     *
     * @param value
     * @param value1
     * @param strs
     * @return
     */
    public static boolean isNotIn(String value, String value1, String... strs) {
        return !isIn(value, value1, strs);
    }

    /**
     * 去除字符串中的回车和换行符
     *
     * @param str
     * @return
     */
    public static String replaceLineBreaks(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile(RTN_CODE);
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 将src字符串前补placeholder到length长度
     *
     * @param src
     * @param placeholder
     * @param length
     * @return
     */
    public static String formatStr(String src, String placeholder, int length) {
        if (length < 0) {
            return src;
        }
        int srcLen = src.length();
        StringBuilder sb = new StringBuilder();
        int addCount = length - srcLen;
        if (addCount < 0) {
            return src;
        }
        for (int i = 0; i < addCount; i++) {
            sb.append(placeholder);
        }
        return sb.append(src).toString();
    }

//    /**
//     * <b>function:</b> 处理oracle sql 语句in子句中（where id in (1, 2, ..., 1000, 1001)），
//     * 如果子句中超过1000项就会报错。
//     * 这主要是oracle考虑性能问题做的限制。
//     * 如果要解决次问题，可以用 where id (1, 2, ..., 1000) or id (1001, ...)
//     *
//     * @param ids   in语句中的集合对象
//     * @param count in语句中出现的条件个数
//     * @param field in语句对应的数据库查询字段
//     * @return 返回 field in (...) or field in (...) 字符串
//     */
//    public static String getOracleSQLIn(List<?> ids, int count, String field, boolean yinhao) {
//        count = Math.min(count, 1000);
//        int len = ids.size();
//        int size = len % count;
//        if (size == 0) {
//            size = len / count;
//        } else {
//            size = (len / count) + 1;
//        }
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            int fromIndex = i * count;
//            int toIndex = Math.min(fromIndex + count, len);
//            String productId = StringUtils.defaultIfEmpty(StringUtils.join(ids.subList(fromIndex, toIndex), yinhao ? "','" : ","), "");
//            if (i != 0) {
//                builder.append(" or ");
//            }
//            if (yinhao) {
//                builder.append(field).append(" in ('").append(productId).append("')");
//            } else {
//                builder.append(field).append(" in (").append(productId).append(")");
//            }
//        }
//
//        return "(" + StringUtils.defaultIfEmpty(builder.toString(), field + " in ('')") + ")";
//    }

    public static String Null2Empty(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Integer) {
            value = String.valueOf(value);
        }
        if (value instanceof Double) {
            value = String.valueOf(value);
        }
        if (value instanceof Float) {
            value = String.valueOf(value);
        }
        if (value instanceof Long) {
            value = String.valueOf(value);
        }
        if (value instanceof JSONNull) {
            value = "";
        }

        if (!(value instanceof String)) {
            return String.valueOf(value);
        } else {
            return value.toString();
        }
    }
    
    public static String newString4UTF8(byte[] bytes) {
        return new String(bytes, Charset.forName(StandardCharsets.UTF_8.name()));
    }

}
