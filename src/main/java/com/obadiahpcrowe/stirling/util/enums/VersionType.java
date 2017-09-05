package com.obadiahpcrowe.stirling.util.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:11 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum VersionType {

    RELEASE("Release"),
    BETA("Beta"),
    DEVELOPMENT_BUILD("Development Preview");

    private String friendlyName;

    VersionType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
