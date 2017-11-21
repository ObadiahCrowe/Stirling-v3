package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 4:52 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingPostable {

    private UUID uuid;
    private String title;
    private String content;
    private UUID sectionUuid;
    private StirlingDate postDateTime;
    private List<AttachableResource> resources;

    @Deprecated
    public StirlingPostable() {}

    public StirlingPostable(String title, String content, List<AttachableResource> resources) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.postDateTime = StirlingDate.getNow();
        this.resources = resources;
    }

    public StirlingPostable(UUID sectionUuid, String title, String content, List<AttachableResource> resources) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.sectionUuid = sectionUuid;
        this.postDateTime = StirlingDate.getNow();
        this.resources = resources;
    }

    public StirlingPostable(String title, String content, List<AttachableResource> resources, StirlingDate dateTime) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.postDateTime = dateTime;
        this.resources = resources;
    }

    public StirlingPostable(UUID sectionUuid, String title, String content, List<AttachableResource> resources, StirlingDate dateTime) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.sectionUuid = sectionUuid;
        this.postDateTime = dateTime;
        this.resources = resources;
    }
}
