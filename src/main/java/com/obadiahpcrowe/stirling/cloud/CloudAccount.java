package com.obadiahpcrowe.stirling.cloud;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/11/17 at 7:36 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("cloud")
public class CloudAccount {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String cloudFolder;
    private List<AttachableResource> cloudFiles;

    @Deprecated
    public CloudAccount() {
    }

    public CloudAccount(UUID uuid) {
        this.uuid = uuid;
        this.cloudFolder = CloudManager.getInstance().getCloudFolder(uuid).getPath();
        this.cloudFiles = Lists.newArrayList();
    }

    public CloudAccount(UUID uuid, List<AttachableResource> resources) {
        this.uuid = uuid;
        this.cloudFolder = CloudManager.getInstance().getCloudFolder(uuid).getPath();
        this.cloudFiles = resources;
    }

    public File getCloudFolder() {
        return new File(cloudFolder);
    }
}
