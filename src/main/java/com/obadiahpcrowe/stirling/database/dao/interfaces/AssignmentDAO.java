package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.classes.assignments.StirlingAssignment;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface AssignmentDAO extends DAO<StirlingAssignment, ObjectId> {

    StirlingAssignment getByUuid(UUID uuid);

    List<StirlingAssignment> getByAssignee(UUID uuid);

    List<StirlingAssignment> getByClass(UUID uuid);

    void updateField(UUID uuid, String field, Object value);
}
