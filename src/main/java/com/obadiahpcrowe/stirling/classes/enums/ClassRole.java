package com.obadiahpcrowe.stirling.classes.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 3:22 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum ClassRole {

    STUDENT("Student", false),
    STUDENT_TEACHER("Student Teacher", false),
    TEACHER("Teacher", true);

    private String friendlyName;
    private boolean canAdminister;

    ClassRole(String friendlyName, boolean canAdminister) {
        this.friendlyName = friendlyName;
        this.canAdminister = canAdminister;
    }
}
