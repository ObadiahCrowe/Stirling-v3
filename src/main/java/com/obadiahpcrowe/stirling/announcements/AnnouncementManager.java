package com.obadiahpcrowe.stirling.announcements;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.AnnouncementDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AnnouncementDAO;
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

    private MorphiaService morphiaService;
    private AnnouncementDAO announcementDAO;
    private Gson gson = new Gson();

    public AnnouncementManager() {
        this.morphiaService = new MorphiaService();
        this.announcementDAO = new AnnouncementDAOImpl(StirlingAnnouncement.class, morphiaService.getDatastore());
    }

    public String postAnnouncement(StirlingAccount account, AnnouncementType type, String bannerImage, String title,
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
        StringTokenizer fileTokenizer = new StringTokenizer(resources, ",");
        while (tokenizer.hasMoreElements()) {
            resourcesList.add(new AttachableResource(account.getAccountName(), fileTokenizer.nextElement().toString()));
        }

        List<String> tagsList = new ArrayList<>();
        StringTokenizer tagTokenizer = new StringTokenizer(tags, ",");
        while (tokenizer.hasMoreElements()) {
            tagsList.add(tagTokenizer.nextElement().toString());
        }

        announcementDAO.save(new StirlingAnnouncement(account, title, shortDesc, type,
          new AttachableResource(account.getAccountName(), bannerImage), content, resourcesList, audience, tagsList));

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

    public StirlingAnnouncement getAnnouncement(UUID uuid) {
        return announcementDAO.getByUuid(uuid);
    }

    public List<StirlingAnnouncement> getAnnouncements(StirlingAccount account) {
        return announcementDAO.getByAudience(Arrays.asList(account.getAccountType()));
    }
}
