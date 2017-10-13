package com.obadiahpcrowe.stirling.classes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.enums.ClassRole;
import com.obadiahpcrowe.stirling.classes.importing.ImportHolder;
import com.obadiahpcrowe.stirling.classes.obj.*;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
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

    /*
    The main problem with adoption right now is that Stirling is only truely effective when everyone is using it, v2 and below didn't have this problem.
    So now it's decently annoying to regulate who is in what class
    And how I determine each class and such
    So as far as permissions go, it's gonna be interesting
    What I'm thinking is assigning each daymap class and id, then when a user adds their daymap credentials, Stirling finds the Id and the associated Stirling class and adds it to the user
    I'm quite tired right now, so if anyone could verify that logic, that would be great
     */

    @Id
    private ObjectId objectId;

    // General
    private UUID uuid;
    private List<String> owners;
    private String name;
    private String desc;
    private String room;

    // For imports
    private Map<UUID, List<ImportHolder>> studentImportHolders;
    private List<ImportHolder> globalHolders;

    // Members and times
    private Map<UUID, ClassRole> members;
    private List<UUID> students;
    private List<UUID> teachers;
    private List<StirlingLesson> lessons;

    // Resources and such
    private List<StirlingSection> sections;
    private List<StirlingCatchup> catchups; //date, catchup info
    private List<StirlingPostable> classNotes;
    private List<StirlingPostable> homework;
    private List<AttachableResource> resources;

    // Results
    private Map<UUID, List<StirlingAssignment>> studentAssignments;
    private Map<UUID, List<StirlingResult>> studentResults; // TODO: 26/9/17 Generate report and shit from these results.
    private Map<UUID, List<ProgressMarker>> progressMarkers;

    @Deprecated
    public StirlingClass() {}

    public StirlingClass(StirlingAccount account, String name, String desc, String room) {
        this.uuid = UUID.randomUUID();
        this.owners = Lists.newArrayList(account.getAccountName());
        this.name = name;
        this.desc = desc;
        this.room = room;

        this.members = new HashMap<UUID, ClassRole>() {{ put(account.getUuid(), ClassRole.TEACHER); }};
        this.students = Lists.newArrayList();
        this.teachers = Lists.newArrayList(account.getUuid());
        this.lessons = Lists.newArrayList();

        this.sections = Lists.newArrayList();
        this.catchups = Lists.newArrayList();
        this.classNotes = Lists.newArrayList();
        this.homework = Lists.newArrayList();
        this.resources = Lists.newArrayList();

        this.studentAssignments = Maps.newHashMap();
        this.studentResults = Maps.newHashMap();
        this.progressMarkers = Maps.newHashMap();
    }

    // TODO: 13/10/17 THIS IS THE MOTHERFUCKING DAYMAP IMPORT FUNCTION. USE THIS YOU BLIND FUCK.
    // TODO: 12/10/17 How to get this shit to work vvv
    // I scrape daymap shit, save against the id, generate a class with the id linked somehow. Every other user that calls the same Id gets the same Stirling class
    public StirlingClass(String ownerId, String name, String desc, String room) {
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
