package com.obadiahpcrowe.stirling.cloud.interfaces;

import com.obadiahpcrowe.stirling.cloud.enums.MediaType;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:36 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface CloudMedia {

    MediaType getType();

    UUID getOwner();

    boolean isPublic();

    String getFileName();
}
