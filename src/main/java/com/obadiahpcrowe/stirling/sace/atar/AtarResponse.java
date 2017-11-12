package com.obadiahpcrowe.stirling.sace.atar;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/11/17 at 5:33 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.atar
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class AtarResponse {

    private double atar;
    private double aggregate;
    private String responseMsg;

    public AtarResponse(double atar, double aggregate, String responseMsg) {
        this.atar = atar;
        this.aggregate = aggregate;
        this.responseMsg = responseMsg;
    }
}
