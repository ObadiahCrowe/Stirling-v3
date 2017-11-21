package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.PodSignInDAO;
import com.obadiahpcrowe.stirling.pod.signin.obj.PodUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 3:00 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodSignInDAOImpl extends BasicDAO<PodUser, ObjectId> implements PodSignInDAO {

    public PodSignInDAOImpl(Class<PodUser> podUserClass, Datastore datastore) {
        super(podUserClass, datastore);
    }

    @Override
    public PodUser getByUuid(UUID uuid) {
        Query<PodUser> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<PodUser> getByStudentId(int studentId) {
        Query<PodUser> query = createQuery()
          .field("studentId").equal(studentId);

        return query.asList();
    }

    @Override
    public void updateField(PodUser user, String field, Object value) {
        Query<PodUser> query = createQuery().field("uuid").equal(user.getUuid());
        UpdateOperations<PodUser> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
