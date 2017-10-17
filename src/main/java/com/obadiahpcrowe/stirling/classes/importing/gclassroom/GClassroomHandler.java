package com.obadiahpcrowe.stirling.classes.importing.gclassroom;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.interfaces.GenericHandler;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:56 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.gclassroom
 * Copyright (c) Obadiah Crowe 2017
 */
public class GClassroomHandler implements GenericHandler {

    private static GClassroomHandler instance;

    public static GClassroomHandler getInstance() {
        if (instance == null)
            instance = new GClassroomHandler();
        return instance;
    }

    @Override
    public String getAllCourses(ImportAccount account) {
        return null;
    }

    @Override
    public void addCourseToUser(ImportAccount account, Object data) {

    }

    @Override
    public String getCourseAsJson(ImportAccount account, String id) {
        return null;
    }
}
