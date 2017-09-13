package com.obadiahpcrowe.stirling.announcements;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 9:30 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.announcements
 * Copyright (c) Obadiah Crowe 2017
 */
public class AnnouncementManager {

    private static AnnouncementManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String postAnnouncement(StirlingAccount account, AnnouncementType type, String title, String shortDesc,
                                   String content, String resources, String targetAudience) {
        if (!type.getAccountTypes().contains(account.getAccountType())) {
            StringBuilder valid = new StringBuilder();
            for (AccountType accountType : type.getAccountTypes()) {
                valid.append(accountType.getFriendlyName()).append(", ");
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "create announcements of this type", valid.toString()));
        }

        StringTokenizer tokenizer = new StringTokenizer(targetAudience, ",");
        List<AccountType> audience = new ArrayList<>();
        while (tokenizer.hasMoreElements()) {
            audience.add(AccountType.valueOf(tokenizer.nextElement().toString()));
        }

        List<AttachableResource> resourcesList = new ArrayList<>();
        StringTokenizer fileTokenizer = new StringTokenizer(resources, ",");
        while (tokenizer.hasMoreElements()) {
            resourcesList.add(new AttachableResource(account.getAccountName(), fileTokenizer.nextElement().toString()));
        }

        databaseManager.makeCall(new StirlingCall(databaseManager.getAnnouncementDB()).insert(
          new StirlingAnnouncement(account, title, shortDesc, type, content, resourcesList, audience)));

        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_CREATED, account.getLocale(), title));
    }

    public String deleteAnnouncement(StirlingAccount account, UUID uuid) {
        StirlingAnnouncement announcement = getAnnouncement(uuid);
        if (!announcement.getType().getAccountTypes().contains(account.getAccountType()) ||
          account.getAccountType().getAccessLevel() < 9) {
            StringBuilder valid = new StringBuilder();
            for (AccountType accountType : announcement.getType().getAccountTypes()) {
                if (account.getAccountType().equals(AccountType.SUB_SCHOOL_LEADER)) {
                    continue;
                }
                valid.append(accountType.getFriendlyName()).append(", ");
            }
            valid.append(AccountType.SUB_SCHOOL_LEADER);

            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "delete this announcement", valid.toString()));
        }

        databaseManager.makeCall(new StirlingCall(databaseManager.getAnnouncementDB()).remove(new HashMap<String, Object>() {{
            put("uuid", uuid.toString());
        }}));

        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_DELETED, account.getLocale(), announcement.getTitle()));
    }

    public String editAnnouncement(StirlingAccount account, UUID uuid, String field, Object value) {
        StirlingAnnouncement announcement = getAnnouncement(uuid);
        if (!announcement.getType().getAccountTypes().contains(account.getAccountType())) {
            StringBuilder valid = new StringBuilder();
            for (AccountType accountType : announcement.getType().getAccountTypes()) {
                valid.append(accountType.getFriendlyName()).append(", ");
            }

            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "edit this announcement", valid.toString()));
        }

        databaseManager.makeCall(new StirlingCall(databaseManager.getAnnouncementDB()).replaceField(new HashMap<String, Object>() {{
            put("uuid", uuid.toString());
        }}, field, value));

        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_EDITED, account.getLocale(), announcement.getTitle()));
    }

    public StirlingAnnouncement getAnnouncement(UUID uuid) {
        return (StirlingAnnouncement) databaseManager.makeCall(new StirlingCall(databaseManager.getAnnouncementDB()).get(
          new HashMap<String, Object>() {{
            put("uuid", uuid.toString());
        }}, StirlingAnnouncement.class));
    }

    public List<StirlingAnnouncement> getAnnouncements(StirlingAccount account) {
        Type type = new TypeToken<List<StirlingAnnouncement>>(){}.getType();
        String raw = (String) databaseManager.makeCall(new StirlingCall(databaseManager.getAnnouncementDB()).get(
          new HashMap<>(), StirlingAnnouncement.class));

        List<StirlingAnnouncement> announcements = gson.fromJson(raw, type);
        for (StirlingAnnouncement announcement : announcements) {
            if (!announcement.getTargetAudience().contains(account.getAccountType())) {
                announcements.remove(announcement);
            }
        }
        return announcements;
    }

    public static AnnouncementManager getInstance() {
        if (instance == null)
            instance = new AnnouncementManager();
        return instance;
    }
}
