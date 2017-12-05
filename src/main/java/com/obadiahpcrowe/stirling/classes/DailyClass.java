package com.obadiahpcrowe.stirling.classes;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.classes.enums.AttendanceStatus;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.formatting.NameFormatter;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 3/12/17 at 10:17 AM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class DailyClass {

    private String className;
    private UUID classUuid;
    private String teacher;
    private String room;
    private StirlingDate startTime;
    private StirlingDate endTime;
    private StirlingPostable classNote;
    private StirlingPostable homework;
    private AttendanceStatus attendanceStatus;

    public DailyClass(UUID accountUuid, UUID classUuid, StirlingDate startTime, StirlingDate endTime) {
        StirlingClass stirlingClass = ClassManager.getInstance().getByUuid(classUuid);
        this.className = stirlingClass.getName();
        this.classUuid = classUuid;

        try {
            this.room = stirlingClass.getRoom();
        } catch (NullPointerException ignored) {
        }

        try {
            this.teacher = NameFormatter.formatName(AccountManager.getInstance().getAccount(stirlingClass.getTeachers().get(0)).getAccountName());
        } catch (NullPointerException ignored) {
            this.teacher = NameFormatter.formatName(stirlingClass.getOwners().get(1));
        }

        this.startTime = startTime;
        this.endTime = endTime;

        try {
            stirlingClass.getClassNotes().forEach(n -> {
                if (n.getPostDateTime().getDate().equalsIgnoreCase(startTime.getDate())) {
                    this.classNote = n;
                }
            });
        } catch (NullPointerException ignored) {
        }

        try {
            stirlingClass.getHomework().forEach(hw -> {
                if (hw.getPostDateTime().getDate().equalsIgnoreCase(startTime.getDate())) {
                    this.homework = hw;
                }
            });
        } catch (NullPointerException ignored) {
        }

        stirlingClass.getLessons().forEach(l -> {
            if (l.getStartDateTime().getDate().equalsIgnoreCase(startTime.getDate())) {
                try {
                    this.attendanceStatus = l.getStudentAttendance().get(accountUuid);
                } catch (NullPointerException ignored) {
                    this.attendanceStatus = AttendanceStatus.ROLL_NOT_MARKED;
                }
            }
        });
    }
}
