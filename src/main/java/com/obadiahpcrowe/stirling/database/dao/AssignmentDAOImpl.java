package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.classes.assignments.StirlingAssignment;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AssignmentDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:18 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class AssignmentDAOImpl extends BasicDAO<StirlingAssignment, ObjectId> implements AssignmentDAO {

    public AssignmentDAOImpl(Class<StirlingAssignment> assignmentClass, Datastore datastore) {
        super(assignmentClass, datastore);
    }

    @Override
    public StirlingAssignment getByUuid(UUID uuid) {
        Query<StirlingAssignment> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<StirlingAssignment> getByAssignee(UUID uuid) {
        Query<StirlingAssignment> query = createQuery()
          .field("assignee").equal(uuid);

        return query.asList();
    }

    @Override
    public List<StirlingAssignment> getByClass(UUID uuid) {
        Query<StirlingAssignment> query = createQuery()
          .field("classUuid").equal(uuid);

        return query.asList();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<StirlingAssignment> query = createQuery().field("uuid").equal(uuid);
        UpdateOperations<StirlingAssignment> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
