package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.StirlingAnnouncement;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface AnnouncementDAO extends DAO<StirlingAnnouncement, ObjectId> {

    StirlingAnnouncement getByUuid(UUID uuid);

    List<StirlingAnnouncement> getByPoster(String accountName);

    List<StirlingAnnouncement> getByAudience(List<AccountType> targetAudience);

    List<StirlingAnnouncement> getByTags(List<String> tags);

    void updateField(UUID uuid, String field, Object value);
}
