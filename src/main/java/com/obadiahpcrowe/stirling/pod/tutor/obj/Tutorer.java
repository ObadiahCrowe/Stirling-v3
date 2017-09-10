package com.obadiahpcrowe.stirling.pod.tutor.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 3:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutor.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class Tutorer {

    private String name;
    private UUID uuid;
    private List<String> specialities;

    public Tutorer(StirlingAccount account, List<String> specialities) {
        this.name = account.getDisplayName();
        this.uuid = account.getUuid();
        this.specialities = specialities;
    }
}
