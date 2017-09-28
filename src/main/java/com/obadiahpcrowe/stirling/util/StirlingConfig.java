package com.obadiahpcrowe.stirling.util;

import com.obadiahpcrowe.stirling.util.enums.VersionType;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 7:10 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingConfig {

    private VersionType releaseChannel;
    private String schoolName;
    private boolean allowUnsignedModules;
    private boolean enableStirlingMarketplace;
    private boolean enableSchoolMarketplace;

    public StirlingConfig(VersionType releaseChannel, String schoolName, boolean allowUnsignedModules,
                          boolean enableStirlingMarketplace, boolean enableSchoolMarketplace) {
        this.releaseChannel = releaseChannel;
        this.schoolName = schoolName;
        this.allowUnsignedModules = allowUnsignedModules;
        this.enableStirlingMarketplace = enableStirlingMarketplace;
        this.enableSchoolMarketplace = enableSchoolMarketplace;
    }
}
