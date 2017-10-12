package com.obadiahpcrowe.stirling.classes;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ClassesDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 11:55 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassManager {

    private static ClassManager instance;

    private MorphiaService morphiaService;
    private ClassesDAO classesDAO;
    private Gson gson = new Gson();

    public ClassManager() {
        this.morphiaService = new MorphiaService();
        this.classesDAO = new ClassesDAOImpl(StirlingClass.class, morphiaService.getDatastore());
    }

    public String createClass(StirlingAccount account, String name, String desc, String room) {
        if (account.getAccountType().getAccessLevel() >= AccountType.TEACHER.getAccessLevel()) {
            if (!classExists(name)) {
                StirlingClass clazz = new StirlingClass(account, name, desc, room);
                UtilFile.getInstance().createClassFolder(clazz.getUuid());

                classesDAO.save(clazz);
                return gson.toJson(new StirlingMsg());
            }
            return gson.toJson(new StirlingMsg());
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create classes", "TEACHER"));
    }

    public String deleteClass(StirlingAccount account, UUID classUuid) {
        if (account.getAccountType().getAccessLevel() >= AccountType.TEACHER.getAccessLevel()) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                if (clazz.getTeachers().contains(account.getUuid())) {
                    classesDAO.delete(clazz);
                    return gson.toJson(new StirlingMsg());
                }
                return gson.toJson(new StirlingMsg());
            }
            return gson.toJson(new StirlingMsg());
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete classes", "TEACHER"));
    }

    public StirlingClass getByUuid(UUID classUuid) {
        return classesDAO.getByUuid(classUuid);
    }

    public StirlingClass getByName(String className) {
        return classesDAO.getByName(className);
    }

    public boolean classExists(UUID classUuid) {
        return getByUuid(classUuid) != null;
    }

    public boolean classExists(String className) {
        return getByName(className) != null;
    }

    public String addTeachers(StirlingAccount account, UUID classUuid, UUID... teacherUuids) {
        return "";
    }

    public String removeTeachers(StirlingAccount account, UUID classUuid, UUID... teacherUuids) {
        return "";
    }

    public String setRoom(StirlingAccount account, UUID classUuid, String roomName) {
        return "";
    }

    public String setName(StirlingAccount account, UUID classUuid, String className) {
        return "";
    }

    public String setDesc(StirlingAccount account, UUID classUuid, String desc) {
        return "";
    }

    @Deprecated // TODO: 12/10/17 Fill this out in a later release
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

    public static ClassManager getInstance() {
        if (instance == null)
            instance = new ClassManager();
        return instance;
    }
}
