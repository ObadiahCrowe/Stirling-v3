package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 11:55 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface ClassesDAO extends DAO<StirlingClass, ObjectId> {

    StirlingClass getByUuid(UUID uuid);

    StirlingClass getByOwner(String owner);

    List<StirlingClass> getByTeacher(String teacherName);

    List<StirlingClass> getByEnrollment(StirlingAccount account);

    StirlingClass getByName(String name);

    void updateField(StirlingClass clazz, String field, Object value);
}
