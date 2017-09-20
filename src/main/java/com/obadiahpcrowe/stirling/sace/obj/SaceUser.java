package com.obadiahpcrowe.stirling.sace.obj;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("sace")
public class SaceUser {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String saceId;
    private String sacePassword;
    private @Setter List<SaceResult> results;
    private @Setter List<SaceCompletion> completions;

    public SaceUser(UUID uuid, String saceId, String sacePassword) {
        this.uuid = uuid;
        this.saceId = saceId;
        this.sacePassword = sacePassword;
    }
}
