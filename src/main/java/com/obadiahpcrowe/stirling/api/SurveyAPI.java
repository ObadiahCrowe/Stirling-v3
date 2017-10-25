package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.surveys.SurveyManager;
import com.obadiahpcrowe.stirling.surveys.obj.StirlingSurvey;
import com.obadiahpcrowe.stirling.surveys.obj.SurveyQuestion;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class SurveyAPI implements APIController {

    private AccountManager accountManager = AccountManager.getInstance();
    private SurveyManager surveyManager = SurveyManager.getInstance();
    private Gson gson = new Gson();

    @CallableAPI(fields = {"accountName", "password", "title", "desc", "targetAudiences", "questions"})
    @RequestMapping(value = "/stirling/v3/surveys/create", method = RequestMethod.GET)
    public String createSurvey(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("title") String title,
                               @RequestParam("desc") String desc,
                               @RequestParam("targetAudiences") String targetAudiences,
                               @RequestParam("questions") String questions) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        List<AccountType> audiences = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(targetAudiences, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                audiences.add(AccountType.valueOf(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException e) {
                return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), targetAudiences, "targetAudiences"));
            }
        }

        Type type = new TypeToken<List<SurveyQuestion>>() {
        }.getType();
        List<SurveyQuestion> surveyQuestions = null;
        try {
            surveyQuestions = gson.fromJson(questions, type);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), questions, "questions"));
        }

        return surveyManager.createSurvey(account, title, desc, audiences, surveyQuestions);
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid"})
    @RequestMapping(value = "/stirling/v3/surveys/delete", method = RequestMethod.GET)
    public String deleteSurvey(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("surveyUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        return surveyManager.deleteSurvey(account, uuid);
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid", "title"})
    @RequestMapping(value = "/stirling/v3/surveys/set/title", method = RequestMethod.GET)
    public String editTitle(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            @RequestParam("surveyUuid") String rawUuid,
                            @RequestParam("title") String title) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        return surveyManager.editSurvey(account, uuid, "title", title);
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid", "desc"})
    @RequestMapping(value = "/stirling/v3/surveys/set/desc", method = RequestMethod.GET)
    public String editDesc(@RequestParam("accountName") String accountName,
                           @RequestParam("password") String password,
                           @RequestParam("surveyUuid") String rawUuid,
                           @RequestParam("desc") String desc) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        return surveyManager.editSurvey(account, uuid, "desc", desc);
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid", "targetAudiences"})
    @RequestMapping(value = "/stirling/v3/surveys/set/audience", method = RequestMethod.GET)
    public String editAudience(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("surveyUuid") String rawUuid,
                               @RequestParam("targetAudiences") String targetAudiences) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        List<AccountType> audiences = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(targetAudiences, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                audiences.add(AccountType.valueOf(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException e) {
                return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), targetAudiences, "targetAudiences"));
            }
        }

        return surveyManager.editSurvey(account, uuid, "targetAudiences", audiences);
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid", "questions"})
    @RequestMapping(value = "/stirling/v3/surveys/set/questions", method = RequestMethod.GET)
    public String editQuestions(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password,
                                @RequestParam("surveyUuid") String rawUuid,
                                @RequestParam("questions") String questions) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        Type type = new TypeToken<List<SurveyQuestion>>() {
        }.getType();
        List<SurveyQuestion> surveyQuestions = null;
        try {
            surveyQuestions = gson.fromJson(questions, type);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), questions, "questions"));
        }

        return surveyManager.editSurvey(account, uuid, "surveyQuestions", surveyQuestions);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/surveys/getAll", method = RequestMethod.GET)
    public String getAllSurveys(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return LocalisationManager.getInstance().translate(gson.toJson(surveyManager.getAllSurveys(account)), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid"})
    @RequestMapping(value = "/stirling/v3/survesys/get", method = RequestMethod.GET)
    public String getSurvey(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            @RequestParam("surveyUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        if (surveyManager.surveyExists(uuid)) {
            StirlingSurvey survey = surveyManager.getSurvey(uuid);

            if (survey.getTargetAudiences().contains(account.getAccountType())) {
                return LocalisationManager.getInstance().translate(gson.toJson(survey), account.getLocale());
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "to view this survey"));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SURVEY_DOES_NOT_EXIST, account.getLocale(), rawUuid));
    }

    @CallableAPI(fields = {"accountName", "password", "surveyUuid", "completedQuestions"})
    @RequestMapping(value = "/stirling/v3/surveys/submit", method = RequestMethod.GET)
    public String submitSurvey(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("surveyUuid") String rawUuid,
                               @RequestParam("completedQuestions") String questions) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "surveyUuid"));
        }

        Type type = new TypeToken<List<SurveyQuestion>>() {
        }.getType();
        List<SurveyQuestion> surveyQuestions = null;
        try {
            surveyQuestions = gson.fromJson(questions, type);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), questions, "completedQuestions"));
        }

        return surveyManager.sendCompletedResponse(account, uuid, surveyQuestions);
    }
}
