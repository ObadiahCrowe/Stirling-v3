package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 3:24 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface ImportDAO extends DAO<ImportAccount, ObjectId> {

    ImportAccount getByUuid(UUID uuid);

    ImportAccount getByStirlingAcc(StirlingAccount account);

    ImportAccount getByUsername(String username);

    void updateField(ImportAccount account, String field, Object value);
}
