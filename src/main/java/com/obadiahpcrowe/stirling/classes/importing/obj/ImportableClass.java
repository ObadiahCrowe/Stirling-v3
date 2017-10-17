package com.obadiahpcrowe.stirling.classes.importing.obj;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 5:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class ImportableClass {

    private String className;
    private String id;

    @Deprecated
    public ImportableClass() {
    }

    public ImportableClass(String className, String id) {
        this.className = className;
        this.id = id;
    }
}
