package com.obadiahpcrowe.stirling.util.formatting;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 4/12/17 at 1:34 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.util.formatting
 * Copyright (c) Obadiah Crowe 2017
 */
public class NameFormatter {

    public static String formatName(String name) {
        String[] nameParts = name.split(" ");

        StringBuilder builder = new StringBuilder();
        for (String part : nameParts) {
            builder.append(part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ");
        }

        return builder.toString().trim();
    }
}
