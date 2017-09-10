package com.obadiahpcrowe.stirling.pod.laptop.obj;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:11 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class LaptopUser {

    private UUID uuid;
    private String laptopName;

    public LaptopUser(UUID uuid, String laptopName) {
        this.uuid = uuid;
        this.laptopName = laptopName;
    }
}
