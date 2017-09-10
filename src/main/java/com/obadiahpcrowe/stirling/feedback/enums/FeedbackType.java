package com.obadiahpcrowe.stirling.feedback.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:16 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.feedback.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum FeedbackType {

    IMPROVEMENT("Improvement", true),
    BUG_REPORT("Bug Report", true),
    FEATURE_REQUEST("Feature request", false);

    private String friendlyName;
    private boolean important;

    FeedbackType(String friendlyName, boolean important) {
        this.friendlyName = friendlyName;
        this.important = important;
    }
}
