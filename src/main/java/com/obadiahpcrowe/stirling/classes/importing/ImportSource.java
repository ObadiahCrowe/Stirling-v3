package com.obadiahpcrowe.stirling.classes.importing;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:46 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum ImportSource {

    DAYMAP("Daymap"),
    MOODLE("Moodle"),
    GOOGLE_CLASSROOM("Google Classroom");

    private String friendlyName;

    ImportSource(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
