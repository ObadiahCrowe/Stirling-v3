package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.classes.interfaces.SectionChild;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/10/17 at 2:29 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingResource extends AttachableResource implements SectionChild {

    private String title;
    private StirlingDate postDateTime;

    @Deprecated
    public StirlingResource() {}

    public StirlingResource(UUID owner, String fileName, String title) {
        super(owner, fileName);
        this.title = title;
        this.postDateTime = StirlingDate.getNow();
    }
}
