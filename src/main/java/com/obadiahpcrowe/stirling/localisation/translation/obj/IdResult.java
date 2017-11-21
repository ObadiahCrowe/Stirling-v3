package com.obadiahpcrowe.stirling.localisation.translation.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/11/17 at 5:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class IdResult {

    private String[] srclangs;
    private double[] srclangs_confidences;
    private String[] extended_srclangs;

    public IdResult(String[] srclangs, double[] srclangs_confidences, String[] extended_srclangs) {
        this.srclangs = srclangs;
        this.srclangs_confidences = srclangs_confidences;
        this.extended_srclangs = extended_srclangs;
    }
}
