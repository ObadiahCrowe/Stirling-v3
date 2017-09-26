package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.resources.AttachableResource;
import lombok.Getter;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 4:52 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingPostable {

    private String title;
    private String content;
    private List<AttachableResource> resources;

    public StirlingPostable() {}

    public StirlingPostable(String title, String content, List<AttachableResource> resources) {
        this.title = title;
        this.content = content;
        this.resources = resources;
    }
}
