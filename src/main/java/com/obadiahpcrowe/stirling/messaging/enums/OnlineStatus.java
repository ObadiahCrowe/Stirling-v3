package com.obadiahpcrowe.stirling.messaging.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 10:25 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.messaging.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum OnlineStatus {

    ONLINE("Online", true),
    BUSY("Busy", false),
    OFFLINE("Offline", false);

    private String friendlyName;
    private boolean notified;

    OnlineStatus(String friendlyName, boolean notified) {
        this.friendlyName = friendlyName;
        this.notified = notified;
    }

}
