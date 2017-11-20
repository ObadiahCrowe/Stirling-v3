package com.obadiahpcrowe.stirling.cloud;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.CloudDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.CloudDAO;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:51 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud
 * Copyright (c) Obadiah Crowe 2017
 */
public class CloudManager {

    private static CloudManager instance;

    private MorphiaService morphiaService;
    private CloudDAO cloudDAO;
    private File home;
    private Gson gson;

    private CloudManager() {
        this.morphiaService = new MorphiaService();
        this.cloudDAO = new CloudDAOImpl(CloudAccount.class, morphiaService.getDatastore());
        this.home = UtilFile.getInstance().getStorageLoc();
        this.gson = new Gson();
    }

    public void checkExists(StirlingAccount account) {
        if (!accountExists(account.getUuid())) {
            cloudDAO.save(new CloudAccount(account.getUuid()));
        }
    }

    public String uploadFiles(StirlingAccount account, MultipartFile[] files) {
        CloudAccount cloudAccount = getCloudAccount(account.getUuid());

        File base = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator + "Cloud");
        if (!base.exists()) {
            base.mkdir();
        }

        List<AttachableResource> resources = Lists.newArrayList();
        try {
            resources.addAll(cloudAccount.getCloudFiles());
        } catch (NullPointerException ignored) {
        }

        for (MultipartFile file : files) {
            try {
                file.transferTo(new File(base, file.getOriginalFilename()));

                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                resources.forEach(r -> {
                    if (r.getFilePath().equals(file.getOriginalFilename())) {
                        contains.complete(true);
                    }
                });

                if (!contains.getNow(false)) {
                    resources.add(new AttachableResource(account.getUuid(), file.getOriginalFilename()));
                }
            } catch (IOException e) {
                return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "uploading the file"));
            }
        }

        updateField(cloudAccount, "cloudFiles", resources);
        return gson.toJson(new StirlingMsg(MsgTemplate.FILES_UPLOADED, account.getLocale(), String.valueOf(files.length)));
    }

    public File getCloudFolder(UUID uuid) {
        File cloud = new File(home + File.separator + "UserData" + File.separator + uuid.toString() +
          File.separator + "Cloud");

        if (!cloud.exists()) {
            cloud.mkdir();
        }

        return cloud;
    }

    public void removeFile(StirlingAccount account, String filePath) {
        if (!accountExists(account.getUuid())) {
            cloudDAO.save(new CloudAccount(account.getUuid()));
        }

        CloudAccount cloudAccount = getCloudAccount(account.getUuid());

        File file = new File(getCloudFolder(account.getUuid()) + File.separator + filePath);
        if (file.isDirectory()) {
            UtilFile.getInstance().deleteDirectory(file);
        } else {
            file.delete();
        }

        List<AttachableResource> resources = Lists.newArrayList();
        try {
            resources.addAll(cloudAccount.getCloudFiles());
        } catch (NullPointerException ignored) {
        }

        CompletableFuture<AttachableResource> resource = new CompletableFuture<>();
        resources.forEach(r -> {
            if (r.getFilePath().equalsIgnoreCase(filePath)) {
                resource.complete(r);
            }
        });

        if (resource.getNow(null) != null) {
            resources.remove(resource.getNow(null));
        }

        updateField(cloudAccount, "cloudFiles", resources);
    }

    public boolean accountExists(UUID uuid) {
        if (getCloudAccount(uuid) == null) {
            return false;
        }

        return true;
    }

    public CloudAccount getCloudAccount(UUID uuid) {
        return cloudDAO.getByUuid(uuid);
    }

    public void updateField(CloudAccount account, String field, Object value) {
        cloudDAO.updateField(account, field, value);
    }

    public static CloudManager getInstance() {
        if (instance == null)
            instance = new CloudManager();
        return instance;
    }
}
