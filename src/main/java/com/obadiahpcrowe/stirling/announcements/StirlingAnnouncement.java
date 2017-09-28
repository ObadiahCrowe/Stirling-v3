package com.obadiahpcrowe.stirling.announcements;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilTime;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

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
@Entity("announcements")
public class StirlingAnnouncement {

    @Id
    private ObjectId id;

    private String title;
    private String shortDesc;
    private String poster;
    private AnnouncementType type;
    private AttachableResource bannerImage;
    private String content;
    private UUID uuid;
    private StirlingDate postDateTime;
    private StirlingDate editDateTime;
    private List<AttachableResource> resources;
    private List<AccountType> targetAudience;
    private List<String> tags;

    public StirlingAnnouncement() {}

    public StirlingAnnouncement(StirlingAccount account, UUID uuid, String title, String shortDesc, AnnouncementType announcementType,
                                AttachableResource bannerImage, String content, List<AttachableResource> resources,
                                List<AccountType> targetAudience, List<String> tags) {
        StirlingDate date = new StirlingDate(UtilTime.getInstance().getFriendlyDate(), UtilTime.getInstance().getFriendlyTime());
        this.title = title;
        this.shortDesc = shortDesc;
        this.poster = account.getAccountName();
        this.type = announcementType;
        this.bannerImage = bannerImage;
        this.content = content;
        this.uuid = uuid;
        this.postDateTime = date;
        this.editDateTime = date;
        this.resources = resources;
        this.targetAudience = targetAudience;
        this.tags = tags;
    }
}
