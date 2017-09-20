package com.obadiahpcrowe.stirling.surveys;

import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.SurveyDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.SurveyDAO;
import com.obadiahpcrowe.stirling.surveys.obj.StirlingSurvey;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 8:15 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys
 * Copyright (c) Obadiah Crowe 2017
 */
public class SurveyManager {

    private MorphiaService morphiaService;
    private SurveyDAO surveyDAO;

    public SurveyManager() {
        this.morphiaService = new MorphiaService();
        this.surveyDAO = new SurveyDAOImpl(StirlingSurvey.class, morphiaService.getDatastore());
    }

    public String createSurvey() {
        return "";
    }
}
