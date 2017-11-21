package com.obadiahpcrowe.stirling.localisation.translation.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/11/17 at 5:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TranslateSentence {

    private String trans;
    private String orig;
    private int backend;

    public TranslateSentence(String trans, String orig, int backend) {
        this.trans = trans;
        this.orig = orig;
        this.backend = backend;
    }
}
