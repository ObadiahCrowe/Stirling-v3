package com.obadiahpcrowe.stirling.signin.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 5:54 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.signin.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum SignInReason {

    NO_REASON("No Reason", false),
    FAMILY("Family", true),
    SICK("Sick", true),
    REASON("Reason", true),
    LATE_START("Late Start", false),
    CLASS("Class", false);

    private String friendlyName;
    private boolean confirmation;

    SignInReason(String friendlyName, boolean confirmation) {
        this.friendlyName = friendlyName;
        this.confirmation = confirmation;
    }
}
