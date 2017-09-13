package com.obadiahpcrowe.stirling.sace.obj;

import com.obadiahpcrowe.stirling.util.UtilFile;
import lombok.Getter;

import java.io.File;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 7:41 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SaceSubject {

    private String name;
    private int stage;
    private UUID uuid;
    private File homeDir;

    public SaceSubject(String name, int stage) {
        this.name = name;
        this.stage = stage;
        this.uuid = UUID.randomUUID();

        this.homeDir = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "SACE" +
          File.separator + name + "-Stage" + stage);
    }
}
