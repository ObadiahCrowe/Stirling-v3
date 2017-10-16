package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.calendar.obj.CalendarEntry;
import com.obadiahpcrowe.stirling.classes.enums.AttendanceStatus;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 3:25 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class StirlingLesson extends CalendarEntry {

    private Map<UUID, AttendanceStatus> studentAttendance;
    private StirlingPostable classNote;
    private StirlingPostable homework;

    public StirlingLesson(String name, String desc, String room, StirlingDate dateTime, Map<UUID, AttendanceStatus> studentAttendance,
                          StirlingPostable classNote, StirlingPostable homework) {
        super(name, desc, dateTime.getDate(), dateTime.getDate(), dateTime.getTime(), dateTime.getTime(), room);
        this.studentAttendance = studentAttendance;
        this.classNote = classNote;
        this.homework = homework;
    }
}
