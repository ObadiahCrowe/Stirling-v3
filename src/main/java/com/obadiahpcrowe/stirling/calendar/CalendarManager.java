package com.obadiahpcrowe.stirling.calendar;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.calendar.obj.CalendarEntry;
import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.CalendarDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.CalendarDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 12:45 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.calendar
 * Copyright (c) Obadiah Crowe 2017
 */
public class CalendarManager {

    private MorphiaService morphiaService;
    private CalendarDAO calendarDAO;
    private Gson gson;

    public CalendarManager() {
        morphiaService = new MorphiaService();
        calendarDAO = new CalendarDAOImpl(StirlingCalendar.class, morphiaService.getDatastore());
        gson = new Gson();
    }

    public String createCalendar(UUID uuid, String title, String desc, List<CalendarEntry> entries) {
        if (!calendarExists(uuid)) {
            calendarDAO.save(new StirlingCalendar(uuid, title, desc, entries));
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_CREATED, StirlingLocale.ENGLISH, uuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_ALREADY_EXISTS, StirlingLocale.ENGLISH));
    }

    public String deleteCalendar(UUID owner) {
        if (calendarExists(owner)) {
            calendarDAO.delete(getCalendar(owner));
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DELETED, StirlingLocale.ENGLISH, owner.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, owner.toString()));
    }

    public StirlingCalendar getCalendar(UUID owner) {
        return calendarDAO.getByOwner(owner);
    }

    public boolean calendarExists(UUID owner) {
        if (getCalendar(owner) == null) {
            return false;
        }
        return true;
    }

    public String addCalendarEvent(UUID owner, String title, String desc, String startDate, String endDate,
                                   String startTime, String endTime, String location) {
        if (calendarExists(owner)) {
            StirlingCalendar calendar = getCalendar(owner);
            calendar.getCalendarEntries().add(new CalendarEntry(title, desc, startDate, endDate, startTime, endTime, location));

            calendarDAO.delete(getCalendar(owner));
            calendarDAO.save(calendar);
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_ENTRY_ADDED, StirlingLocale.ENGLISH, owner.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, owner.toString()));
    }

    public String removeCalendarEvent(UUID owner, UUID eventUuid) {
        if (calendarExists(owner)) {
            StirlingCalendar calendar = getCalendar(owner);
            calendar.getCalendarEntries().forEach(entry -> {
                if (entry.getUuid().equals(eventUuid)) {
                    calendar.getCalendarEntries().remove(entry);
                }
            });

            calendarDAO.delete(getCalendar(owner));
            calendarDAO.save(calendar);
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_ENTRY_DELETED, StirlingLocale.ENGLISH, owner.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, owner.toString()));
    }

    public String editEventTitle(UUID owner, UUID eventUuid, String title) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setTitle(title);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "title", eventUuid.toString()));
    }

    public String editEventDesc(UUID owner, UUID eventUuid, String desc) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setDesc(desc);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "description", eventUuid.toString()));
    }

    public String editStartDate(UUID owner, UUID eventUuid, String startDate) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setStartDate(startDate);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "start date", eventUuid.toString()));
    }

    public String editEndDate(UUID owner, UUID eventUuid, String endDate) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setEndDate(endDate);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "end date", eventUuid.toString()));
    }

    public String editStartTime(UUID owner, UUID eventUuid, String startTime) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setStartTime(startTime);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "start time", eventUuid.toString()));
    }

    public String editEndTime(UUID owner, UUID eventUuid, String endTime) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setEndTime(endTime);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "end time", eventUuid.toString()));
    }

    public String editLocation(UUID owner, UUID eventUuid, String location) {
        StirlingCalendar calendar = getCalendar(owner);
        calendar.getCalendarEntries().forEach(event -> {
            if (event.getUuid().equals(eventUuid)) {
                calendar.getCalendarEntries().remove(event);
                event.setLocation(location);
                calendar.getCalendarEntries().add(event);
            }
        });
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_EVENT_FIELD_EDITED, StirlingLocale.ENGLISH, "location", eventUuid.toString()));
    }

    public String setTitle(UUID owner, String title) {
        if (calendarExists(owner)) {
            StirlingCalendar calendar = getCalendar(owner);
            calendar.setTitle(title);

            calendarDAO.delete(getCalendar(owner));
            calendarDAO.save(calendar);
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_TITLE_CHANGED, StirlingLocale.ENGLISH, title));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, owner.toString()));
    }

    public String setDesc(UUID owner, String desc) {
        if (calendarExists(owner)) {
            StirlingCalendar calendar = getCalendar(owner);
            calendar.setDesc(desc);

            calendarDAO.delete(getCalendar(owner));
            calendarDAO.save(calendar);
            return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DESC_CHANGED, StirlingLocale.ENGLISH, desc));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CALENDAR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, owner.toString()));
    }
}
