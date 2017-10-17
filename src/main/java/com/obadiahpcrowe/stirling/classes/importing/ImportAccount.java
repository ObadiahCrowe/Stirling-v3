package com.obadiahpcrowe.stirling.classes.importing;

import com.google.common.collect.Maps;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import lombok.Getter;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 10:22 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("importers")
public class ImportAccount {

    private UUID accountUuid;
    private Map<ImportSource, ImportCredential> credentials;
    private Map<ImportSource, List<ImportableClass>> courseHolders;
    private Map<ImportSource, List<? extends ImportableClass>> importableClasses;

    @Deprecated
    public ImportAccount() {
    }

    public ImportAccount(UUID accountUuid, Map<ImportSource, ImportCredential> credentials) {
        this.accountUuid = accountUuid;
        this.credentials = credentials;
        this.courseHolders = Maps.newHashMap();
        this.importableClasses = Maps.newHashMap();
    }
}
