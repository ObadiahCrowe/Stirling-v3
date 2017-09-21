package com.obadiahpcrowe.stirling.calendar;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
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

    public String addCalendarEvent() {
        return "";
    }

    public String removeCalendarEvent() {
        return "";
    }

    public String editCalendarEvent() {
        return "";
    }

    public String setTitle() {
        return "";
    }

    public String setDesc() {
        return "";
    }
}
