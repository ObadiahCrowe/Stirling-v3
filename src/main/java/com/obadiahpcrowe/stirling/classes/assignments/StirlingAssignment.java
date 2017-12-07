package com.obadiahpcrowe.stirling.classes.assignments;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.enums.AssignmentType;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
    private UUID classUuid;

    @Setter
    private String title;

    @Setter
    private String desc;

    @Setter
    private StirlingResult result;

    @Setter
    private boolean formative;

    @Setter
    private AssignmentType type;
    private StirlingDate assignedDateTime;

    @Setter
    private StirlingDate dueDateTime;

    @Setter
    private List<AttachableResource> submittedFiles;

    @Deprecated
    public StirlingAssignment() {
    }

    public StirlingAssignment(UUID classUuid, String title, String desc, AssignmentType type, boolean formative,
                              StirlingResult result, StirlingDate dueDateTime) {
        this.uuid = UUID.randomUUID();
        this.classUuid = classUuid;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.formative = formative;
        this.result = result;
        this.assignedDateTime = StirlingDate.getNow();
        this.dueDateTime = dueDateTime;
        this.submittedFiles = Lists.newArrayList();
    }
}
