package com.obadiahpcrowe.stirling.util.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum HostOS {

    MACOS("MacOS"),
    WINDOWS("Windows"),
    LINUX("Linux"),
    UNKNOWN("Unknown");

    private String friendlyName;

    HostOS(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
