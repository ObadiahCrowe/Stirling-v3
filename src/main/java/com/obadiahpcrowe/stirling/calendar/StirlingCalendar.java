package com.obadiahpcrowe.stirling.calendar;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:32 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.calendar
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
@Entity("calendars")
public class StirlingCalendar {

    @Id
    private ObjectId id;

    private UUID owner;
    private UUID uuid;
    private String title;
    private String desc;
    private List<CalendarEntry> calendarEntries;

    public StirlingCalendar() {}

    public StirlingCalendar(StirlingAccount account, String title, String desc, List<CalendarEntry> calendarEntries) {
        this.owner = account.getUuid();
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.calendarEntries = calendarEntries;
    }
}
