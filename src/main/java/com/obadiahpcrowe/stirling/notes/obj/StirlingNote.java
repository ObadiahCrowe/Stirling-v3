package com.obadiahpcrowe.stirling.notes.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

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
@Entity("notes")
public class StirlingNote {

    @Id
    private ObjectId id;

    private String owner;
    private UUID uuid;
    private @Setter String title;
    private @Setter String content;
    private List<AttachableResource> resources;

    public StirlingNote() {}

    public StirlingNote(StirlingAccount account, String title, String content, List<AttachableResource> resources) {
        this.owner = account.getAccountName();
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.resources = resources;
    }
}
