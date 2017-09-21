package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:45 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface CalendarDAO extends DAO<StirlingCalendar, ObjectId> {

    StirlingCalendar getByUuid(UUID uuid);

    StirlingCalendar getByOwner(UUID owner);
}
