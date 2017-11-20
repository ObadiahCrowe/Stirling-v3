package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.cloud.CloudAccount;
import com.obadiahpcrowe.stirling.database.dao.interfaces.CloudDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/11/17 at 7:39 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class CloudDAOImpl extends BasicDAO<CloudAccount, ObjectId> implements CloudDAO {

    public CloudDAOImpl(Class<CloudAccount> cloudClass, Datastore datastore) {
        super(cloudClass, datastore);
    }

    @Override
    public CloudAccount getByUuid(UUID uuid) {
        Query<CloudAccount> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public void updateField(CloudAccount account, String field, Object value) {
        Query<CloudAccount> query = createQuery().field("uuid").equal(account.getUuid());
        UpdateOperations<CloudAccount> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
