package com.obadiahpcrowe.stirling.sace.obj;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SaceUser {

    private UUID uuid;
    private String saceId;
    private String sacePassword;

    public SaceUser(UUID uuid, String saceId, String sacePassword) {
        this.uuid = uuid;
        this.saceId = saceId;
        this.sacePassword = sacePassword;
    }
}
