package com.obadiahpcrowe.stirling.cloud;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private File home;
    private Gson gson;

    public CloudManager() {
        this.home = UtilFile.getInstance().getStorageLoc();
        this.gson = new Gson();
    }

    public File getCloudFolder(StirlingAccount account) {
        return getCloudFolder(account.getUuid());
    }

    public String uploadFile(StirlingAccount account, MultipartFile file) {
        try {
            File out = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
              "Cloud" + File.separator + file.getOriginalFilename());

            if (out.exists()) {
                return gson.toJson(new StirlingMsg(MsgTemplate.FILE_ALREADY_EXISTS, account.getLocale(), file.getOriginalFilename()));
            }

            file.transferTo(out);
            return gson.toJson(new StirlingMsg(MsgTemplate.UPLOADING_FILE, account.getLocale(), file.getOriginalFilename()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "uploading the file"));
        }
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
        UtilFile.getInstance().copyFile(file, getCloudFolder(uuid));
    }

    public void removeFile(String filePath, UUID uuid) {
        File file = new File(getCloudFolder(uuid) + File.separator + filePath);
        if (file.isDirectory()) {
            UtilFile.getInstance().deleteDirectory(file);
        } else {
            file.delete();
        }
    }

    public static CloudManager getInstance() {
        if (instance == null)
            instance = new CloudManager();
        return instance;
    }
}
