package com.obadiahpcrowe.stirling.pod.tutors.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:41 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutors.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum TutorSpeciality {

    ENGLISH("English"),
    MATHEMATICS("Mathematics"),
    BIOLOGY("Biology"),
    CHEMISTRY("Chemistry"),
    PHYSICS("Physics"),
    FRENCH("French"),
    JAPANESE("Japanese"),
    CHINESE("Chinese"),
    PSYCHOLOGY("Psychology"),
    HEALTH("Health / PE"),
    LEGAL_STUDIES("Legal Studies"),
    HISTORY("History"),
    RESEARCH_PROJECT("Research Project"),
    PROGRAMMING("Programming"),
    DESIGN_TECH("Design and Technology");

    private String friendlyName;

    TutorSpeciality(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
