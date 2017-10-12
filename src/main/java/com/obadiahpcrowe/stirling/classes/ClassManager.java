package com.obadiahpcrowe.stirling.classes;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ClassesDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.List;
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
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CREATED, account.getLocale(), name));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ALREADY_EXISTS, account.getLocale(), name));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create classes", "TEACHER"));
    }

    public String deleteClass(StirlingAccount account, UUID classUuid) {
        if (account.getAccountType().getAccessLevel() >= AccountType.TEACHER.getAccessLevel()) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                if (clazz.getOwners().contains(account.getAccountName())) {
                    classesDAO.delete(clazz);
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DELETED, account.getLocale(), clazz.getName()));
                }
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_NOT_OWNER, account.getLocale()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
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

    public String addSection(StirlingAccount account, UUID classUuid, String title, String desc) {
        return "";
    }

    public String removeSection(StirlingAccount account, UUID classUuid, UUID sectionUuid) {
        return "";
    }

    public String addPostable(StirlingAccount account, UUID classUuid, UUID sectionUuid, String title,
                              String content, List<AttachableResource> resources) {
        return "";
    }

    public String removePostable(StirlingAccount account, UUID classUuid, UUID postableUuid) {
        return "";
    }

    public String addCatchupModule(StirlingAccount account, UUID classUuid, UUID lessonUuid, String title,
                                   String content, List<AttachableResource> resources) {
        return "";
    }

    public String removeCatchupModule(StirlingAccount account, UUID classUuid, UUID lessonUuid) {
        return "";
    }

    public String addAssessmentPiece(StirlingAccount account, UUID classUuid, String title, String desc, StirlingDate dueDate) {
        return "";
    }

    public String removeAssessmentForStudent(StirlingAccount account, UUID classUuid, UUID studentUuid, UUID assignmentUuid) {
        return "";
    }

    public String removeAssignmentForAll(StirlingAccount account, UUID classUuid, UUID assignmentUuid) {
        return "";
    }

    public String addStudent(StirlingAccount account, UUID classUuid, UUID studentUuid) {
        return "";
    }

    public String removeStudent(StirlingAccount account, UUID classUuid, UUID studentUuid) {
        return "";
    }

    public static ClassManager getInstance() {
        if (instance == null)
            instance = new ClassManager();
        return instance;
    }
}
