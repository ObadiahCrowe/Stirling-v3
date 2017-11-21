package com.obadiahpcrowe.stirling.util;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 3:34 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingDate {

    private String date;
    private String time;

    @Deprecated
    public StirlingDate() {
    }

    public StirlingDate(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public static StirlingDate getNow() {
        return new StirlingDate(UtilTime.getInstance().getFriendlyDate(), UtilTime.getInstance().getFriendlyTime());
    }

    public static StirlingDate parse(String rawDate) {
        String[] parts = rawDate.split("/");

        String day = parts[0];
        String month = parts[1];
        String year = "";

        if (parts[2].equalsIgnoreCase("17")) {
            year = "2017";
        }

        if (parts[2].equalsIgnoreCase("18")) {
            year = "2018";
        }

        if (year.equalsIgnoreCase("")) {
            return null;
        }

        return new StirlingDate(day + "/" + month + "/" + year, UtilTime.getInstance().getFriendlyTime());
    }
}
