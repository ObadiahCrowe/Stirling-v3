package com.obadiahpcrowe.stirling.classes.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 3:27 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AttendanceStatus {

    PRESENT("Present", false),
    NOT_PRESENT("Not present", true),
    NOT_PRESENT_REASON("Not present (Has reason)", false),
    ROLL_NOT_MARKED("Roll not marked", true),
    CANCELLED_LESSON("Cancelled lesson", false);

    private String friendlyName;
    private boolean notifyTeacher;

    AttendanceStatus(String friendlyName, boolean notifyTeacher) {
        this.friendlyName = friendlyName;
        this.notifyTeacher = notifyTeacher;
    }
}
