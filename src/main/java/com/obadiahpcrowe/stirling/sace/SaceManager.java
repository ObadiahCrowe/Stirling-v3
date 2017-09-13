package com.obadiahpcrowe.stirling.sace;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.sace.obj.SaceUser;
import com.obadiahpcrowe.stirling.sace.scrapers.SaceScraper;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.HashMap;

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

    public String setSaceCreds(StirlingAccount account, String saceId, String sacePassword) {
        SaceUser user = new SaceUser(account.getUuid(), saceId, sacePassword);
        if (isSaceUserPresent(account)) {
            databaseManager.makeCall(new StirlingCall(databaseManager.getSaceDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, user));
        } else {
            databaseManager.makeCall(new StirlingCall(databaseManager.getSaceDB()).insert(user));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SACE_CREDS_SET, account.getLocale(), account.getDisplayName()));
    }

    public String getSaceResults(StirlingAccount account) {
        return gson.toJson(SaceScraper.getInstance().getResults(getSaceUser(account)));
    }

    public String getSaceCompletion(StirlingAccount account) {
        return gson.toJson(SaceScraper.getInstance().getCompletion(getSaceUser(account)));
    }

    private boolean isSaceUserPresent(StirlingAccount account) {
        try {
            SaceUser user = (SaceUser) databaseManager.makeCall(new StirlingCall(databaseManager.getSaceDB()).get(
              new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, SaceUser.class));

            if (user != null) {
                return true;
            }
        } catch (NullPointerException ignored) { }
        return false;
    }

    public SaceUser getSaceUser(StirlingAccount account) {
        try {
            SaceUser user = (SaceUser) databaseManager.makeCall(new StirlingCall(databaseManager.getSaceDB()).get(
              new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, SaceUser.class));

            return user;
        } catch (NullPointerException ignored) { }
        return null;
    }

    public String getSaceId(StirlingAccount account) {
        if (isSaceUserPresent(account)) {
            SaceUser user = getSaceUser(account);
            return user.getSaceId();
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SACE_CREDS_NOT_FOUND, account.getLocale()));
    }

    public static SaceManager getInstance() {
        if (instance == null)
            instance = new SaceManager();
        return instance;
    }
}
