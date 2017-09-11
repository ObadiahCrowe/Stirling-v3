package com.obadiahpcrowe.stirling.pod.signin;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import com.obadiahpcrowe.stirling.pod.signin.obj.PodUser;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.HashMap;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:04 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodSignInManager {

    private static PodSignInManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String setStudentId(StirlingAccount account, int studentId) {
        PodUser podUser = new PodUser(account.getUuid(), studentId);
        if (!studentIdExists(account)) {
            databaseManager.makeCall(new StirlingCall(databaseManager.getPodDB())
              .insert(podUser));
        } else {
            databaseManager.makeCall(new StirlingCall(databaseManager.getPodDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, podUser));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_ID_ADDED, account.getLocale(), account.getDisplayName(),
          String.valueOf(studentId)));
    }

    public boolean studentIdExists(StirlingAccount account) {
        try {
            PodUser user = (PodUser) databaseManager.makeCall(new StirlingCall(databaseManager.getPodDB())
              .get(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, PodUser.class));

            if (user != null) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public String signInToPod(StirlingAccount account, PodLine line, PodReason reason) {
        if (studentIdExists(account)) {
            // TODO: 11/9/17 this
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_ID_NOT_FOUND, account.getLocale(), account.getDisplayName()));
    }

    public static PodSignInManager getInstance() {
        if (instance == null)
            instance = new PodSignInManager();
        return instance;
    }
}
