package com.obadiahpcrowe.stirling.pod.tutorsINACTIVE;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.TutorDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.TutorDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.enums.TutorSpeciality;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.obj.StirlingTutor;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.obj.TutorRequest;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:39 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutorsINACTIVE
 * Copyright (c) Obadiah Crowe 2017
 */
public class TutorManager {

    private MorphiaService morphiaService;
    private TutorDAO tutorDAO;
    private Gson gson;

    public TutorManager() {
        this.morphiaService = new MorphiaService();
        this.tutorDAO = new TutorDAOImpl(StirlingTutor.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public String registerAsTutor(StirlingAccount account, List<TutorSpeciality> specialities) {
        if (account.getAccountType() == AccountType.TUTOR) {
            if (!tutorExists(account.getUuid())) {
                tutorDAO.save(new StirlingTutor(account.getUuid(), specialities));
                return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_REGISTERED, account.getLocale()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_ALREADY_REGISTERED, account.getLocale()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "register as a tutor", AccountType.TUTOR.getFriendlyName()));
    }

    public String unregisterTutor(UUID tutorUuid) {
        if (tutorExists(tutorUuid)) {
            StirlingTutor tutor = getTutor(tutorUuid);
            tutorDAO.delete(tutor);
            return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_UNREGISTERED, AccountManager.getInstance()
              .getAccount(tutorUuid).getLocale(), tutor.getDisplayName()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_DOES_NOT_EXIST, StirlingLocale.ENGLISH, tutorUuid.toString()));
    }

    public String createTutorRequest(StirlingAccount account, UUID tutorUuid, String desc, String date, String time,
                                     List<TutorSpeciality> specialities) {
        StirlingTutor tutor = getTutor(tutorUuid);

        tutorDAO.delete(tutor);
        tutor.getTutorRequests().put(account.getAccountName(), new TutorRequest(account, desc, date, time, specialities));
        tutorDAO.save(tutor);

        return gson.toJson(new StirlingMsg(MsgTemplate.TUTOR_REQUEST_MADE, account.getLocale(), time, date, desc));
    }

    public StirlingTutor getTutor(UUID uuid) {
        return tutorDAO.getByUuid(uuid);
    }

    public boolean tutorExists(UUID uuid) {
        if (getTutor(uuid) == null) {
            return false;
        }
        return true;
    }
}
