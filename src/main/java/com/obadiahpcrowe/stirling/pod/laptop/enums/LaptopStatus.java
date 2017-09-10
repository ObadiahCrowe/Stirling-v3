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

    COMPLETED("Finished reimaging"),
    ACTIVE("Actively reimaging"),
    FAILED("Reimaging failed");

    private String outputName;

    LaptopStatus(String outputName) {
        this.outputName = outputName;
    }
}
