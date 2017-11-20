package com.obadiahpcrowe.stirling.database.dao;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.dao.interfaces.SurveyDAO;
import com.obadiahpcrowe.stirling.surveys.obj.StirlingSurvey;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 3:42 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class SurveyDAOImpl extends BasicDAO<StirlingSurvey, ObjectId> implements SurveyDAO {

    public SurveyDAOImpl(Class<StirlingSurvey> stirlingSurveyClass, Datastore datastore) {
        super(stirlingSurveyClass, datastore);
    }

    @Override
    public StirlingSurvey getByUuid(UUID uuid) {
        Query<StirlingSurvey> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<StirlingSurvey> getByCompletion(StirlingAccount account) {
        List<StirlingSurvey> query = createQuery().asList();
        List<StirlingSurvey> finalSurveys = Lists.newArrayList();

        query.forEach(survey -> {
            if (survey.getCompletedResponses().containsKey(account.getUuid())) {
                finalSurveys.add(survey);
            }
        });

        return finalSurveys;
    }

    @Override
    public List<StirlingSurvey> getByUnCompleted(StirlingAccount account) {
        List<StirlingSurvey> query = createQuery().asList();
        List<StirlingSurvey> finalSurveys = Lists.newArrayList();

        query.forEach(survey -> {
            if (!survey.getCompletedResponses().containsKey(account.getUuid())) {
                finalSurveys.add(survey);
            }
        });

        return finalSurveys;
    }

    @Override
    public List<StirlingSurvey> getByAudience(List<AccountType> targetAudience) {
        Query<StirlingSurvey> query = createQuery().field("targetAudiences").equal(targetAudience);

        return query.asList();
    }

    @Override
    public void updateField(StirlingSurvey survey, String field, Object value) {
        Query<StirlingSurvey> query = createQuery().field("uuid").equal(survey.getUuid());
        UpdateOperations<StirlingSurvey> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
