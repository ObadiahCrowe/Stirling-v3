package com.obadiahpcrowe.stirling.resources;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.cloud.CloudManager;
import lombok.Getter;

import java.io.File;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 5:41 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.resources
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class AttachableResource {

    private String owner;
    private String filePath;

    public AttachableResource(String owner, String filePath) {
        this.owner = owner;
        this.filePath = filePath;
    }

    public File getFile() {
        return new File(CloudManager.getInstance().getCloudFolder(
          AccountManager.getInstance().getAccount(owner).getUuid()) + File.separator + filePath);
    }
}
