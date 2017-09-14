package com.obadiahpcrowe.stirling.blogs.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 8:35 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.blogs.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingBlog {

    private String title;
    private UUID uuid;
    private String owner;
    private String desc;
    private String bannerImage;
    private List<AccountType> targetAudience;
    private List<BlogPost> blogPosts;

    public StirlingBlog(StirlingAccount account, String title, String desc, AttachableResource bannerImage,
                        List<AccountType> targetAudience) {
        this.title = title;
        this.owner = account.getDisplayName();
        this.uuid = UUID.randomUUID();
        this.desc = desc;
        this.bannerImage = bannerImage.getFile().getPath();
        this.targetAudience = targetAudience;
        this.blogPosts = new ArrayList<>();
    }
}
