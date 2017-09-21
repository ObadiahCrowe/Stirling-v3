package com.obadiahpcrowe.stirling.calendar;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:31 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.calendar
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
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String location;

    public CalendarEntry() {}

    public CalendarEntry(String title, String desc, String startDate, String endDate, String startTime, String endTime,
                         String location) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
}
