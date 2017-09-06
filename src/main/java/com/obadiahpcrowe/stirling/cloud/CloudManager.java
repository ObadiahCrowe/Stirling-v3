package com.obadiahpcrowe.stirling.cloud;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.cloud.interfaces.CloudDocument;
import com.obadiahpcrowe.stirling.cloud.interfaces.CloudMedia;
import com.obadiahpcrowe.stirling.util.UtilFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:51 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud
 * Copyright (c) Obadiah Crowe 2017
 */
public class CloudManager {

    private static CloudManager instance;
    private File home = UtilFile.getInstance().getStorageLoc();

    public File getCloudFolder(StirlingAccount account) {
        return getCloudFolder(account.getUuid());
    }

    public File getCloudFolder(UUID uuid) {
        File cloud = new File(home + File.separator + "UserData" + File.separator + uuid.toString() +
          File.separator + "Cloud");

        if (!cloud.exists()) {
            cloud.mkdir();
        }

        return cloud;
    }

    public void addFile(File file, UUID uuid) {

    }

    public void removeFile(String filePath, UUID uuid) {

    }

    public List<CloudMedia> getMediaFiles(UUID uuid) {
        for (File file : UtilFile.getInstance().getAllFiles(getCloudFolder(uuid))) {

        }
        return null;
    }

    public List<CloudDocument> getDocuments(UUID uuid) {
        return null;
    }

    public static CloudManager getInstance() {
        if (instance == null)
            instance = new CloudManager();
        return instance;
    }
}