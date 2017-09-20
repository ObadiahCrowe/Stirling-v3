package com.obadiahpcrowe.stirling.feedback;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.feedback.enums.FeedbackType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:19 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.feedback
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("feedback")
public class StirlingFeedback {

    @Id
    private ObjectId id;

    private String poster;
    private UUID uuid;
    private String title;
    private String content;
    private List<AttachableResource> resources;
    private FeedbackType type;

    public StirlingFeedback(StirlingAccount account, String title, String content,
                            List<AttachableResource> resources, FeedbackType type) {
        this.poster = account.getAccountName();
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.resources = resources;
        this.type = type;
    }
}
