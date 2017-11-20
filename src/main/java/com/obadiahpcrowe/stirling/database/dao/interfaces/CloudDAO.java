package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.cloud.CloudAccount;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/11/17 at 7:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface CloudDAO extends DAO<CloudAccount, ObjectId> {

    CloudAccount getByUuid(UUID uuid);

    void updateField(CloudAccount account, String field, Object value);
}
