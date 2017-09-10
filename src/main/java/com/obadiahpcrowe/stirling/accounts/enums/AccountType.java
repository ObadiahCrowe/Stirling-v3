package com.obadiahpcrowe.stirling.accounts.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:12 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.accounts.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AccountType {

    VISITOR("Visitor", 0),
    STUDENT("Student", 1),
    PARENT("Parent", 2),
    CLUB_CAPTAIN("Club Captain", 3),
    PREFECT("Prefect", 4),
    TEACHER("Teacher", 5),
    TUTOR("Tutor", 6),
    SERVICES("Services", 7),
    POD("POD", 8),
    SUB_SCHOOL_LEADER("Sub-School Leader", 9),
    YEAR_LEVEL_LEADER("Year-Level Leader", 10),
    DEPUTY_PRINCIPAL("Deputy Principal", 11),
    PRINCIPAL("Principal", 12),
    ADMINISTRATOR("Admin", 13),
    DEVELOPER("Developer", 14);

    private String friendlyName;
    private int accessLevel;

    AccountType(String friendlyName, int accessLevel) {
        this.friendlyName = friendlyName;
        this.accessLevel = accessLevel;
    }

}
