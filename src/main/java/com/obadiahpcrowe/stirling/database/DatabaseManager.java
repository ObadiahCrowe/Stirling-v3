package com.obadiahpcrowe.stirling.database;

import com.mongodb.ServerAddress;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.database.obj.StirlingDatabase;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 5:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database
 * Copyright (c) Obadiah Crowe 2017
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private ServerAddress address = new ServerAddress("localhost", 27017);
    private String dbName = "stirling";

    @Getter
    private final StirlingDatabase accountDB = new StirlingDatabase(address, dbName, "accounts", null);

    @Getter
    private final StirlingDatabase announcementDB = new StirlingDatabase(address, dbName, "announcements", null);

    @Getter
    private final StirlingDatabase feedbackDB = new StirlingDatabase(address, dbName, "feedback", null);

    @Getter
    private final StirlingDatabase notesDB = new StirlingDatabase(address, dbName, "notes", null);

    @Getter
    private final StirlingDatabase podDB = new StirlingDatabase(address, dbName, "pod", null);

    @Getter
    private final StirlingDatabase calendarDB = new StirlingDatabase(address, dbName, "calendar", null);

    @Getter
    private final StirlingDatabase signInDB = new StirlingDatabase(address, dbName, "signin", null);

    @Getter
    private final StirlingDatabase messagingDB = new StirlingDatabase(address, dbName, "messaging", null);

    @Getter
    private final StirlingDatabase saceDB = new StirlingDatabase(address, dbName, "sace", null);

    @Getter
    private final StirlingDatabase tutorDB = new StirlingDatabase(address, dbName, "tutor", null);

    @Getter
    private final StirlingDatabase progressMarkerDB = new StirlingDatabase(address, dbName, "progmarkers", null);

    @Getter
    private final StirlingDatabase surveyDB = new StirlingDatabase(address, dbName, "surveys", null);

    @Getter
    private final StirlingDatabase blogDB = new StirlingDatabase(address, dbName, "blogs", null);

    @Getter
    private final StirlingDatabase attendanceDB = new StirlingDatabase(address, dbName, "attendance", null);

    @Getter
    private final StirlingDatabase classesDB = new StirlingDatabase(address, dbName, "classes", null);

    public Object makeCall(StirlingCall call) {
        return null;
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }
}
