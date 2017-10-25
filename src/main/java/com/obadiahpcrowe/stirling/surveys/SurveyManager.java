package com.obadiahpcrowe.stirling.surveys;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 8:15 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys
 * Copyright (c) Obadiah Crowe 2017
 */
public class SurveyManager {

    private static SurveyManager instance;

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
        if (account.getAccountType().getAccessLevel() >= 4) {
            surveyDAO.save(new StirlingSurvey(account, title, desc, targetAudiences, questions));
            return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_CREATED, account.getLocale(), title));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create surveys", AccountType.PREFECT.getFriendlyName()));
    }

    public String deleteSurvey(StirlingAccount account, UUID surveyUuid) {
        if (account.getAccountType().getAccessLevel() >= 4) {
            StirlingSurvey survey = getSurvey(surveyUuid);
            if (survey.getOwner().equals(account.getAccountName())) {
                surveyDAO.delete(getSurvey(surveyUuid));
                return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_DELETED, account.getLocale(), survey.getTitle()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER, account.getLocale(), "this survey", "delete it"));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete surveys", AccountType.PREFECT.getFriendlyName()));
    }

    public String editSurvey(StirlingAccount account, UUID surveyUuid, String field, Object value) {
        if (account.getAccountType().getAccessLevel() >= 4) {
            StirlingSurvey survey = getSurvey(surveyUuid);
            if (survey.getOwner().equals(account.getAccountName())) {
                surveyDAO.updateField(survey, field, value);
                return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_UPDATED, account.getLocale(), survey.getTitle(), field));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.NOT_OWNER, account.getLocale(), "this survey", "edit it"));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "edit surveys", AccountType.PREFECT.getFriendlyName()));
    }

    public String sendCompletedResponse(StirlingAccount account, UUID surveyUuid, List<SurveyQuestion> questions) {
        if (!surveyExists(surveyUuid)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_DOES_NOT_EXIST, account.getLocale(), surveyUuid.toString()));
        }

        StirlingSurvey survey = getSurvey(surveyUuid);

        Map<UUID, List<SurveyQuestion>> responses = Maps.newHashMap();
        try {
            responses.putAll(survey.getCompletedResponses());
        } catch (NullPointerException e) {
        }

        if (!responses.containsKey(account.getUuid())) {
            responses.put(account.getUuid(), questions);

            surveyDAO.updateField(survey, "completedResponses", responses);
            return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_SUBMITTED, account.getLocale(), survey.getTitle()));
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_ALREADY_SUBMITTED, account.getLocale()));
    }

    public List<StirlingSurvey> getAllSurveys(StirlingAccount account) {
        List<StirlingSurvey> byUnCompleted = surveyDAO.getByUnCompleted(account);
        List<StirlingSurvey> byAccType = surveyDAO.getByAudience(Collections.singletonList(account.getAccountType()));

        List<StirlingSurvey> surveys = Lists.newArrayList();

        byAccType.forEach(type -> {
            if (byUnCompleted.contains(type)) {
                surveys.add(type);
            }
        });

        return surveys;
    }

    public StirlingSurvey getSurvey(UUID uuid) {
        return surveyDAO.getByUuid(uuid);
    }

    public boolean surveyExists(UUID uuid) {
        return getSurvey(uuid) != null;
    }

    public static SurveyManager getInstance() {
        if (instance == null)
            instance = new SurveyManager();
        return instance;
    }
}
