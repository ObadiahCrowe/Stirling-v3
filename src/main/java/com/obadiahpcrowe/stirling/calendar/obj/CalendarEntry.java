package com.obadiahpcrowe.stirling.calendar.obj;

import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:31 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.calendar.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class CalendarEntry {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String title;
    private String desc;
    private StirlingDate startDateTime;
    private StirlingDate endDateTime;
    private String location;

    public CalendarEntry() {}

    public CalendarEntry(String title, String desc, String startDate, String endDate, String startTime, String endTime,
                         String location) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.startDateTime = new StirlingDate(startDate, startTime);
        this.endDateTime = new StirlingDate(endDate, endTime);
        this.location = location;
    }
}
