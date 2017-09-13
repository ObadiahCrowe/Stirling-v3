package com.obadiahpcrowe.stirling.surveys;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 8:15 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys
 * Copyright (c) Obadiah Crowe 2017
 */
public class SurveyManager {

    private static SurveyManager instance;

    public static SurveyManager getInstance() {
        if (instance == null)
            instance = new SurveyManager();
        return instance;
    }
}
