package com.obadiahpcrowe.stirling.pod.signin;

import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 10:21 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodScraper {

    private static PodScraper instance;

    public void signIn(int studentId, PodLine line, PodReason reason) {

    }

    public static PodScraper getInstance() {
        if (instance == null)
            instance = new PodScraper();
        return instance;
    }
}
