package com.obadiahpcrowe.stirling.redis.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:57 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.redis.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum RedisHeader {

    POD_SIGNIN("PODIN-"),
    SCHOOL_SIGNIN("SCHOOLIN-"),
    LAPTOP_REIMAGING("LAPTOP-"),
    PROGRESS_MARKER("PROGMARK-"),
    SURVEY("SURVEY-"),
    SESSION("SESSION-"),
    TUTOR("TUTOR-"),
    TUTOR_REQUEST("TUTOR-REQUEST-");

    private String headerName;

    RedisHeader(String headerName) {
        this.headerName = headerName;
    }
}
