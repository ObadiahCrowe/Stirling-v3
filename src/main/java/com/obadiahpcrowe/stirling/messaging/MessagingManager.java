package com.obadiahpcrowe.stirling.messaging;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 10:28 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.messaging
 * Copyright (c) Obadiah Crowe 2017
 */
public class MessagingManager {

    private static MessagingManager instance;

    public static MessagingManager getInstance() {
        if (instance == null)
            instance = new MessagingManager();
        return instance;
    }
}
