package com.obadiahpcrowe.stirling.util;

import org.joda.time.DateTime;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:27 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilTime {

    private static UtilTime instance;
    private DateTime now = DateTime.now();
    private DateTime dt = new DateTime();

    public String getLogTime() {
        return now.getDayOfMonth() + "/" + now.getMonthOfYear() + "/" + now.getYear() + " at " + now.getHourOfDay() + ":" +
          now.getMinuteOfHour() + "." + now.getSecondOfMinute();
    }

    public String getLogDate() {
        return now.getDayOfMonth() + "." + now.getMonthOfYear() + "." + now.getYear();
    }

    public String getFriendlyDate() {
        return now.getDayOfMonth() + "/" + now.getMonthOfYear() + "/" + now.getYear();
    }

    public String getFriendlyTime() {
        String ampm = "am";
        int hour = now.getHourOfDay();
        if (hour >= 13) {
            hour = hour - 12;
            ampm = "pm";
        }

        return hour + ":" + now.getMinuteOfHour() + " " + ampm.toUpperCase();
    }

    public static UtilTime getInstance() {
        if (instance == null)
            instance = new UtilTime();
        return instance;
    }
}
