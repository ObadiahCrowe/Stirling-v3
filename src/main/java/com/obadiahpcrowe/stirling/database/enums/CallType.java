package com.obadiahpcrowe.stirling.database.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 10:44 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum CallType {

    INSERT(false),
    GET(true),
    GET_FIELD(true),
    REPLACE(false),
    REPLACE_FIELD(false),
    REMOVE(false);

    private boolean returnsData;

    CallType(boolean returnsData) {
        this.returnsData = returnsData;
    }
}
