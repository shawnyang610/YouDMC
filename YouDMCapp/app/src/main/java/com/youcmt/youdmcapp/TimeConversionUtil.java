package com.youcmt.youdmcapp;

import android.text.format.DateUtils;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Utility class to convert the datetime sent from the API
 * into a GregorianCalendar, and find the difference between
 * current time and that GregorianCalendar.
 */

public class TimeConversionUtil {
    public static final int MINUTE_QUALIFIER = 0;
    public static final int HOUR_QUALIFIER = 1;
    public static final int DAY_QUALIFIER = 2;
    public static final int WEEK_QUALIFIER = 3;
    public static final int YEAR_QUALIFIER = 4;

    private static GregorianCalendar getCalendar(String datetime)
    {
        String[] parts = datetime.split(" ");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");
        int year = Integer.valueOf(dateParts[0]);
        int month = Integer.valueOf(dateParts[1])-1; //months are numbered 0-11
        int day = Integer.valueOf(dateParts[2]);
        int hour = Integer.valueOf(timeParts[0]);
        int minute = Integer.valueOf(timeParts[1]);

        GregorianCalendar gc = new GregorianCalendar(year, month, day, hour, minute);
        gc.setTimeZone(TimeZone.getTimeZone("UTC"));
        return gc;
    }

    private static long milliDifference(GregorianCalendar calendar)
    {
        GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        long millisecondDifference = now.getTimeInMillis()-calendar.getTimeInMillis();
        if(millisecondDifference< DateUtils.MINUTE_IN_MILLIS) {
            return DateUtils.MINUTE_IN_MILLIS;
        } else return (millisecondDifference);
    }

    public static int[] timeDifferencesValues(String datetime)
    {
        int[] values = new int[2];
        GregorianCalendar gregorianCalendar = getCalendar(datetime);
        long difference = milliDifference(gregorianCalendar);
        if(difference<DateUtils.HOUR_IN_MILLIS){
             values[0] = MINUTE_QUALIFIER;
             values[1] = (int)(difference/DateUtils.MINUTE_IN_MILLIS);
        }
        else if(difference<DateUtils.DAY_IN_MILLIS)
        {
            values[0] = HOUR_QUALIFIER;
            values[1] = (int)(difference/DateUtils.HOUR_IN_MILLIS);
        }
        else if(difference<DateUtils.WEEK_IN_MILLIS)
        {
            values[0] = DAY_QUALIFIER;
            values[1] = (int)(difference/DateUtils.DAY_IN_MILLIS);
        }
        else if(difference<DateUtils.YEAR_IN_MILLIS)
        {
            values[0] = WEEK_QUALIFIER;
            values[1] = (int)(difference/DateUtils.WEEK_IN_MILLIS);
        }
        else {
            values[0] = YEAR_QUALIFIER;
            values[1] = (int)(difference/DateUtils.YEAR_IN_MILLIS);
        }
        return values;
    }

}
