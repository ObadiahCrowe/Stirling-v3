package com.obadiahpcrowe.stirling.pod.signin.obj;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 10:59 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class PodUser {

    private UUID uuid;
    private int studentId;

    public PodUser(UUID uuid, int studentId) {
        this.uuid = uuid;
        this.studentId = studentId;
    }
}
