package com.obadiahpcrowe.stirling.classes.assignments;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.enums.AssignmentType;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

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
@Entity("assignments")
public class StirlingAssignment {

    @Id
    private ObjectId id;

    private UUID uuid;
    private UUID classUuid;
    private UUID assignee;
    private String title;
    private String desc;
    private StirlingResult result;
    private boolean formative;
    private AssignmentType type;
    private StirlingDate assignedDateTime;
    private StirlingDate dueDateTime;
    private List<AttachableResource> submittedFiles;

    @Deprecated
    public StirlingAssignment() {
    }

    public StirlingAssignment(UUID assignee, UUID classUuid, String title, String desc, AssignmentType type, boolean formative,
                              StirlingResult result, StirlingDate dueDateTime) {
        this.uuid = UUID.randomUUID();
        this.classUuid = classUuid;
        this.assignee = assignee;
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
