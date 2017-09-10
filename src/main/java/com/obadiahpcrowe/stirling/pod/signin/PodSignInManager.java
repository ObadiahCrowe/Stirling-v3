package com.obadiahpcrowe.stirling.pod.signin;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 10:04 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodSignInManager {

    private static PodSignInManager instance;

    // TODO: 10/9/17 ALL of this

    public static PodSignInManager getInstance() {
        if (instance == null)
            instance = new PodSignInManager();
        return instance;
    }
}
