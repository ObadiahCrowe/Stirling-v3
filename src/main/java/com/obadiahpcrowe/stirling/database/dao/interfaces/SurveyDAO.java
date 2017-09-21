package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.surveys.obj.StirlingSurvey;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 3:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface SurveyDAO extends DAO<StirlingSurvey, ObjectId> {

    StirlingSurvey getByUuid(UUID uuid);

    List<StirlingSurvey> getByCompletion(StirlingAccount account);

    List<StirlingSurvey> getByUnCompleted(StirlingAccount account);

    List<StirlingSurvey> getByAudience(List<AccountType> targetAudience);

    void updateField(StirlingSurvey survey, String field, Object value);
}
