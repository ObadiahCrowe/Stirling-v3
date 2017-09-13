package com.obadiahpcrowe.stirling.sace.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:41 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SaceResult {

    private String yearAwarded;
    private String subject;
    private int credits;
    private String result;

    public SaceResult(String yearAwarded, String subject, int credits, String result) {
        this.yearAwarded = yearAwarded;
        this.subject = subject;
        this.credits = credits;
        this.result = result;
    }
}
