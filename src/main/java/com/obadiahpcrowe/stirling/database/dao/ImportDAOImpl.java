package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 3:27 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class ImportDAOImpl extends BasicDAO<ImportAccount, ObjectId> implements ImportDAO {

    public ImportDAOImpl(Class<ImportAccount> importClass, Datastore datastore) {
        super(importClass, datastore);
    }

    @Override
    public ImportAccount getByUuid(UUID uuid) {
        Query<ImportAccount> query = createQuery()
          .field("accountUuid").equal(uuid);

        return query.get();
    }

    @Override
    public ImportAccount getByStirlingAcc(StirlingAccount account) {
        Query<ImportAccount> query = createQuery()
          .field("accountUuid").equal(account.getUuid());

        return query.get();
    }

    @Override
    public ImportAccount getByUsername(String username) {
        Query<ImportAccount> query = createQuery()
          .field("accountUuid").equal(AccountManager.getInstance().getAccount(username).getUuid());

        return query.get();
    }

    @Override
    public List<ImportAccount> getAllImportAccounts() {
        Query<ImportAccount> query = createQuery();

        return query.asList();
    }

    @Override
    public void updateField(ImportAccount account, String field, Object value) {
        Query<ImportAccount> query = createQuery().field("accountUuid").equal(account.getAccountUuid());
        UpdateOperations<ImportAccount> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
