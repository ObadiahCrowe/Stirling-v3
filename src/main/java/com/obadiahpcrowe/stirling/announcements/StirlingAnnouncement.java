package com.obadiahpcrowe.stirling.announcements;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilTime;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 10:50 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.announcements
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingAnnouncement {

    private String title;
    private String shortDesc;
    private String poster;
    private AnnouncementType type;
    private AttachableResource bannerImage;
    private String content;
    private UUID uuid;
    private String postTime;
    private String postDate;
    private String editTime;
    private String editDate;
    private List<AttachableResource> resources;
    private List<AccountType> targetAudience;
    private List<String> tags;

    public StirlingAnnouncement(StirlingAccount account, String title, String shortDesc, AnnouncementType announcementType,
                                AttachableResource bannerImage, String content, List<AttachableResource> resources,
                                List<AccountType> targetAudience, List<String> tags) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.poster = account.getAccountName();
        this.type = announcementType;
        this.bannerImage = bannerImage;
        this.content = content;
        this.uuid = UUID.randomUUID();
        this.postTime = UtilTime.getInstance().getFriendlyTime();
        this.postDate = UtilTime.getInstance().getFriendlyDate();
        this.editTime = UtilTime.getInstance().getFriendlyTime();
        this.editDate = UtilTime.getInstance().getFriendlyDate();
        this.resources = resources;
        this.targetAudience = targetAudience;
        this.tags = tags;
    }
}
