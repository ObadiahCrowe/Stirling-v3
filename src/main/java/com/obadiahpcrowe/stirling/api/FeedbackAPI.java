package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.feedback.FeedbackManager;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:07 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class FeedbackAPI implements APIController {

    private Gson gson = new Gson();
    private AccountManager accountManager = AccountManager.getInstance();
    private FeedbackManager feedbackManager = FeedbackManager.getInstance();

    @CallableAPI(fields = {"accountName", "password", "title", "content", "type"})
    @RequestMapping(value = "/stirling/v3/feedback/create", method = RequestMethod.GET)
    public String createFeedback(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("title") String title,
                                 @RequestParam("content") String content,
                                 @RequestParam("type") String rawType) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        FeedbackType type;
        try {
            type = FeedbackType.valueOf(rawType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawType, "type"));
        }

        return feedbackManager.createFeedback(account, title, content, Lists.newArrayList(), type);
    }

    @CallableAPI(fields = {"accountName", "password", "uuid"})
    @RequestMapping(value = "/stirling/v3/feedback/delete", method = RequestMethod.GET)
    public String deleteFeedback(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("uuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), "uuid", rawUuid));
        }

        return feedbackManager.deleteFeedback(account, uuid);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/feedback/getAll", method = RequestMethod.GET)
    public String getAllFeedback(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return LocalisationManager.getInstance().translate(gson.toJson(feedbackManager.getAllFeedback(account)), account.getLocale());
    }
}
