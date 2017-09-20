package com.obadiahpcrowe.stirling.pod.signin;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import com.obadiahpcrowe.stirling.pod.signin.obj.PodUser;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;
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
    private MorphiaService morphiaService = MorphiaService.getInstance();
    private Gson gson = new Gson();

    public String setStudentId(StirlingAccount account, int studentId) {
        PodUser podUser = new PodUser(account.getUuid(), studentId, false);
        if (!studentIdExists(account)) {
            morphiaService.makeCall(new StirlingCall(morphiaService.getPodDB())
              .insert(podUser));
        } else {
            morphiaService.makeCall(new StirlingCall(morphiaService.getPodDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, podUser));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_ID_ADDED, account.getLocale(), account.getDisplayName(),
          String.valueOf(studentId)));
    }

    public boolean studentIdExists(StirlingAccount account) {
        try {
            PodUser user = (PodUser) morphiaService.makeCall(new StirlingCall(morphiaService.getPodDB())
              .get(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, PodUser.class));

            if (user != null) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public PodUser getPodUser(StirlingAccount account) {
        try {
            return  (PodUser) morphiaService.makeCall(new StirlingCall(morphiaService.getPodDB()).get(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, PodUser.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String signInToPod(StirlingAccount account, PodLine line, String assigningTeacher, PodReason reason) {
        if (studentIdExists(account)) {
            PodUser podUser = getPodUser(account).setSignInOptions(line, assigningTeacher, reason);
            podUser.setSignedIn(true);

            morphiaService.makeCall(new StirlingCall(morphiaService.getPodDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, podUser));

            try {
                PodScraper.getInstance().signIn(podUser.getStudentId(), line, assigningTeacher, reason);
            } catch (IOException e) {
                return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "working with POD compatibility layer"));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.POD_SIGN_IN, account.getLocale(), reason.getFriendlyName()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_ID_NOT_FOUND, account.getLocale(), account.getDisplayName()));
    }

    public static PodSignInManager getInstance() {
        if (instance == null)
            instance = new PodSignInManager();
        return instance;
    }
}
