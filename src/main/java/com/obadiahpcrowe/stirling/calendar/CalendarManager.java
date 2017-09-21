package com.obadiahpcrowe.stirling.calendar;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.CalendarDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.CalendarDAO;

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

    public String createCalendar() {
        return "";
    }

    public String deleteCalendar() {
        return "";
    }

    public StirlingCalendar getCalendar() {
        return null;
    }

    public boolean calendarExists() {
        return false;
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
