package com.obadiahpcrowe.stirling.classes.importing.moodle;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.interfaces.GenericHandler;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:55 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.moodle
 * Copyright (c) Obadiah Crowe 2017
 */
public class MoodleHandler implements GenericHandler {

    private static MoodleHandler instance;

    public static MoodleHandler getInstance() {
        if (instance == null)
            instance = new MoodleHandler();
        return instance;
    }

    @Override
    public List<ImportableClass> getAllCourses(ImportAccount account) {
        return null;
    }

    @Override
    public void addCourseToUser(ImportAccount account, List<? extends ImportableClass> classes) {

    }

    @Override
    public String getCourseAsJson(ImportAccount account, String id) {
        return null;
    }
}
