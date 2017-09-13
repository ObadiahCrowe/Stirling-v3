package com.obadiahpcrowe.stirling.calendar.obj;

import com.obadiahpcrowe.stirling.calendar.interfaces.CalEntry;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 7:59 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.calendar.obj
 * Copyright (c) Obadiah Crowe 2017
 */
public class ExamCalEntry implements CalEntry {

    private String name;
    private String desc;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String location;
    private @Getter UUID uuid;

    public ExamCalEntry(String name, String desc, String startDate, String startTime, String endDate, String endTime, String loc) {
        this.name = name;
        this.desc = desc;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = loc;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public String getEndDate() {
        return endDate;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    @Override
    public String getLoc() {
        return location;
    }
}
