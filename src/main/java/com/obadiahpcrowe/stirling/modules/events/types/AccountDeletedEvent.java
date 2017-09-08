package com.obadiahpcrowe.stirling.modules.events.types;

import com.obadiahpcrowe.stirling.modules.events.StirlingEvent;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 1:46 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.events.types
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class AccountDeletedEvent implements StirlingEvent {

    private StirlingMsg output;
    private String accountName;
    private UUID uuid;

    public AccountDeletedEvent(StirlingMsg output, String accountName, UUID uuid) {
        this.output = output;
        this.accountName = accountName;
        this.uuid = uuid;
    }
}
