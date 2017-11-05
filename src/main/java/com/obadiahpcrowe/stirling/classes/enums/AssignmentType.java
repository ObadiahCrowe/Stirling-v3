package com.obadiahpcrowe.stirling.classes.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/11/17 at 5:46 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AssignmentType {

    ASSIGNMENT("Assignment"),
    TEST("Test"),
    ESSAY("Essay"),
    GROUP_PROJECT("Group Project"),
    OTHER("Other");

    private String friendlyName;

    AssignmentType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
