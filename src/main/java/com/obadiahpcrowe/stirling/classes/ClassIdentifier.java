package com.obadiahpcrowe.stirling.classes;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 19/11/17 at 9:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class ClassIdentifier {

    private String className;
    private UUID classUuid;

    public ClassIdentifier(String className, UUID classUuid) {
        this.className = className;
        this.classUuid = classUuid;
    }
}
