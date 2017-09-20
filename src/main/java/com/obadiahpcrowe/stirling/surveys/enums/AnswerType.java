package com.obadiahpcrowe.stirling.surveys.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 4:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AnswerType {

    MULTIPLE_CHOICE("Mutliple Choice"),
    TEXT("Text"),
    SINGLE_CHOICE("Single Choice");

    private String friendlyName;

    AnswerType(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
