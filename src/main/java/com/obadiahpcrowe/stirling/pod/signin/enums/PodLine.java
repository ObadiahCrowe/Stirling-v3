package com.obadiahpcrowe.stirling.pod.signin.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 2:35 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum PodLine {

    ALL("All"),
    BEFORE_SCHOOL("Before School"),
    PROGRAM_X("Program X"),
    ONE("Line 1"),
    TWO("Line 2"),
    THREE("Line 3"),
    FOUR("Line 4"),
    FIVE("Line 5"),
    SIX("Line 6"),
    AFTER_SCHOOL("After School");

    private String friendlyName;

    PodLine(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
