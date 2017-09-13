package com.obadiahpcrowe.stirling.pod.tutor;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.pod.tutor.obj.TutorAssignment;
import com.obadiahpcrowe.stirling.pod.tutor.obj.TutorRequest;
import com.obadiahpcrowe.stirling.pod.tutor.obj.Tutorer;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 3:02 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutor
 * Copyright (c) Obadiah Crowe 2017
 */
public class TutorManager {

    private static TutorManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String deleteRequest(UUID uuid) {
        databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).remove(new HashMap<String, Object>() {{
            put("uuid", uuid.toString());
        }}));

        return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_REQUEST_DELETED, StirlingLocale.ENGLISH));
    }

    public String deleteAssignment(UUID uuid) {
        databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).remove(new HashMap<String, Object>() {{
            put("uuid", uuid.toString());
        }}));

        return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_ASSIGNMENT_DELETED, StirlingLocale.ENGLISH));
    }

    public String requestTutor(StirlingAccount account, String reason, String date, String time) {
        databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).insert(
          new TutorRequest(account.getUuid(), reason, date, time)));

        return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_REQUEST_MADE, account.getLocale(), time, date, reason));
    }

    public String assignTutor(UUID uuid, Tutorer tutorer, String date, String time) {
        return assignTutor(AccountManager.getInstance().getAccount(uuid), tutorer, date, time);
    }

    public String assignTutor(StirlingAccount account, Tutorer tutorer, String date, String time) {
        databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).insert(
          new TutorAssignment(account.getUuid(), tutorer.getUuid(), date, time)));

        return gson.toJson(new StirlingMsg(
          MsgTemplate.TUTOR_ASSIGNED_TO, account.getLocale(), tutorer.getName(), account.getDisplayName(), date, time));
    }

    public String registerAsTutor(StirlingAccount account, List<String> specialities) {
        if (account.getAccountType().equals(AccountType.TUTOR)) {
            if (tutorExists(account.getUuid())) {
                databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).insert(new Tutorer(account, specialities)));
                return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_REGISTERED, account.getLocale()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_ALREADY_REGISTERED, account.getLocale()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "register as a tutor", "tutor"));
        }
    }

    public boolean tutorExists(UUID uuid) {
        try {
            Tutorer tutorer = (Tutorer) databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid.toString());
            }}, Tutorer.class));

            if (tutorer != null) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Tutorer getTutor(String tutorName) {
        try {
            return (Tutorer) databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).get(new HashMap<String, Object>() {{
                put("name", tutorName);
            }}, Tutorer.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Tutorer getTutor(UUID uuid) {
        try {
            return (Tutorer) databaseManager.makeCall(new StirlingCall(databaseManager.getTutorDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid.toString());
            }}, Tutorer.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static TutorManager getInstance() {
        if (instance == null)
            instance = new TutorManager();
        return instance;
    }
}
