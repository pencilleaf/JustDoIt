package com.example.justdoit;

import androidx.room.TypeConverter;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampConverter {
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

//    @TypeConverter
//    public static Date fromTimestamp(String value) {
//        if (value != null) {
//            try {
//                TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
//                df.setTimeZone(timeZone);
//                return df.parse(value);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            return null;
//        } else {
//            return null;
//        }
//    }
//
//
//    @TypeConverter
//    public static String dateToTimestamp(Date value) {
//        TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");
//        df.setTimeZone(timeZone);
//        return value == null ? null : df.format(value);
//    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}