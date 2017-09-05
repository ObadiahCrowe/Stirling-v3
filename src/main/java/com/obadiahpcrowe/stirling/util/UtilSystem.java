package com.obadiahpcrowe.stirling.util;

import com.obadiahpcrowe.stirling.util.enums.HostOS;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:08 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class UtilSystem {

    private static UtilSystem instance;

    public HostOS getOS() {
        String os = System.getProperty("os.name");
        if (os.contains("Mac")) {
            return HostOS.MACOS;
        } else if (os.contains("Windows")) {
            return HostOS.WINDOWS;
        } else if (os.contains("Linux")) {
            return HostOS.LINUX;
        }

        return HostOS.UNKNOWN;
    }

    public static UtilSystem getInstance() {
        if (instance == null)
            instance = new UtilSystem();
        return instance;
    }
}
