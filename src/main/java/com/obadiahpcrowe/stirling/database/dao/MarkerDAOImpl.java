package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.classes.progress.ProgressAccount;
import com.obadiahpcrowe.stirling.database.dao.interfaces.MarkerDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 8:00 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class MarkerDAOImpl extends BasicDAO<ProgressAccount, ObjectId> implements MarkerDAO {

    public MarkerDAOImpl(Class<ProgressAccount> progressClass, Datastore datastore) {
        super(progressClass, datastore);
    }

    @Override
    public ProgressAccount getByUuid(UUID uuid) {
        Query<ProgressAccount> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<ProgressAccount> query = createQuery().field("uuid").equal(uuid);
        UpdateOperations<ProgressAccount> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
