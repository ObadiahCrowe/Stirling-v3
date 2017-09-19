package com.obadiahpcrowe.stirling.blogs.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilTime;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 8:39 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.blogs.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class BlogPost {

    private String title;
    private UUID uuid;
    private String poster;
    private String shortDesc;
    private String content;
    private List<AttachableResource> resources;
    private String postDate;
    private String postTime;
    private String editDate;
    private String editTime;

    public BlogPost(StirlingAccount account, String title, String shortDesc, String content, List<AttachableResource> resources) {
        this.title = title;
        this.uuid = UUID.randomUUID();
        this.poster = account.getDisplayName();
        this.shortDesc = shortDesc;
        this.content = content;
        this.resources = resources;
        this.postDate = UtilTime.getInstance().getFriendlyDate();
        this.postTime = UtilTime.getInstance().getFriendlyTime();
        this.editDate = UtilTime.getInstance().getFriendlyDate();
        this.editTime = UtilTime.getInstance().getFriendlyTime();
    }
}
