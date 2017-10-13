package com.obadiahpcrowe.stirling.classes.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 9:28 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SlotData {

    private String dayOfWeek;
    private String startTime;
    private String endTime;

    @Deprecated
    public SlotData() {
    }

    public SlotData(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
