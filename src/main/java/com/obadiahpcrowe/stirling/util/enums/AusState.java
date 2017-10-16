package com.obadiahpcrowe.stirling.util.enums;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:09 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.util.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AusState {

    SA("South Australia"),
    NT("Northern Territory"),
    ACT("Australian Capital Territory"),
    WA("Western Australia"),
    VIC("Victoria"),
    TAS("Tasmania"),
    QLD("Queensland"),
    NSW("New South Wales");

    private String stateName;

    AusState(String stateName) {
        this.stateName = stateName;
    }
}
