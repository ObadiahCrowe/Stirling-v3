package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.StirlingAnnouncement;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AnnouncementDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:44 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class AnnouncementDAOImpl extends BasicDAO<StirlingAnnouncement, ObjectId> implements AnnouncementDAO {

    public AnnouncementDAOImpl(Class<StirlingAnnouncement> announcementClass, Datastore datastore) {
        super(announcementClass, datastore);
    }

    @Override
    public StirlingAnnouncement getByUuid(UUID uuid) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<StirlingAnnouncement> getByPoster(String accountName) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("poster").equal(accountName);

        return query.asList();
    }

    @Override
    public List<StirlingAnnouncement> getByAudience(List<AccountType> targetAudience) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("targetAudience").equal(targetAudience);

        return query.asList();
    }

    @Override
    public List<StirlingAnnouncement> getByTags(List<String> tags) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("tags").equal(tags);

        return query.asList();
    }
}
