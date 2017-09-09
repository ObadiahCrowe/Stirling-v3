package com.obadiahpcrowe.stirling.laptop;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.laptop
 * Copyright (c) Obadiah Crowe 2017
 */
public class LaptopManager {

    private static LaptopManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    // TODO: 9/9/17 this
    public String setLaptop(StirlingAccount account, String name) {
        return "";
    }

    public String getLaptopStatus(StirlingAccount account) {
        return "";
    }

    public static LaptopManager getInstance() {
        if (instance == null)
            instance = new LaptopManager();
        return instance;
    }
}
