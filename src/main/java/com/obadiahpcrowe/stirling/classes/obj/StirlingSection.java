package com.obadiahpcrowe.stirling.classes.obj;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/10/17 at 2:23 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingSection {

    private UUID classUuid;
    private UUID sectionUuid;
    private String title;
    private String desc;
    private List<StirlingPostable> classNotes;
    private List<StirlingPostable> homework;
    private List<AttachableResource> resources;

    @Deprecated
    public StirlingSection() {}

    public StirlingSection(UUID classUuid, String title, String desc) {
        this.classUuid = classUuid;
        this.sectionUuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.classNotes = Lists.newArrayList();
        this.homework = Lists.newArrayList();
        this.resources = Lists.newArrayList();
    }

    public StirlingSection(UUID classUuid, String title, String desc, List<StirlingPostable> classNotes, List<StirlingPostable> homework,
                           List<AttachableResource> resources) {
        this.classUuid = classUuid;
        this.sectionUuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.classNotes = classNotes;
        this.homework = homework;
        this.resources = resources;
    }
}
