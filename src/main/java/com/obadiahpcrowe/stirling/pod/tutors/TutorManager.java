package com.obadiahpcrowe.stirling.pod.tutors;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.TutorDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.TutorDAO;
import com.obadiahpcrowe.stirling.pod.tutors.obj.StirlingTutor;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:39 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutors
 * Copyright (c) Obadiah Crowe 2017
 */
public class TutorManager {

    private MorphiaService morphiaService;
    private TutorDAO tutorDAO;
    private Gson gson;

    public TutorManager() {
        this.morphiaService = new MorphiaService();
        this.tutorDAO = new TutorDAOImpl(StirlingTutor.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }
}
