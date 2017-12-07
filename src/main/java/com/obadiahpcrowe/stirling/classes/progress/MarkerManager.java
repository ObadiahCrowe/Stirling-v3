package com.obadiahpcrowe.stirling.classes.progress;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.MarkerDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.MarkerDAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 8:02 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.progress
 * Copyright (c) Obadiah Crowe 2017
 */
public class MarkerManager {

    private static MarkerManager instance;

    private MorphiaService morphiaService;
    private MarkerDAO markerDAO;
    private Gson gson;

    private MarkerManager() {
        this.morphiaService = new MorphiaService();
        this.markerDAO = new MarkerDAOImpl(ProgressAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static MarkerManager getInstance() {
        if (instance == null)
            instance = new MarkerManager();
        return instance;
    }

    public void createProgressAccount(ProgressAccount account) {
        markerDAO.save(account);
    }

    public ProgressAccount getByUuid(UUID uuid) {
        return markerDAO.getByUuid(uuid);
    }

    public void updateField(UUID uuid, String field, Object value) {
        markerDAO.updateField(uuid, field, value);
    }
}
