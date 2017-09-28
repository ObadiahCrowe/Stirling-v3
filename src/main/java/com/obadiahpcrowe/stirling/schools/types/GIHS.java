package com.obadiahpcrowe.stirling.schools.types;

import com.obadiahpcrowe.stirling.api.gihs.PodAPI;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.schools.interfaces.RegisteredSchool;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 28/9/17 at 8:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.schools.types
 * Copyright (c) Obadiah Crowe 2017
 */
public class GIHS implements RegisteredSchool {

    private String name = "Glenunga International High School";
    private String emailExtension = "@gihs.sa.edu.au";
    private StirlingLocale defaultLocale = StirlingLocale.ENGLISH;
    private boolean supportsSace = true;
    private boolean supportsIb = true;
    private APIController[] extraApis = new APIController[] {
      new PodAPI()
    };

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmailExtension() {
        return emailExtension;
    }

    @Override
    public StirlingLocale getDefaultLocale() {
        return defaultLocale;
    }

    @Override
    public boolean supportsSACE() {
        return supportsSace;
    }

    @Override
    public boolean supportsIB() {
        return supportsIb;
    }

    @Override
    public APIController[] getExtraAPIs() {
        return extraApis;
    }
}
