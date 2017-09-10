package com.obadiahpcrowe.stirling.signin.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 5:57 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.signin.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum SignOutReason {

    FAMILY("Family", true),
    SICK("Sick", true),
    REASON("Reason", true),
    EARLY_FINISH("Early Finish", false);

    private String friendlyName;
    private boolean confirmation;

    SignOutReason(String friendlyName, boolean confirmation) {
        this.friendlyName = friendlyName;
        this.confirmation = confirmation;
    }
}
