package com.obadiahpcrowe.stirling.classes.enums;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.obj.SlotData;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 8:30 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum LessonTimeSlot {

    TIMESLOT_0(0,
      new SlotData("Monday", "8:45", "8:55"),
      new SlotData("Wednesday", "9:50", "10:00"),
      new SlotData("Friday", "8:45", "8:55"),
      new SlotData("Tuesday", "11:15", "12:05"),
      new SlotData("Thursday", "12:30", "13:20")),
    TIMESLOT_1(1,
      new SlotData("Monday", "8:55", "9:50"),
      new SlotData("Tuesday", "12:05", "13:20"),
      new SlotData("Thursday", "14:05", "15:25")),
    TIMESLOT_2(2,
      new SlotData("Monday", "9:50", "10:45"),
      new SlotData("Tuesday", "14:05", "15:25"),
      new SlotData("Thursday", "11:15", "12:30")),
    TIMESLOT_3(3,
      new SlotData("Monday", "11:10", "12:15"),
      new SlotData("Wednesday", "14:05", "15:25"),
      new SlotData("Thursday", "9:50", "10:50")),
    TIMESLOT_4(4,
      new SlotData("Monday", "12:15", "13:20"),
      new SlotData("Wednesday", "10:00", "11:00"),
      new SlotData("Friday", "10:40", "12:00")),
    TIMESLOT_5(5,
      new SlotData("Monday", "14:05", "15:25"),
      new SlotData("Wednesday", "11:25", "12:15"),
      new SlotData("Friday", "8:55", "10:15")),
    TIMESLOT_6(6,
      new SlotData("Tuesday", "8:45", "9:50"),
      new SlotData("Wednesday", "12:15", "13:20"),
      new SlotData("Friday", "12:00", "13:20")),
    TIMESLOT_7(7,
      new SlotData("Tuesday", "9:50", "10:50"),
      new SlotData("Thursday", "8:45", "9:50"),
      new SlotData("Friday", "14:05", "15:25")),
    TIMESLOT_8(8,
      new SlotData("Tuesday", "15:35", "17:15"),
      new SlotData("Thursday", "15:35", "17:15"));

    /*
     * 0 = Cyan
     * 1 = Blue
     * 2 = Green
     * 3 = Pink
     * 4 = Purple
     * 5 = Red
     * 6 = Yellow
     * 7 = Orange
     * 8 = Line 0
     */

    private int slotNumber;
    private List<SlotData> weeklyOccurances;

    LessonTimeSlot(int slotNumber, SlotData... weeklyOccurances) {
        this.slotNumber = slotNumber;
        this.weeklyOccurances = Lists.newArrayList(weeklyOccurances);
    }

    public static LessonTimeSlot getFromString(String timeSlot, String day) {
        String[] parts = timeSlot.split("to");
        CompletableFuture<LessonTimeSlot> slot = new CompletableFuture<>();

        String start = parts[0].replace("to", "").replace(" ", "");
        String end = parts[1].replace("to", "").replace(" ", "");

        if (start.contains("PM")) {
            String startPart = start.replace("PM", "");
            int hour = Integer.valueOf(startPart.split(":")[0]);
            int minute = Integer.valueOf(startPart.split(":")[1]);

            if (hour < 12) {
                hour = hour + 12;
            }

            if (!startPart.split(":")[1].equalsIgnoreCase(String.valueOf(minute))) {
                start = String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                start = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
        } else {
            start = start.replace("AM", "");
        }

        if (end.contains("PM")) {
            String endPart = end.replace("PM", "");
            int hour = Integer.valueOf(endPart.split(":")[0]);
            int minute = Integer.valueOf(endPart.split(":")[1]);

            if (hour < 12) {
                hour = hour + 12;
            }

            if (!endPart.split(":")[1].equalsIgnoreCase(String.valueOf(minute))) {
                end = String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                end = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
        } else {
            end = end.replace("AM", "");
        }

        final String fStart = start;
        final String fEnd = end;

        for (LessonTimeSlot s : LessonTimeSlot.values()) {
            s.getWeeklyOccurances().forEach(data -> {
                if (data.getStartTime().equalsIgnoreCase(fStart) && data.getEndTime().equalsIgnoreCase(fEnd) && data.getDayOfWeek().equalsIgnoreCase(day)) {
                    slot.complete(s);
                }
            });
        }

        return slot.getNow(null);
    }
}
