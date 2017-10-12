package com.obadiahpcrowe.stirling.classes;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/10/17 at 3:35 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassImporter {

    private static ClassImporter instance;

    public String importFromDaymap() {
        return "";
    }

    public String importFromMoodle() {
        return "";
    }

    public String importFromGoogleClassroom() {
        return "";
    }

    public static ClassImporter getInstance() {
        if (instance == null)
            instance = new ClassImporter();
        return instance;
    }
}
