package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.interfaces.GenericHandler;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:54 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.daymap
 * Copyright (c) Obadiah Crowe 2017
 */
public class DaymapHandler implements GenericHandler {

    private static DaymapHandler instance;

    public static DaymapHandler getInstance() {
        if (instance == null)
            instance = new DaymapHandler();
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
