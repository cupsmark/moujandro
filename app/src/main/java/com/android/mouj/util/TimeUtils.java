package com.android.mouj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ekobudiarto on 11/29/15.
 */
public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp > 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "0 second ago";
        else
            return res.toString();
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        Date d = new Date();
        long now = d.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1h";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1d";
        } else {
            return diff / DAY_MILLIS + "d";
        }
    }

    public static String convertFormatDate(String date, String parameter)
    {
        //Parameter : w (Week), d (Day : number), dt (Day :text) , m (Month), y (Year), pm (PM/AM), H (Hour Normal)
        String result = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(date);
            if (parameter.equals("w")) {
                sdf = new SimpleDateFormat("W");
                result = sdf.format(d);
            } else if (parameter.equals("d")) {
                sdf = new SimpleDateFormat("d");
                result = sdf.format(d);
            } else if (parameter.equals("dt")) {
                sdf = new SimpleDateFormat("EEEE");
                result = sdf.format(d);
            } else if (parameter.equals("m")) {
                sdf = new SimpleDateFormat("MMM");
                result = sdf.format(d);
            } else if (parameter.equals("M")) {
                sdf = new SimpleDateFormat("MMMM");
                result = sdf.format(d);
            } else if (parameter.equals("y")) {
                sdf = new SimpleDateFormat("yyyy");
                result = sdf.format(d);
            } else if (parameter.equals("pm")) {
                sdf = new SimpleDateFormat("K");
                result = sdf.format(d);
            } else if(parameter.equals("H")) {
                sdf = new SimpleDateFormat("HH:mm");
                result = sdf.format(d);
            }else {
                result = "";
            }
        }catch (ParseException ex)
        {
            result = "";
        }

        return result;
    }
}
