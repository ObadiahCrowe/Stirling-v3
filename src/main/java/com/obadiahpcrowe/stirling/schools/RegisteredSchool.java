package com.obadiahpcrowe.stirling.schools;

import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 28/9/17 at 8:02 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.schools
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class RegisteredSchool {

    private String name;
    private String emailExtension;
    private StirlingLocale defaultLocale;
    private boolean supportsSace;
    private boolean supportsIb;

    @Deprecated
    public RegisteredSchool() {
    }

    public RegisteredSchool(String name, String emailExtension, StirlingLocale defaultLocale, boolean supportsSace,
                            boolean supportsIb) {
        this.name = name;
        this.emailExtension = emailExtension;
        this.defaultLocale = defaultLocale;
        this.supportsSace = supportsSace;
        this.supportsIb = supportsIb;
    }
}
