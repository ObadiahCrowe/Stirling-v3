package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.StirlingAnnouncement;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AnnouncementDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

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
    public List<StirlingAnnouncement> getByAccountType(AccountType accountType) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("targetAudience").equal(accountType);

        return query.asList();
    }

    @Override
    public List<StirlingAnnouncement> getByTags(List<String> tags) {
        Query<StirlingAnnouncement> query = createQuery()
          .field("tags").equal(tags);

        return query.asList();
    }

    @Override
    public void updateField(UUID uuid, String field, Object value) {
        Query<StirlingAnnouncement> query = createQuery().field("uuid").equal(uuid);
        UpdateOperations<StirlingAnnouncement> updateOps = createUpdateOperations().set(field, value);

        update(query, updateOps);
    }
}
