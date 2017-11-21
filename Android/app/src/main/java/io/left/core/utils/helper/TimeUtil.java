package io.left.core.utils.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by W3E16 on 10/30/2017.
 */

public class TimeUtil {
    private static String dateFormat1 = "yyyy-MM-dd HH:mm:ss";
    private static String dateFormat2 = "yyyy-MM-dd HH:mm:ss.SSS";
    private static String dateFormat4 = "MMM dd";
    private static String dateFormat5 = "MMMM";
    private static String dateFormat6 = "dd";
    private static String dateFormat7 = "EEE";
    private static String dateFormat8 = "yyyy";
    private static String dateFormat9 = "hh:mm aa";
    private static String dateFormat10 = "hh:mm";

    private TimeUtil() {
    }

    public static long toCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String parseToLocalFromNS(long timeInNS) {
        long millis = TimeUnit.NANOSECONDS.toMillis(timeInNS);
        return parseToLocalFromMilli(millis);
    }

    public static String parseToLocalFromMilli(long millis) {
        Date date = new Date(millis);
        DateFormat format = new SimpleDateFormat(dateFormat1, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static long parseToUtcFromMilli(String millis) {
        try {
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormat2, new Locale("en"));
            Date objDate = dateFormatGmt.parse(millis);
            long currentdateInMillSec = objDate.getTime();
            return currentdateInMillSec;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseToLocalMilliFromMilli(long millis) {

        Date date = new Date(millis);
        DateFormat format = new SimpleDateFormat(dateFormat1, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        String defaultTimeString = format.format(date);
        long defaultTime = 0;
        try {
            defaultTime = format.parse(defaultTimeString).getTime();
            return defaultTime;
        } catch (ParseException e) {
            return millis;
        }
    }

    public static String getDate(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat4, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getOnlyMonth(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat5, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getOnlyDate(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat6, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    // Please do not remove this portion
    // requested by MIMO
    public static String getOnlyWeek(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat7, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getOnlyYear(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat8, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getOnlyTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat9, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static String getOnlyTimeHHMM(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat(dateFormat10, Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static int getYear(long milli) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        return calendar.get(Calendar.YEAR);
    }
}
