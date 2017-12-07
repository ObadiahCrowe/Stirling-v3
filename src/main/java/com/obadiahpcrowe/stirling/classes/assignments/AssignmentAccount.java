package com.obadiahpcrowe.stirling.classes.assignments;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 6:39 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.assignments
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("assignments")
public class AssignmentAccount {

    @Id
    private ObjectId id;

    private UUID uuid;
    private List<StirlingAssignment> assignments;

    @Deprecated
    public AssignmentAccount() {
    }

    public AssignmentAccount(UUID uuid) {
        this.uuid = uuid;
        this.assignments = Lists.newArrayList();
    }

    public AssignmentAccount(UUID uuid, List<StirlingAssignment> assignments) {
        this.uuid = uuid;
        this.assignments = assignments;
    }
}
