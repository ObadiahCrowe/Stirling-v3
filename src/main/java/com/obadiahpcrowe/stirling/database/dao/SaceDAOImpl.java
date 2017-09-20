package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.SaceDAO;
import com.obadiahpcrowe.stirling.sace.obj.SaceCompletion;
import com.obadiahpcrowe.stirling.sace.obj.SaceResult;
import com.obadiahpcrowe.stirling.sace.obj.SaceUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:43 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class SaceDAOImpl extends BasicDAO<SaceUser, ObjectId> implements SaceDAO {

    public SaceDAOImpl(Class<SaceUser> saceUserClass, Datastore datastore) {
        super(saceUserClass, datastore);
    }

    @Override
    public SaceUser getByUuid(UUID uuid) {
        Query<SaceUser> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<SaceResult> getResults(SaceUser saceUser) {
        return saceUser.getResults();
    }

    @Override
    public List<SaceCompletion> getCompletions(SaceUser saceUser) {
        return saceUser.getCompletions();
    }
}
