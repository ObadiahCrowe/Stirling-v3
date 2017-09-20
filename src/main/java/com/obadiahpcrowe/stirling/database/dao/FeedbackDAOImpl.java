package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.FeedbackDAO;
import com.obadiahpcrowe.stirling.feedback.StirlingFeedback;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:18 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class FeedbackDAOImpl extends BasicDAO<StirlingFeedback, ObjectId> implements FeedbackDAO {

    public FeedbackDAOImpl(Class<StirlingFeedback> feedbackClass, Datastore datastore) {
        super(feedbackClass, datastore);
    }

    @Override
    public StirlingFeedback getByUuid(UUID uuid) {
        Query<StirlingFeedback> feedback = createQuery()
          .field("uuid").equal(uuid);

        return feedback.get();
    }

    @Override
    public List<StirlingFeedback> getByType(FeedbackType type) {
        Query<StirlingFeedback> feedbacks = createQuery()
          .field("type").equal(type);

        return feedbacks.asList();
    }

    @Override
    public List<StirlingFeedback> getAll() {
        return createQuery().asList();
    }
}
