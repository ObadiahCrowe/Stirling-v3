package com.obadiahpcrowe.stirling.classes;

import com.obadiahpcrowe.stirling.classes.enums.ClassRole;
import com.obadiahpcrowe.stirling.classes.obj.*;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:17 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("classes")
public class StirlingClass {

    @Id
    private ObjectId objectId;

    // General
    private UUID uuid;
    private String room;
    private String name;
    private String desc;

    // Members and times
    private Map<UUID, ClassRole> members;
    private List<UUID> students;
    private List<UUID> teachers;
    private List<StirlingLesson> lessons;

    // Resources and such
    private List<StirlingCatchup> catchups; //date, catchup info
    private List<StirlingPostable> classNotes;
    private List<StirlingPostable> homework;
    private List<AttachableResource> resources;

    // Results
    private Map<UUID, List<StirlingAssignment>> studentAssignments;
    private Map<UUID, List<StirlingResult>> studentResults; // TODO: 26/9/17 Generate report and shit from these results.

    public StirlingClass() {}

}
