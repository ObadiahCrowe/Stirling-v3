package com.obadiahpcrowe.stirling.classes;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ClassesDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 11:55 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassManager {

    private MorphiaService morphiaService;
    private ClassesDAO classesDAO;
    private Gson gson = new Gson();

    public ClassManager() {
        this.morphiaService = new MorphiaService();
        this.classesDAO = new ClassesDAOImpl(StirlingClass.class, morphiaService.getDatastore());
    }

    public String createClass() {
        return "";
    }

    public String deleteClass() {
        return "";
    }

    public StirlingClass getStirlingClass(UUID classUuid) {
        return classesDAO.getByUuid(classUuid);
    }

    public boolean classExists() {
        return false;
    }

    public String addTeachers() {
        return "";
    }

    public String removeTeachers() {
        return "";
    }

    public String setRoom() {
        return "";
    }

    public String setName() {
        return "";
    }

    public String setDesc() {
        return "";
    }

    public String setOutline() {
        return "";
    }

    public String addSection() {
        return "";
    }

    public String removeSection() {
        return "";
    }

    public String addPostable() {
        return "";
    }

    public String removePostable() {
        return "";
    }

    public String addCatchupModule() {
        return "";
    }

    public String removeCatchupModule() {
        return "";
    }

    public String addAssessmentPiece() {
        return "";
    }

    public String removeAssessmentPiece() {
        return "";
    }

    public String addStudent() {
        return "";
    }

    public String removeStudent() {
        return "";
    }
}
