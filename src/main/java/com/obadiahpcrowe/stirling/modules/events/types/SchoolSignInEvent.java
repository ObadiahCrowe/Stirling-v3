package com.obadiahpcrowe.stirling.modules.events.types;

import com.obadiahpcrowe.stirling.modules.events.StirlingEvent;
import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 10:50 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.events.types
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SchoolSignInEvent implements StirlingEvent {

    private StirlingMsg output;
    private UUID uuid;
    private SignInReason reason;
    private String extraInfo;

    public SchoolSignInEvent(StirlingMsg output, UUID uuid, SignInReason reason, String extraInfo) {
        this.output = output;
        this.uuid = uuid;
        this.reason = reason;
        this.extraInfo = extraInfo;
    }
}
