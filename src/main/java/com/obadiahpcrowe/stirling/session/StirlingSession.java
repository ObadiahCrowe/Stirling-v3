package com.obadiahpcrowe.stirling.session;

import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 6:16 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.session
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingSession {

    private UUID uuid;
    private long initTime;
    private String accessToken;

    public StirlingSession(UUID uuid) {
        this.uuid = uuid;
        this.initTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2);
        this.accessToken = UUID.randomUUID().toString().replace("-", "");
    }
}
