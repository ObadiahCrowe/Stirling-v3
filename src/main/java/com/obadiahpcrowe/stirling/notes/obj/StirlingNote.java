package com.obadiahpcrowe.stirling.notes.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 5:39 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.notes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingNote {

    private String owner;
    private UUID uuid;
    private @Setter String title;
    private @Setter String content;
    private List<AttachableResource> resources;

    public StirlingNote(StirlingAccount account, String title, String content, List<AttachableResource> resources) {
        this.owner = account.getAccountName();
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.resources = resources;
    }
}
