package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.pod.signin.obj.PodUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:50 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface PodSignInDAO extends DAO<PodUser, ObjectId> {

    PodUser getByUuid(UUID uuid);

    List<PodUser> getByStudentId(int studentId);

    void updateField(PodUser user, String field, Object value);
}
