package com.obadiahpcrowe.stirling.util;

import com.obadiahpcrowe.stirling.util.enums.VersionType;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 12:00 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingVersion {

    private VersionType type;
    private double majorVersion;
    private int minVersion;

    public StirlingVersion(VersionType type, double majorVersion, int minVersion) {
        this.type = type;
        this.majorVersion = majorVersion;
        this.minVersion = minVersion;
    }
}
