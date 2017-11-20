package com.obadiahpcrowe.stirling.resources;

import com.obadiahpcrowe.stirling.cloud.CloudManager;
import com.obadiahpcrowe.stirling.util.UtilFile;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 5:41 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.resources
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class AttachableResource {

    private UUID owner;
    private UUID resUuid;
    private String filePath;
    private ARType arType;

    public AttachableResource() {}

    public AttachableResource(UUID uuid, String filePath) {
        this.owner = uuid;
        this.resUuid = UUID.randomUUID();
        this.filePath = filePath;
        this.arType = ARType.NORMAL;
    }

    public AttachableResource(UUID uuid, String filePath, ARType arType) {
        this.owner = uuid;
        this.resUuid = UUID.randomUUID();
        this.filePath = filePath;
        this.arType = arType;
    }

    public File getFile() {
        if (arType == ARType.ANNOUNCEMENT) {
            return getAnnouncementResource();
        }

        if (arType == ARType.CLASS) {
            return getClassResource();
        }

        if (arType == ARType.CLASS_SINGLE) {
            return getSingleClassResource();
        }

        if (filePath.equalsIgnoreCase("avatar.png")) {
            return getAvatar();
        }

        if (filePath.equalsIgnoreCase("banner.jpg")) {
            return getBanner();
        }

        return new File(CloudManager.getInstance().getCloudFolder(owner) + File.separator + filePath);
    }

    private File getAvatar() {
        return new File(UtilFile.getInstance().getUserFolder(owner) + File.separator + "Images" + File.separator + "avatar.png");
    }

    private File getBanner() {
        return new File(UtilFile.getInstance().getUserFolder(owner) + File.separator + "Images" + File.separator + "banner.jpg");
    }

    private File getAnnouncementResource() {
        return new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Announcements" +
          File.separator + owner + File.separator + filePath);
    }

    private File getSingleClassResource() {
        return new File(UtilFile.getInstance().getStorageLoc() + File.separator + "UserData" + File.separator +
          owner.toString() + File.separator + "Classes" + File.separator + filePath);
    }

    private File getClassResource() {
        return new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Classes" + File.separator +
          owner.toString() + File.separator + "Resources" + File.separator + filePath);
    }
}
