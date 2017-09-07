package com.obadiahpcrowe.stirling.redis.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 12:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.redis.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum JedisType {

    INSERT(false),
    GET(true),
    DELETE(false),
    REPLACE(false);

    private boolean returnsData;

    JedisType(boolean returnsData) {
        this.returnsData = returnsData;
    }
}
