package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 27/9/17 at 10:08 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class ProgressMarker {

    private UUID uuid;
    private String name;
    private String desc;
    private StirlingDate assignedDate;
    private boolean isCompleted;

    public ProgressMarker() {}

    public ProgressMarker(String name, String desc, StirlingDate assignedDate) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.desc = desc;
        this.assignedDate = assignedDate;
        this.isCompleted = false;
    }
}
