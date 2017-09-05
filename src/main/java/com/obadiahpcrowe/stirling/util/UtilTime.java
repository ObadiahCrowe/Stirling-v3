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

    public static UtilTime getInstance() {
        if (instance == null)
            instance = new UtilTime();
        return instance;
    }
}
