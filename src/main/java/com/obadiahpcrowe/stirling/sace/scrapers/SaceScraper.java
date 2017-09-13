package com.obadiahpcrowe.stirling.sace.scrapers;

import com.obadiahpcrowe.stirling.sace.obj.SaceCompletion;
import com.obadiahpcrowe.stirling.sace.obj.SaceResult;
import com.obadiahpcrowe.stirling.sace.obj.SaceUser;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.scrapers
 * Copyright (c) Obadiah Crowe 2017
 */
public class SaceScraper {

    private static SaceScraper instance;

    public List<SaceResult> getResults(SaceUser user) {
        return null;
    }

    public List<SaceCompletion> getCompletion(SaceUser user) {
        return null;
    }

    public static SaceScraper getInstance() {
        if (instance == null)
            instance = new SaceScraper();
        return instance;
    }
}
