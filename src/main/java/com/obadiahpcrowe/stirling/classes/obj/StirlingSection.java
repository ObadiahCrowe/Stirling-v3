package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 10:35 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingSection {

    private UUID uuid;
    private String title;
    private String desc;
    private AccountType minType;

    public StirlingSection(UUID uuid, String title, String desc, AccountType minType) {
        this.uuid = uuid;
        this.title = title;
        this.desc = desc;
        this.minType = minType;
    }
}
