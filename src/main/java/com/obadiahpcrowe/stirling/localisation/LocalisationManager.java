package com.obadiahpcrowe.stirling.localisation;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:31 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation
 * Copyright (c) Obadiah Crowe 2017
 */
public class LocalisationManager {

    private static LocalisationManager instance;

    public String translate(String originalStr, StirlingLocale locale) {
        return originalStr;
    }

    public static LocalisationManager getInstance() {
        if (instance == null)
            instance = new LocalisationManager();
        return instance;
    }
}
