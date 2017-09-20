package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.calendar.interfaces.CalEntry;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 1:19 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface CalendarDAO extends DAO<CalEntry, ObjectId> {
}
