package com.obadiahpcrowe.stirling.classes.assignments;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.AssignmentDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AssignmentDAO;

/**
 * Created by: Obadiah Crowe
 * Creation Date / Time: 7/12/17 at 2:25 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.assignments
 * Copyright (c) Obadiah Crowe 2017
 */
public class AssignmentManager {

    private static AssignmentManager instance;

    private MorphiaService morphiaService;
    private AssignmentDAO assignmentDAO;
    private Gson gson;

    private AssignmentManager() {
        this.morphiaService = new MorphiaService();
        this.assignmentDAO = new AssignmentDAOImpl(StirlingAssignment.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static AssignmentManager getInstance() {
        if (instance == null)
            instance = new AssignmentManager();
        return instance;
    }

    public void addAssignment() {

    }
}