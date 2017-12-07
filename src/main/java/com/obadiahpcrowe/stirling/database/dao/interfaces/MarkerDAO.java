package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.classes.progress.ProgressAccount;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 7:59 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface MarkerDAO extends DAO<ProgressAccount, ObjectId> {

    ProgressAccount getByUuid(UUID uuid);

    void updateField(UUID uuid, String field, Object value);
}
