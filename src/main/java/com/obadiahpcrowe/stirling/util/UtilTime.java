package com.obadiahpcrowe.stirling.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:27 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilTime {

    private static UtilTime instance;

    public String getDayOfWeek(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dateTime = formatter.parseDateTime(date);
        return dateTime.dayOfWeek().getAsText();
    }

    public String getLogTime() {
        DateTime now = DateTime.now();
        return now.getDayOfMonth() + "/" + now.getMonthOfYear() + "/" + now.getYear() + " at " + now.getHourOfDay() + ":" +
          now.getMinuteOfHour() + "." + now.getSecondOfMinute();
    }

    public String getLogDate() {
        DateTime now = DateTime.now();
        return now.getDayOfMonth() + "." + now.getMonthOfYear() + "." + now.getYear();
    }

    public String getFriendlyDate() {
        DateTime now = DateTime.now();
        return now.getDayOfMonth() + "/" + now.getMonthOfYear() + "/" + now.getYear();
    }

    public String getFriendlyTime() {
        DateTime now = DateTime.now();
        int hour = now.getHourOfDay();

        String minStr;
        int min = now.getMinuteOfHour();
        if (min < 10) {
            minStr = "0" + String.valueOf(min);
        } else {
            minStr = String.valueOf(min);
        }

        return hour + ":" + minStr;
    }

    public static UtilTime getInstance() {
        if (instance == null)
            instance = new UtilTime();
        return instance;
    }
}
