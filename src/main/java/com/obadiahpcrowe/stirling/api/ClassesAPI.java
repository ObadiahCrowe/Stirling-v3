package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class ClassesAPI implements APIController {

    // TODO: 26/10/17 Create edit api calls
    // TODO: 26/10/17 Resources in sections

    private AccountManager accountManager = AccountManager.getInstance();
    private ClassManager classManager = ClassManager.getInstance();
    private Gson gson = new Gson();

    private final String CALL_DISABLED = "This API call is undergoing rigorous testing before becoming a beta API. Please wait a day from now.";

    @CallableAPI(fields = {"accountName", "password", "name", "desc", "room", "timeSlot"})
    @RequestMapping(value = "/stirling/v3/classes/create", method = RequestMethod.GET)
    public String createClass(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("name") String name,
                              @RequestParam("desc") String desc,
                              @RequestParam("room") String room,
                              @RequestParam("timeSlot") String timeSlot) {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password "})
    @RequestMapping(value = "/stirling/v3/classes/getAll", method = RequestMethod.GET)
    public String getAllClasses(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return LocalisationManager.getInstance().translate(gson.toJson(classManager.getAllClasses(account)), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/timetable", method = RequestMethod.GET)
    public String getTimeTable() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete", method = RequestMethod.GET)
    public String deleteClass() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "room"})
    @RequestMapping(value = "/stirling/v3/classes/set/room", method = RequestMethod.GET)
    public String setRoom() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "title", "desc"})
    @RequestMapping(value = "/stirling/v3/classes/create/section", method = RequestMethod.GET)
    public String createSection() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "sectionUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/section", method = RequestMethod.GET)
    public String deleteSection() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "sectionUuid", "title", "content", "resources"})
    @RequestMapping(value = "/stirling/v3/classes/create/post", method = RequestMethod.GET)
    public String createPostable() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "sectionUuid", "postUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/post", method = RequestMethod.GET)
    public String deletePostable() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "lessonUuid", "title", "content", "resources"})
    @RequestMapping(value = "/stirling/v3/classes/create/catchup", method = RequestMethod.GET)
    public String createCatchup() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "lessonUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/catchup", method = RequestMethod.GET)
    public String deleteCatchup() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "title", "desc", "dueDate", "dueTime", "maxMarks", "weighting"})
    @RequestMapping(value = "/stirling/v3/classes/create/assignment", method = RequestMethod.GET)
    public String createAssignment() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuid", "assignmentUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/assignment/student", method = RequestMethod.GET)
    public String deleteAssignmentSingular() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "assignmentUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/assignment", method = RequestMethod.GET)
    public String deleteAssignment() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "studentUuids"})
    @RequestMapping(value = "/stirling/v3/classes/students/add", method = RequestMethod.GET)
    public String addStudents() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "studentUuids"})
    @RequestMapping(value = "/stirling/v3/classes/students/remove", method = RequestMethod.GET)
    public String removeStudents() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "teacherUuids"})
    @RequestMapping(value = "/stirling/v3/classes/teachers/add", method = RequestMethod.GET)
    public String addTeachers() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "teacherUuids"})
    @RequestMapping(value = "/stirling/v3/classes/teachers/remove", method = RequestMethod.GET)
    public String removeTeachers() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "title", "content", "resources"})
    @RequestMapping(value = "/stirling/v3/classes/create/homework", method = RequestMethod.GET)
    public String createHomework() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "homeworkUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/homework", method = RequestMethod.GET)
    public String deleteHomework() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "title", "content", "resources"})
    @RequestMapping(value = "/stirling/v3/classes/create/classNote", method = RequestMethod.GET)
    public String createClassNote() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "homeworkUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/classNote", method = RequestMethod.GET)
    public String deleteClassNote() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "title", "resource"})
    @RequestMapping(value = "/stirling/v3/classes/create/resource", method = RequestMethod.GET)
    public String createResource() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "resourceUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete/resource", method = RequestMethod.GET)
    public String deleteResource() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "resourceUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/resource", method = RequestMethod.GET)
    public String getResource() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "assignmentUuid", "resources"})
    @RequestMapping(value = "/stirling/v3/classes/upload/assignment", method = RequestMethod.GET)
    public String uploadAssignment() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "assignmentUuid", "studentUuid", "receivedMarks", "grade", "weighting", "comments"})
    @RequestMapping(value = "/stirling/v3/classes/mark/assignment", method = RequestMethod.GET)
    public String markAssignment() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuid", "title", "desc"})
    @RequestMapping(value = "/stirling/v3/classes/progressMarkers/assign", method = RequestMethod.GET)
    public String assignMarker() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuid", "markerUuid"})
    @RequestMapping(value = "/stirling/v3/classes/progressMarkers/remove", method = RequestMethod.GET)
    public String removeMarker() {
        return CALL_DISABLED;
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "lessonUuid", "studentUuid", "attendanceStatus"})
    @RequestMapping(value = "/stirling/v3/classes/attendance/set", method = RequestMethod.GET)
    public String setAttendance() {
        return CALL_DISABLED;
    }

    // TODO: 29/10/17 Edit shit and other yanks
}
