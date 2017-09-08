package com.obadiahpcrowe.stirling.util;

import java.io.FilenameFilter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 10:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilFilter {

    public static FilenameFilter endsWithFilter(final String endsWith) {
        return (dir, name) -> {
            if (name.endsWith(endsWith)) {
                return true;
            }
            return false;
        };
    }
}
