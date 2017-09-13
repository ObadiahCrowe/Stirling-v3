package com.obadiahpcrowe.stirling.modules.handoff;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 4:18 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.handoff
 * Copyright (c) Obadiah Crowe 2017
 */
public class HandoffManager {

    private static HandoffManager instance;

    public void init() {
        // TODO: 13/9/17 this is required for platform support
    }

    public static HandoffManager getInstance() {
        if (instance == null)
            instance = new HandoffManager();
        return instance;
    }
}
