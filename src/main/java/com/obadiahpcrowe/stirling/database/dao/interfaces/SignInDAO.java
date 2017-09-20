package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.signin.enums.SignOutReason;
import com.obadiahpcrowe.stirling.signin.obj.PresentUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:33 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface SignInDAO extends DAO<PresentUser, ObjectId> {

    PresentUser getByUuid(UUID uuid);

    List<PresentUser> getBySignInReason(SignInReason reason);

    List<PresentUser> getBySignOutReason(SignOutReason reason);

    List<PresentUser> getBySignedIn(boolean isSignedIn);
}
