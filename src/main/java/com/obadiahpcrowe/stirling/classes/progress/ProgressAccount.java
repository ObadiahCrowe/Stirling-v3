package com.obadiahpcrowe.stirling.classes.progress;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.obj.ProgressMarker;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 7:54 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.progress
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("progmarkers")
public class ProgressAccount {

    @Id
    private ObjectId id;

    private UUID uuid;
    private List<ProgressMarker> progressMarkers;

    @Deprecated
    public ProgressAccount() {
    }

    public ProgressAccount(UUID uuid) {
        this.uuid = uuid;
        this.progressMarkers = Lists.newArrayList();
    }

    public ProgressAccount(UUID uuid, List<ProgressMarker> progressMarkers) {
        this.uuid = uuid;
        this.progressMarkers = progressMarkers;
    }
}
