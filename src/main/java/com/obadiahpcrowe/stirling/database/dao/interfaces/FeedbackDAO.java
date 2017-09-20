package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.feedback.StirlingFeedback;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:12 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface FeedbackDAO extends DAO<StirlingFeedback, ObjectId> {

    StirlingFeedback getByUuid(UUID uuid);

    List<StirlingFeedback> getByType(FeedbackType type);

    List<StirlingFeedback> getAll();
}
