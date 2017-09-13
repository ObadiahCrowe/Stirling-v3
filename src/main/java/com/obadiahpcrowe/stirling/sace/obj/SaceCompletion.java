package com.obadiahpcrowe.stirling.sace.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:42 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SaceCompletion {

    private String name;
    private int potentialCredits;
    private int awardedCredits;
    private int target;

    public SaceCompletion(String name, int potentialCredits, int awardedCredits, int target) {
        this.name = name;
        this.potentialCredits = potentialCredits;
        this.awardedCredits = awardedCredits;
        this.target = target;
    }
}
