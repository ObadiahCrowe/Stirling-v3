package com.obadiahpcrowe.stirling.pod.laptop.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum LaptopStatus {

    COMPLETED("Finished re-imaging"),
    ACTIVE("Actively re-imaging"),
    FAILED("Reimaging failed"),
    NOT_REIMAGING("Not re-imaging");

    private String outputName;

    LaptopStatus(String outputName) {
        this.outputName = outputName;
    }
}
