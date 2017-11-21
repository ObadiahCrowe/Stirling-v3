package com.obadiahpcrowe.stirling.pod.signin;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.PodSignInDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.PodSignInDAO;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import com.obadiahpcrowe.stirling.pod.signin.obj.PodUser;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:04 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodSignInManager {

    private static PodSignInManager instance;

    private MorphiaService morphiaService;
    private PodSignInDAO podSignInDAO;
    private Gson gson = new Gson();

    public PodSignInManager() {
        this.morphiaService = new MorphiaService();
        this.podSignInDAO = new PodSignInDAOImpl(PodUser.class, morphiaService.getDatastore());
    }

    public String setStudentId(StirlingAccount account, int studentId) {
        PodUser podUser = new PodUser(account.getUuid(), studentId, false);
        if (!studentIdExists(account)) {
            podSignInDAO.save(podUser);
        } else {
            podSignInDAO.updateField(getPodUser(account), "studentId", studentId);
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_ID_ADDED, account.getLocale(), account.getDisplayName(),
          String.valueOf(studentId)));
    }

    public boolean studentIdExists(StirlingAccount account) {
        PodUser user = getPodUser(account);
        if (user == null) {
            return false;
        }

        if (String.valueOf(user.getStudentId()).length() <= 0) {
            return false;
        }

        return true;
    }

    public PodUser getPodUser(StirlingAccount account) {
        return podSignInDAO.getByUuid(account.getUuid());
    }

    public String signInToPod(StirlingAccount account, PodLine line, String assigningTeacher, PodReason reason) {
        if (studentIdExists(account)) {
            PodUser podUser = getPodUser(account).setSignInOptions(line, assigningTeacher, reason);

            podSignInDAO.updateField(podUser, "signedIn", true);

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
