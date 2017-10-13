package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 9:23 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingAssignment {

    private UUID uuid;
    private String title;
    private String desc;
    private StirlingResult result;
    private StirlingDate assignedDateTime;
    private StirlingDate dueDateTime;

    @Deprecated
    public StirlingAssignment() {}

    public StirlingAssignment(String title, String desc, StirlingResult result, StirlingDate dueDateTime) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.result = result;
        this.assignedDateTime = StirlingDate.getNow();
        this.dueDateTime = dueDateTime;
    }
}
