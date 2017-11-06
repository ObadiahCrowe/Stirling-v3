package com.obadiahpcrowe.stirling.classes.importing.moodle;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:55 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.moodle
 * Copyright (c) Obadiah Crowe 2017
 */
public class MoodleHandler {

    private static MoodleHandler instance;

    public static MoodleHandler getInstance() {
        if (instance == null)
            instance = new MoodleHandler();
        return instance;
    }
}
