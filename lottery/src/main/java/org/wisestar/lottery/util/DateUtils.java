package org.wisestar.lottery.util;

import org.wisestar.lottery.exception.ServiceException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangxu on 2017/8/23.
 */
public class DateUtils {

    public static Date string2Date(String date, String format) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    public static String date2String(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 将weekday的星期部分换算成数字拼接后面的序号便于比较大小之后排序
     * @param week
     * @return
     */
    public static String week2Num(String week) {
        if (week.contains("周一")) {
            return week.replace("周一", "1");
        } else if (week.contains("周二")) {
            return week.replace("周二", "2");
        } else if (week.contains("周三")) {
            return week.replace("周三", "3");
        } else if (week.contains("周四")) {
            return week.replace("周四", "4");
        } else if (week.contains("周五")) {
            return week.replace("周五", "5");
        } else if (week.contains("周六")) {
            return week.replace("周六", "6");
        } else if (week.contains("周日")) {
            return week.replace("周日", "7");
        } else {
            throw new ServiceException("wrong week format");
        }
    }

}
