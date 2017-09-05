package com.obadiahpcrowe.stirling.classes.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 11:13 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum PostableType {

    CLASS_POST("Class Post"),
    HOMEWORK("Homework");

    private String friendlyName;

    PostableType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
