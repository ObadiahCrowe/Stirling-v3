package com.obadiahpcrowe.stirling.classes.importing;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ImportDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:46 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing
 * Copyright (c) Obadiah Crowe 2017
 */
public class ImportManager {

    private static ImportManager instance;

    private MorphiaService morphiaService;
    private ImportDAO importDAO;
    private Gson gson;

    public ImportManager() {
        this.morphiaService = new MorphiaService();
        this.importDAO = new ImportDAOImpl(ImportAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static ImportManager getInstance() {
        if (instance == null)
            instance = new ImportManager();
        return instance;
    }

    public String addImportCredential(StirlingAccount account, ImportSource source, ImportCredential credential) {
        return "";
    }

    public String importExternalCourse(StirlingAccount account, UUID classUuid, ImportSource source, String id) {
        return "";
    }
}
