package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.classes.assignments.AssignmentAccount;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AssignmentDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:18 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class AssignmentDAOImpl extends BasicDAO<AssignmentAccount, ObjectId> implements AssignmentDAO {

    public AssignmentDAOImpl(Class<AssignmentAccount> assignmentClass, Datastore datastore) {
        super(assignmentClass, datastore);
    }

    @Override
    public AssignmentAccount getByUuid(UUID uuid) {
        Query<AssignmentAccount> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<AssignmentAccount> query = createQuery().field("uuid").equal(uuid);
        UpdateOperations<AssignmentAccount> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
