package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 4:53 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingCatchup {

    private UUID lessonUuid;
    private String title;
    private String content;
    private List<AttachableResource> resources;

    @Deprecated
    public StirlingCatchup() {}

    public StirlingCatchup(UUID lessonUuid, String title, String content, List<AttachableResource> resources) {
        this.lessonUuid = lessonUuid;
        this.title = title;
        this.content = content;
        this.resources = resources;
    }
}
