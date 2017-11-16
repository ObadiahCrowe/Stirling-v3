package com.obadiahpcrowe.stirling.classes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.enums.ClassRole;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.*;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
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
    private List<String> owners;
    private String name;
    private String desc;
    private String room;

    // For imports
    private Map<UUID, List<String>> studentImportHolders; // Account UUID, Course ID
    private List<? extends ImportableClass> globalHolders;

    // Members and times
    private Map<UUID, ClassRole> members;
    private List<UUID> students;
    private List<UUID> teachers;

    private List<StirlingLesson> lessons;
    private LessonTimeSlot timeSlot;

    // Resources and such
    private List<StirlingSection> sections;
    private List<StirlingCatchup> catchups;
    private List<StirlingPostable> classNotes;
    private List<StirlingPostable> homework;
    private List<StirlingResource> resources;

    // Results
    private Map<UUID, List<StirlingAssignment>> studentAssignments;
    private Map<UUID, List<StirlingResult>> studentResults; // TODO: 26/9/17 Generate report and stuff from these results.
    private Map<UUID, List<ProgressMarker>> progressMarkers;

    @Deprecated
    public StirlingClass() {}

    public StirlingClass(StirlingAccount account, String name, String desc, String room, LessonTimeSlot slot) {
        this.uuid = UUID.randomUUID();
        this.owners = Lists.newArrayList(account.getAccountName());
        this.name = name;
        this.desc = desc;
        this.room = room;

        this.members = new HashMap<UUID, ClassRole>() {{ put(account.getUuid(), ClassRole.TEACHER); }};
        this.students = Lists.newArrayList();
        this.teachers = Lists.newArrayList(account.getUuid());
        this.lessons = Lists.newArrayList();
        this.timeSlot = slot;

        this.sections = Lists.newArrayList();
        this.catchups = Lists.newArrayList();
        this.classNotes = Lists.newArrayList();
        this.homework = Lists.newArrayList();
        this.resources = Lists.newArrayList();

        this.studentAssignments = Maps.newHashMap();
        this.studentResults = Maps.newHashMap();
        this.progressMarkers = Maps.newHashMap();
    }

    public StirlingClass(String ownerId, String name, String desc, String room, LessonTimeSlot slot) {
        this.uuid = UUID.randomUUID();
        this.owners = Lists.newArrayList(ownerId);
        this.name = name;
        this.desc = desc;
        this.room = room;

        this.studentImportHolders = Maps.newHashMap();
        this.globalHolders = Lists.newArrayList();

        this.members = Maps.newHashMap();
        this.students = Lists.newArrayList();
        this.teachers = Lists.newArrayList();
        this.lessons = Lists.newArrayList();
        this.timeSlot = slot;

        this.sections = Lists.newArrayList();
        this.catchups = Lists.newArrayList();
        this.classNotes = Lists.newArrayList();
        this.homework = Lists.newArrayList();
        this.resources = Lists.newArrayList();

        this.studentAssignments = Maps.newHashMap();
        this.studentResults = Maps.newHashMap();
        this.progressMarkers = Maps.newHashMap();
    }
}
