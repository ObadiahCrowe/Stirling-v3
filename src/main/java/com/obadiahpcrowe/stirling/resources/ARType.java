package com.obadiahpcrowe.stirling.resources;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 7:32 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.resources
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum ARType {

    NORMAL("Resource"),
    CLASS("Class Resource"),
    CLASS_SINGLE("User Class Resource"),
    ANNOUNCEMENT("Announcement Resource");

    private String friendlyName;

    ARType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
