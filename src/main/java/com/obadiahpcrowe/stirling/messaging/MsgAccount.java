package com.obadiahpcrowe.stirling.messaging;

import com.obadiahpcrowe.stirling.messaging.contacts.ContactableAccount;
import com.obadiahpcrowe.stirling.messaging.enums.OnlineStatus;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 10:24 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.messaging
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class MsgAccount {

    private UUID uuid;
    private String status;
    private OnlineStatus onlineStatus;
    private List<ContactableAccount> contacts;

    public MsgAccount(UUID uuid, String status, OnlineStatus onlineStatus, List<ContactableAccount> contacts) {
        this.uuid = uuid;
        this.status = status;
        this.onlineStatus = onlineStatus;
        this.contacts = contacts;
    }
}
