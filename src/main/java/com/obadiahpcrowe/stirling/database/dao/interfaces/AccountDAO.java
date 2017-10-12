package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:00 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface AccountDAO extends DAO<StirlingAccount, ObjectId> {

    StirlingAccount getByUuid(UUID uuid);

    StirlingAccount getByAccountName(String accountName);

    List<StirlingAccount> getByAccountType(AccountType accountType);

    List<StirlingAccount> getByLocale(StirlingLocale locale);

    void updateField(StirlingAccount account, String field, Object value);

    boolean emailAddressExists(String emailAddress);
}
