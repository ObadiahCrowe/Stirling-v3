package com.obadiahpcrowe.stirling.pod.signin.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 2:37 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum PodReason {

    BEHAVIOUR("Behaviour"),
    STUDY("Study"),
    TEST("Test"),
    UNIFORM("Uniform"),
    LINE_0("Line 0"),
    WELLBEING("Wellbeing"),
    INDEPENDENT_STUDY("Independent Study"),
    CLASS_EXCURSION("Class Excursion"),
    CLASS_CAMP("Class Camp"),
    INTERNAL_SUSPENSION("Internal Suspension"),
    INCOMPLETE_WORK("Incomplete Work");

    private String friendlyName;

    PodReason(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
