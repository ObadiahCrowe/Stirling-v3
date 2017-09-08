package com.obadiahpcrowe.stirling.announcements.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 9:24 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.announcements
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class AnnouncementResource {

    private String title;
    private UUID owner;
    private String filePath;

    public AnnouncementResource(StirlingAccount account, String title, File file) {
        this.title = title;
        this.owner = account.getUuid();
        this.filePath = file.getPath();
    }
}
