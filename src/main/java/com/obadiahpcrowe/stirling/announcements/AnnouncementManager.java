package com.obadiahpcrowe.stirling.announcements;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.AnnouncementDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AnnouncementDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

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

    private final MorphiaService morphiaService;
    private final AnnouncementDAO announcementDAO;
    private final Gson gson;

    private AnnouncementManager() {
        this.morphiaService = new MorphiaService();
        this.announcementDAO = new AnnouncementDAOImpl(StirlingAnnouncement.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public String postAnnouncement(StirlingAccount account, UUID uuid, AnnouncementType type, AttachableResource bannerImage, String title,
                                   String shortDesc, String content, String resources, String targetAudience, String tags) {
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
        if (resources != null) {
            StringTokenizer fileTokenizer = new StringTokenizer(resources, ",");
            while (tokenizer.hasMoreElements()) {
                resourcesList.add(new AttachableResource(account.getUuid(), fileTokenizer.nextElement().toString(), ARType.ANNOUNCEMENT));
            }
        }

        List<String> tagsList = new ArrayList<>();
        if (tags != null) {
            StringTokenizer tagTokenizer = new StringTokenizer(tags, ",");
            while (tokenizer.hasMoreElements()) {
                tagsList.add(tagTokenizer.nextElement().toString());
            }
        }

        StirlingAnnouncement announcement = new StirlingAnnouncement(account, uuid, title, shortDesc, type, bannerImage,
          content, resourcesList, audience, tagsList);

        announcementDAO.save(announcement);

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

        announcementDAO.delete(announcement);

        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_DELETED, account.getLocale(), announcement.getTitle()));
    }

    public String updateField(UUID uuid, String field, Object value) {
        if (announcementExists(uuid)) {
            announcementDAO.updateField(uuid, field, value);
            return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_EDITED, StirlingLocale.ENGLISH, uuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, uuid.toString()));
    }

    public StirlingAnnouncement getAnnouncement(UUID uuid) {
        return announcementDAO.getByUuid(uuid);
    }

    public boolean announcementExists(UUID uuid) {
        if (getAnnouncement(uuid) == null) {
            return false;
        }
        return true;
    }

    public List<StirlingAnnouncement> getAnnouncements(StirlingAccount account) {
        return announcementDAO.getByAudience(Arrays.asList(account.getAccountType()));
    }

    public static AnnouncementManager getInstance() {
        if (instance == null)
            instance = new AnnouncementManager();
        return instance;
    }
}
