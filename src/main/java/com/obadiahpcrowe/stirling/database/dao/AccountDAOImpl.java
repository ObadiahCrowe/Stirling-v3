package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AccountDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:02 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class AccountDAOImpl extends BasicDAO<StirlingAccount, ObjectId> implements AccountDAO {

    public AccountDAOImpl(Class<StirlingAccount> accountClass, Datastore datastore) {
        super(accountClass, datastore);
    }

    @Override
    public StirlingAccount getByUuid(UUID uuid) {
        Query<StirlingAccount> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public StirlingAccount getByAccountName(String accountName) {
        Query<StirlingAccount> query = createQuery()
          .field("accountName").equal(accountName);

        return query.get();
    }

    @Override
    public List<StirlingAccount> getByAccountType(AccountType accountType) {
        Query<StirlingAccount> query = createQuery()
          .field("accountType").equal(accountType);

        return query.asList();
    }

    @Override
    public List<StirlingAccount> getByLocale(StirlingLocale locale) {
        Query<StirlingAccount> query = createQuery()
          .field("locale").equal(locale);

        return query.asList();
    }

    @Override
    public void updateField(StirlingAccount account, String field, Object value) {
        Query<StirlingAccount> query = createQuery().field("uuid").equal(account.getUuid());
        UpdateOperations<StirlingAccount> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
