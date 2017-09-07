package com.obadiahpcrowe.stirling.accounts;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
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
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String createAccount(String accountName, String emailAddress, String password) {
        if (!accountExists(accountName)) {
            if (password.length() < 8) {
                return gson.toJson(new StirlingMsg(MsgTemplate.FIELD_TOO_SHORT, StirlingLocale.ENGLISH, "password", "8"));
            }

            if (password.length() > 36) {
                return gson.toJson(new StirlingMsg(MsgTemplate.FIELD_TOO_LONG, StirlingLocale.ENGLISH, "password", "36"));
            }

            StirlingAccount account = new StirlingAccount(accountName, emailAddress, password);
            databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).insert(account));

            UtilFile.getInstance().createUserFiles(account.getUuid());

            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_CREATED, StirlingLocale.ENGLISH, accountName));
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_EXISTS, StirlingLocale.ENGLISH, accountName));
        }
    }

    public String deleteAccount(String accountName, String password) {
        if (accountExists(accountName)) {
            if (validCredentials(accountName, password)) {
                return deleteAccount(getAccount(accountName).getUuid());
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }
    }

    public String deleteAccount(UUID uuid) {
        if (accountExists(uuid)) {
            StirlingAccount account = getAccount(uuid);
            databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).remove(new HashMap<String, Object>() {{
                put("uuid", uuid);
            }}));

            UtilFile.getInstance().deleteUserFiles(uuid);

            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DELETED, account.getLocale(), uuid.toString()));
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, uuid.toString()));
        }
    }

    public Object getField(StirlingAccount account, String field) {
        return databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).getField(new HashMap<String, Object>() {{
            put("accountName", account.getAccountName());
        }}, StirlingAccount.class, field));
    }

    public void updateField(StirlingAccount account, String field, Object value) {
        if (accountExists(account.getUuid())) {
            databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).replaceField(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, value, field));
        }
    }

    public boolean accountExists(String accountName) {
        try {
            StirlingAccount account = (StirlingAccount) databaseManager.makeCall(new StirlingCall(
              databaseManager.getAccountDB()).get(new HashMap<String, Object>() {{
                put("accountName", accountName);
            }}, StirlingAccount.class));

            if (account != null) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean accountExists(UUID uuid) {
        try {
            StirlingAccount account = (StirlingAccount) databaseManager.makeCall(new StirlingCall(
              databaseManager.getAccountDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid);
            }}, StirlingAccount.class));

            if (account != null) {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public StirlingAccount getAccount(UUID uuid) {
        if (accountExists(uuid)) {
            return (StirlingAccount) databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid);
            }}, StirlingAccount.class));
        }
        return null;
    }

    public StirlingAccount getAccount(String accountName) {
        if (accountExists(accountName)) {
            return (StirlingAccount) databaseManager.makeCall(new StirlingCall(databaseManager.getAccountDB()).get(new HashMap<String, Object>() {{
                put("accountName", accountName);
            }}, StirlingAccount.class));
        }
        return null;
    }

    public boolean validCredentials(String accountName, String password) {
        if (accountExists(accountName)) {
            StirlingAccount account = getAccount(accountName);
            if (BCrypt.checkpw(password, account.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }
}
