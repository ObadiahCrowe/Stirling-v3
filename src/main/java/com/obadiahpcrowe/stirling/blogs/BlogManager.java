package com.obadiahpcrowe.stirling.blogs;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.resources.AttachableResource;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 8:22 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.blogs
 * Copyright (c) Obadiah Crowe 2017
 */
public class BlogManager {

    private static BlogManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String createBlog(StirlingAccount account, String title, String desc, AttachableResource bannerImage,
                             List<AccountType> targetAudience) {
        return "";
    }

    public String editTargetAudience(StirlingAccount account, UUID blogUuid, List<AccountType> targetAudience) {
        return "";
    }

    public String editTitle(StirlingAccount account, UUID blogUuid, String title) {
        return "";
    }

    public String editDesc(StirlingAccount account, UUID blogUuid, String desc) {
        return "";
    }

    public String editBanner(StirlingAccount account, UUID blogUuid, AttachableResource bannerImage) {
        return "";
    }

    public String deleteBlog(StirlingAccount account, UUID blogUuid) {
        return "";
    }

    public String createBlogPost(StirlingAccount account, UUID blogUuid) {
        return "";
    }

    public String deleteBlogPost(StirlingAccount account, UUID blogUuid, UUID postUuid) {
        return "";
    }

    public String editBlogPost(StirlingAccount account, UUID blogUuid, UUID postUuid) {
        return "";
    }

    public static BlogManager getInstance() {
        if (instance == null)
            instance = new BlogManager();
        return instance;
    }
}
