package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 11:58 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassesDAOImpl extends BasicDAO<StirlingClass, ObjectId> implements ClassesDAO {

    public ClassesDAOImpl(Class<StirlingClass> stirlingClassClass, Datastore datastore) {
        super(stirlingClassClass, datastore);
    }

    @Override
    public StirlingClass getByUuid(UUID uuid) {
        Query<StirlingClass> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<StirlingClass> getByTeacher(String teacherName) {
        Query<StirlingClass> query = createQuery()
          .field("teachers").contains(teacherName);

        return query.asList();
    }

    @Override
    public List<StirlingClass> getByEnrollment(StirlingAccount account) {
        Query<StirlingClass> query = createQuery()
          .field("students").contains(account.getAccountName());

        return query.asList();
    }

    @Override
    public StirlingClass getByName(String name) {
        Query<StirlingClass> query = createQuery()
          .field("name").equal(name);

        return query.get();
    }

    @Override
    public void updateField(StirlingClass clazz, String field, Object value) {
        Query<StirlingClass> query = createQuery().field("uuid").equal(clazz.getUuid());
        UpdateOperations<StirlingClass> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
