package com.obadiahpcrowe.stirling.surveys;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.SurveyDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.SurveyDAO;
import com.obadiahpcrowe.stirling.surveys.obj.StirlingSurvey;
import com.obadiahpcrowe.stirling.surveys.obj.SurveyQuestion;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 8:15 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys
 * Copyright (c) Obadiah Crowe 2017
 */
public class SurveyManager {

    private MorphiaService morphiaService;
    private SurveyDAO surveyDAO;
    private Gson gson;

    public SurveyManager() {
        this.morphiaService = new MorphiaService();
        this.surveyDAO = new SurveyDAOImpl(StirlingSurvey.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public String createSurvey(StirlingAccount account, String title, String desc, List<AccountType> targetAudiences,
                               List<SurveyQuestion> questions) {
        if (account.getAccountType().getAccessLevel() >= 9) {
            surveyDAO.save(new StirlingSurvey(account, title, desc, targetAudiences, questions));
            return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_CREATED, account.getLocale(), title));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create surveys", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
    }

    public String deleteSurvey(StirlingAccount account, UUID surveyUuid) {
        if (account.getAccountType().getAccessLevel() >= 9) {
            StirlingSurvey survey = getSurvey(surveyUuid);
            if (survey.getOwner().equals(account.getAccountName())) {
                surveyDAO.delete(getSurvey(surveyUuid));
                return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_DELETED, account.getLocale(), survey.getTitle()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER, account.getLocale(), "this survey", "delete it"));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete surveys", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
    }

    public String editSurvey(StirlingAccount account, UUID surveyUuid, String field, Object value) {
        if (account.getAccountType().getAccessLevel() >= 9) {
            StirlingSurvey survey = getSurvey(surveyUuid);
            if (survey.getOwner().equals(account.getAccountName())) {
                surveyDAO.updateField(survey, field, value);
                return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_UPDATED, account.getLocale(), survey.getTitle(), field));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER, account.getLocale(), "this survey", "delete it"));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "edit surveys", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
    }

    public String sendCompletedResponse(StirlingAccount account, UUID surveyUuid, List<SurveyQuestion> questions) {
        StirlingSurvey survey = getSurvey(surveyUuid);

        if (!survey.getCompletedResponses().containsKey(account.getUuid())) {
            survey.getCompletedResponses().put(account.getUuid(), questions);

            surveyDAO.delete(getSurvey(surveyUuid));
            surveyDAO.save(survey);

            return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_SUBMITTED, account.getLocale(), survey.getTitle()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_ALREADY_SUBMITTED, account.getLocale()));
    }

    public StirlingSurvey getSurvey(UUID uuid) {
        return surveyDAO.getByUuid(uuid);
    }

    public boolean surveyExists(UUID uuid) {
        if (getSurvey(uuid) == null) {
            return false;
        }
        return true;
    }
}
