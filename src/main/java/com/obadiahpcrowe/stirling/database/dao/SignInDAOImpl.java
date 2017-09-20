package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.SignInDAO;
import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.signin.enums.SignOutReason;
import com.obadiahpcrowe.stirling.signin.obj.PresentUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:58 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class SignInDAOImpl extends BasicDAO<PresentUser, ObjectId> implements SignInDAO {

    public SignInDAOImpl(Class<PresentUser> presentUserClass, Datastore datastore) {
        super(presentUserClass, datastore);
    }

    @Override
    public PresentUser getByUuid(UUID uuid) {
        Query<PresentUser> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<PresentUser> getBySignInReason(SignInReason reason) {
        Query<PresentUser> query = createQuery()
          .field("signInReason").equal(reason);

        return query.asList();
    }

    @Override
    public List<PresentUser> getBySignOutReason(SignOutReason reason) {
        Query<PresentUser> query = createQuery()
          .field("signOutReason").equal(reason);

        return query.asList();
    }

    @Override
    public List<PresentUser> getBySignedIn(boolean isSignedIn) {
        Query<PresentUser> query = createQuery()
          .field("present").equal(isSignedIn);

        return query.asList();
    }
}
