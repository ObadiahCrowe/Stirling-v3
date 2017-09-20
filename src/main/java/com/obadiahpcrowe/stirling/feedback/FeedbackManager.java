package com.obadiahpcrowe.stirling.feedback;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.FeedbackDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.FeedbackDAO;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:14 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.feedback
 * Copyright (c) Obadiah Crowe 2017
 */
public class FeedbackManager {

    private static FeedbackManager instance;
    private MorphiaService morphiaService;
    private FeedbackDAO feedbackDAO;
    private Gson gson = new Gson();

    public FeedbackManager() {
        this.morphiaService = new MorphiaService();
        this.feedbackDAO = new FeedbackDAOImpl(StirlingFeedback.class, morphiaService.getDatastore());
    }

    public String createFeedback(StirlingAccount account, String title, String content,
                                 List<AttachableResource> resources, FeedbackType type) {
        StirlingFeedback feedback = new StirlingFeedback(account, title, content, resources, type);
        feedbackDAO.save(feedback);

        return gson.toJson(new StirlingMsg(MsgTemplate.FEEDBACK_CREATED, account.getLocale(), feedback.getUuid().toString()));
    }

    public String deleteFeedback(StirlingAccount account, UUID uuid) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            feedbackDAO.delete(getFeedback(account, uuid));
            return gson.toJson(new StirlingMsg(MsgTemplate.FEEDBACK_DELETED, account.getLocale(), uuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete feedback", "developer"));
    }

    public List<StirlingFeedback> getAllFeedback(StirlingAccount account) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            return feedbackDAO.getAll();
        }
        return null;
    }

    public StirlingFeedback getFeedback(StirlingAccount account, UUID uuid) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            return feedbackDAO.getByUuid(uuid);
        }
        return null;
    }

    public static FeedbackManager getInstance() {
        if (instance == null)
            instance = new FeedbackManager();
        return instance;
    }
}
