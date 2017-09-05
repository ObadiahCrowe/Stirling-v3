package com.obadiahpcrowe.stirling.accounts;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:22 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.accounts
 * Copyright (c) Obadiah Crowe 2017
 */
public class AccountManager {

    private static AccountManager instance;

    public void init() {

    }

    public String createAccount(String username, String emailAddress, String password) {
        return "";
    }

    public Object getField(String fieldName, UUID uuid) {
        return null;
    }

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }
}
