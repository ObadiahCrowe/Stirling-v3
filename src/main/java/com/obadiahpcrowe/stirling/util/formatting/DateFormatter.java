package com.obadiahpcrowe.stirling.util.formatting;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 4/12/17 at 6:24 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.util.formatting
 * Copyright (c) Obadiah Crowe 2017
 */
public class DateFormatter {

    public static String formatTime(String date) {
        String pureDate;
        boolean isPm = false;
        if (date.contains("PM") || date.contains("AM")) {
            pureDate = date.replace("PM", "").replace("AM", "");
            if (date.contains("PM")) {
                isPm = true;
            }
        } else {
            pureDate = date;
        }

        String[] parts = pureDate.split(":");
        String hour = parts[0];
        String minute = parts[1];

        if (isPm) {
            Integer h = Integer.valueOf(hour);
            h = h + 12;
            hour = String.valueOf(h);
        }

        String trimmable = hour + ":" + minute;

        return trimmable.trim();
    }
}
