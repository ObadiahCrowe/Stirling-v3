package com.obadiahpcrowe.stirling.modules.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 1:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.modules.events
 * Copyright (c) Obadiah Crowe 2017
 */
public class EventManager {

    private static EventManager instance;
    private Map<StirlingEvent, Class> fireables = new HashMap<>();

    public void init() {

    }

    public void fireEvent(StirlingEvent event) {
        //
    }

    public static EventManager getInstance() {
        if (instance == null)
            instance = new EventManager();
        return instance;
    }
}
