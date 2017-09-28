package com.obadiahpcrowe.stirling.schools.interfaces;

import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 28/9/17 at 8:02 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.schools.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface RegisteredSchool {

    String getName();

    String getEmailExtension();

    StirlingLocale getDefaultLocale();

    boolean supportsSACE();

    boolean supportsIB();

    APIController[] getExtraAPIs();
}
