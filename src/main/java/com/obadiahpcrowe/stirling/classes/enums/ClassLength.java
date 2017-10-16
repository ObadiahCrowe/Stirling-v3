package com.obadiahpcrowe.stirling.classes.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:08 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum ClassLength {

    TERM("Term"),
    SEMESTER("Semester"),
    YEAR("Year");

    private String friendlyName;

    ClassLength(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
