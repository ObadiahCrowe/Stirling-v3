package com.obadiahpcrowe.stirling.laptop.obj;

import com.obadiahpcrowe.stirling.laptop.enums.LaptopStatus;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:06 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.laptop
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class ReimageLaptop {

    private String laptopName;
    private LaptopStatus status;
    private String stage;
    private int percentageComplete;

    public ReimageLaptop(String laptopName, LaptopStatus status, String stage, int percentageComplete) {
        this.laptopName = laptopName;
        this.status = status;
        this.stage = stage;
        this.percentageComplete = percentageComplete;
    }

}
