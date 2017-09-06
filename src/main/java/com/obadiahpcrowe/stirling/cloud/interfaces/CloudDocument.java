package com.obadiahpcrowe.stirling.cloud.interfaces;

import com.obadiahpcrowe.stirling.cloud.enums.DocType;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 11:42 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.cloud.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface CloudDocument {

    DocType getType();

    UUID getOwner();

    boolean isPublic();

    String getFileName();
}
