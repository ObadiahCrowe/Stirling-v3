package com.obadiahpcrowe.stirling.classes.importing.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 10:20 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class ImportCredential {

    // Generic username / password
    private String username;
    private String password;

    // Google Classroom
    private String gAuthCode;
    private String gRefreshCode;

    @Deprecated
    public ImportCredential() {
    }

    public ImportCredential(String username, char[] password) {
        this.username = username;
        this.password = String.valueOf(password);
    }

    public ImportCredential(String gAuthCode, String gRefreshCode) {
        this.gAuthCode = gAuthCode;
        this.gRefreshCode = gRefreshCode;
    }
}
