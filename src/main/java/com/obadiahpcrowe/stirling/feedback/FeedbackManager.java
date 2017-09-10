package com.obadiahpcrowe.stirling.feedback;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.lang.reflect.Type;
import java.util.HashMap;
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
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String createFeedback(StirlingAccount account, String title, String content,
                                 List<AttachableResource> resources, FeedbackType type) {
        StirlingFeedback feedback = new StirlingFeedback(account, title, content, resources, type);
        databaseManager.makeCall(new StirlingCall(databaseManager.getFeedbackDB()).insert(feedback));

        return gson.toJson(new StirlingMsg(MsgTemplate.FEEDBACK_CREATED, account.getLocale(), feedback.getUuid().toString()));
    }

    public String deleteFeedback(StirlingAccount account, UUID uuid) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            databaseManager.makeCall(new StirlingCall(databaseManager.getFeedbackDB()).remove(new HashMap<String, Object>() {{
                put("uuid", uuid);
            }}));
            return gson.toJson(new StirlingMsg(MsgTemplate.FEEDBACK_DELETED, account.getLocale(), uuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete feedback", "developer"));
    }

    public List<StirlingFeedback> getAllFeedback(StirlingAccount account) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            Type type = new TypeToken<List<StirlingFeedback>>(){}.getType();
            String raw = (String) databaseManager.makeCall(new StirlingCall(databaseManager.getFeedbackDB()).get(
              new HashMap<>(), StirlingFeedback.class));

            return gson.fromJson(raw, type);
        }
        return null;
    }

    public StirlingFeedback getFeedback(StirlingAccount account, UUID uuid) {
        if (account.getAccountType().equals(AccountType.DEVELOPER)) {
            try {
                StirlingFeedback feedback = (StirlingFeedback) databaseManager.makeCall(new StirlingCall(databaseManager.getFeedbackDB())
                  .get(new HashMap<String, Object>() {{
                    put("uuid", uuid);
                }}, StirlingFeedback.class));
                return feedback;
            } catch (NullPointerException e) {
                return null;
            }
        }
        return null;
    }

    public static FeedbackManager getInstance() {
        if (instance == null)
            instance = new FeedbackManager();
        return instance;
    }
}
