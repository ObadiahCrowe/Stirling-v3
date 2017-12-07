package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.classes.assignments.AssignmentAccount;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface AssignmentDAO extends DAO<AssignmentAccount, ObjectId> {

    AssignmentAccount getByUuid(UUID uuid);

    void updateField(UUID uuid, String field, Object value);
}
