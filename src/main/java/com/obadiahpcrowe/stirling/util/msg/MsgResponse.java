package com.obadiahpcrowe.stirling.util.msg;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:25 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util.msg
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum MsgResponse {

    SUCCESS("Success", false),
    WARNING("Warning", true),
    USER_ERROR("Error", false),
    STIRLING_ERROR("Stirling Error", true);

    private String message;
    private boolean log;

    MsgResponse(String message, boolean log) {
        this.message = message;
        this.log = log;
    }
}
