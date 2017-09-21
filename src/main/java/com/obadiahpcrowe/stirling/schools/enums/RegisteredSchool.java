package com.obadiahpcrowe.stirling.schools.enums;

import com.obadiahpcrowe.stirling.api.gihs.PodAPI;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:10 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.schools.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum RegisteredSchool {

    GIHS("Glenunga International High School", StirlingLocale.ENGLISH, true, true, new PodAPI()),
    UNREGISTERED("Stirling is unregistered", StirlingLocale.ENGLISH, false, false);

    private String friendlyName;
    private StirlingLocale defaultLocale;
    private boolean supportsSace;
    private boolean supportsIb;
    private APIController[] extraApis;

    RegisteredSchool(String friendlyName, StirlingLocale defaultLocale, boolean supportsSace,
                     boolean supportsIb, APIController... extraApis) {
        this.friendlyName = friendlyName;
        this.defaultLocale = defaultLocale;
        this.supportsSace = supportsSace;
        this.supportsIb = supportsIb;
        this.extraApis = extraApis;
    }
}
