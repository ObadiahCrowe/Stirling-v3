package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.database.dao.interfaces.CalendarDAO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:46 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class CalendarDAOImpl extends BasicDAO<StirlingCalendar, ObjectId> implements CalendarDAO {

    public CalendarDAOImpl(Class<StirlingCalendar> stirlingCalendarClass, Datastore datastore) {
        super(stirlingCalendarClass, datastore);
    }

    @Override
    public StirlingCalendar getByUuid(UUID uuid) {
        Query<StirlingCalendar> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public StirlingCalendar getByOwner(UUID owner) {
        Query<StirlingCalendar> query = createQuery()
          .field("owner").equal(owner);

        return query.get();
    }
}
