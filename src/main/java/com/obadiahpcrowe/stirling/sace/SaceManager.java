package com.obadiahpcrowe.stirling.sace;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 6:26 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace
 * Copyright (c) Obadiah Crowe 2017
 */
public class SaceManager {

    private static SaceManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String setSaceId(StirlingAccount account, String saceId) {
        return "";
    }

    public String getSaceResults(StirlingAccount account) {
        return "";
    }

    public String getSaceCompletion(StirlingAccount account) {
        return "";
    }

    public boolean isSaceIdPresent(StirlingAccount account) {
        return false;
    }

    public static SaceManager getInstance() {
        if (instance == null)
            instance = new SaceManager();
        return instance;
    }
}
