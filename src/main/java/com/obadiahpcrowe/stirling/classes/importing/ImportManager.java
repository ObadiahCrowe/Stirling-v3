package com.obadiahpcrowe.stirling.classes.importing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.calendar.CalendarManager;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.assignments.AssignmentAccount;
import com.obadiahpcrowe.stirling.classes.assignments.AssignmentManager;
import com.obadiahpcrowe.stirling.classes.assignments.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.enums.ClassLength;
import com.obadiahpcrowe.stirling.classes.enums.ClassRole;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapClass;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapScraper;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.gclassroom.GClassroomHandler;
import com.obadiahpcrowe.stirling.classes.importing.gclassroom.GoogleClass;
import com.obadiahpcrowe.stirling.classes.importing.moodle.MoodleClass;
import com.obadiahpcrowe.stirling.classes.importing.moodle.MoodleScraper;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ImportDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilLog;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:46 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing
 * Copyright (c) Obadiah Crowe 2017
 */
public class ImportManager {

    private static ImportManager instance;

    private AccountManager accountManager;
    private ClassManager classManager;

    private MorphiaService morphiaService;
    private ImportDAO importDAO;
    private Gson gson;

    public ImportManager() {
        this.accountManager = AccountManager.getInstance();
        this.classManager = ClassManager.getInstance();

        this.morphiaService = new MorphiaService();
        this.importDAO = new ImportDAOImpl(ImportAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static ImportManager getInstance() {
        if (instance == null)
            instance = new ImportManager();
        return instance;
    }

    public List<ImportableClass> getDaymapCourses(StirlingAccount account) {
        if (importAccExists(account.getUuid())) {
            if (credentialsExist(account, ImportSource.DAYMAP)) {
                ImportCredential cred = getCreds(account, ImportSource.DAYMAP);
                if (cred == null) {
                    return null;
                }

                try {
                    return DaymapScraper.getInstance().getCourses(cred.getUsername(), cred.getPassword());
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    public List<ImportableClass> getMoodleCourses(StirlingAccount account) {
        if (credentialsExist(account, ImportSource.MOODLE)) {
            return MoodleScraper.getInstance().getCourses(account);
        }
        return null;
    }

    public List<ImportableClass> getGoogleCourses(StirlingAccount account) {
        if (credentialsExist(account, ImportSource.GOOGLE_CLASSROOM)) {
            return GClassroomHandler.getInstance().getCourses(account);
        }
        return null;
    }

    public GoogleClass getGoogleClass(StirlingAccount account, ImportableClass clazz) {
        if (credentialsExist(account, ImportSource.GOOGLE_CLASSROOM)) {
            GoogleClass c = GClassroomHandler.getInstance().importCourse(account, clazz);

            ImportAccount acc = getByUuid(account.getUuid());
            List<GoogleClass> googleClasses = Lists.newArrayList();

            try {
                googleClasses.addAll(acc.getGoogleClasses());
            } catch (NullPointerException ignored) {
            }

            CompletableFuture<Boolean> contains = new CompletableFuture<>();
            googleClasses.forEach(googleClass -> {
                if (googleClass.getClassName().equals(clazz.getClassName())) {
                    contains.complete(true);
                }
            });

            if (!contains.getNow(false)) {
                googleClasses.add(c);
                updateField(acc, "googleClasses", googleClasses);
            }

            return c;
        }
        return null;
    }

    public MoodleClass getMoodleClass(StirlingAccount account, ImportableClass clazz) {
        if (credentialsExist(account, ImportSource.MOODLE)) {
            MoodleClass c = MoodleScraper.getInstance().getCourse(account, clazz);

            ImportAccount acc = getByUuid(account.getUuid());
            List<MoodleClass> moodleClasses = Lists.newArrayList();

            try {
                moodleClasses.addAll(acc.getMoodleClasses());
            } catch (NullPointerException ignored) {
            }

            CompletableFuture<Boolean> contains = new CompletableFuture<>();
            moodleClasses.forEach(moodleClass -> {
                if (moodleClass.getClassName().equals(clazz.getClassName())) {
                    contains.complete(true);
                }
            });

            if (!contains.getNow(false)) {
                moodleClasses.add(c);
                updateField(acc, "moodleClasses", moodleClasses);
            }

            return c;
        }
        return null;
    }

    public String addImportCredential(StirlingAccount account, ImportSource source, ImportCredential credential) {
        Map<ImportSource, ImportCredential> credentialMap;
        if (!importAccExists(account.getUuid())) {
            credentialMap = Maps.newHashMap();
            credentialMap.put(source, credential);

            importDAO.save(new ImportAccount(account.getUuid(), credentialMap));
            if (areCredentialsValid(account, source)) {
                switch (source) {
                    case DAYMAP:
                        importAllDaymap(account);
                        break;
                    case MOODLE:
                        importAllMoodle(account);
                        break;
                }
                return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
            }

            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_CREDS_INVALID, account.getLocale(), source.getFriendlyName()));
        }

        // Add account then import
        ImportAccount importAccount = getByUuid(account.getUuid());
        credentialMap = Maps.newHashMap();
        try {
            credentialMap.putAll(importAccount.getCredentials());
        } catch (NullPointerException ignored) {
        }

        if (credentialMap.containsKey(source)) {
            credentialMap.replace(source, credential);
        } else {
            credentialMap.put(source, credential);
        }

        importDAO.updateField(importAccount, "credentials", credentialMap);

        if (areCredentialsValid(account, source)) {
            switch (source) {
                case DAYMAP:
                    importAllDaymap(account);
                    break;
                case MOODLE:
                    importAllMoodle(account);
                    break;
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_CREDS_INVALID, account.getLocale(), source.getFriendlyName()));
    }

    public String addGoogleImportCode(StirlingAccount account, String authCode) {
        return GClassroomHandler.getInstance().addGoogleClassroomCreds(account, authCode);
    }

    public void importAllDaymap(StirlingAccount account) {
        if (credentialsExist(account, ImportSource.DAYMAP)) {
            Thread j = new Thread(() -> {
                List<ImportableClass> classes = getDaymapCourses(account);
                classes.forEach(c -> {
                    Thread t = new Thread(() -> {
                        StirlingClass stirlingClass;
                        if (classManager.classExists(c.getClassName())) {
                            stirlingClass = classManager.getByOwner(c.getId());
                        } else {
                            stirlingClass = importDaymapCourse(account, c);
                        }

                        Thread importThread = new Thread(() -> importDaymapCourse(account, c));
                        importThread.start();

                        List<UUID> students = Lists.newArrayList();
                        try {
                            students.addAll(stirlingClass.getStudents());
                        } catch (NullPointerException ignored) {
                        }

                        if (!students.contains(account.getUuid())) {
                            students.add(account.getUuid());
                            classManager.updateField(stirlingClass, "students", students);
                        }

                        Map<UUID, ClassRole> members = Maps.newHashMap();
                        try {
                            members.putAll(stirlingClass.getMembers());
                        } catch (NullPointerException ignored) {
                        }

                        if (!members.containsKey(account.getUuid())) {
                            members.put(account.getUuid(), ClassRole.STUDENT);
                            classManager.updateField(stirlingClass, "members", members);
                        }
                    });
                    t.start();
                });
            });
            j.start();
        }
    }

    public StirlingClass importDaymapCourse(StirlingAccount account, ImportableClass clazz) {
        DaymapClass daymapClass = DaymapScraper.getInstance().getFullCourse(getByUuid(account.getUuid()), clazz, false);
        return classManager.getByOwner(daymapClass.getId());
    }

    private void importAllMoodle(StirlingAccount account) {
        if (credentialsExist(account, ImportSource.MOODLE)) {
            List<ImportableClass> classes = MoodleScraper.getInstance().getCourses(account);

            classes.forEach(c -> {
                Thread t = new Thread(() -> {
                    getMoodleClass(account, c);
                });
                t.start();
            });
        }
    }

    public ImportCredential getCreds(StirlingAccount account, ImportSource source) {
        ImportAccount acc = getByUuid(account.getUuid());
        if (credentialsExist(acc, source)) {
            return acc.getCredentials().get(source);
        }
        return null;
    }

    public boolean areCredentialsValid(StirlingAccount account, ImportSource source) {
        ImportAccount acc = getByUuid(account.getUuid());
        switch (source) {
            case DAYMAP:
                return DaymapScraper.getInstance().areCredentialsValid(acc.getCredentials().get(source));
            case MOODLE:
                return MoodleScraper.getInstance().areCredentialsValid(acc.getCredentials().get(source));
        }
        return false;
    }

    public boolean credentialsExist(StirlingAccount account, ImportSource source) {
        return credentialsExist(getByUuid(account.getUuid()), source);
    }

    public boolean credentialsExist(ImportAccount account, ImportSource source) {
        List<ImportCredential> credentials = Lists.newArrayList();

        account.getCredentials().forEach((src, cred) -> {
            if (src == source) {
                try {
                    credentials.add(cred);
                } catch (NullPointerException ignored) {
                }
            }
        });

        return credentials.size() > 0;
    }

    public String removeImportCredential(StirlingAccount account, ImportSource source) {
        if (importAccExists(account.getUuid())) {
            ImportAccount acc = getByUuid(account.getUuid());

            Map<ImportSource, ImportCredential> creds = Maps.newHashMap();
            creds.putAll(acc.getCredentials());

            creds.forEach((src, cred) -> {
                if (src.equals(source)) {
                    creds.remove(cred);
                }
            });

            importDAO.updateField(acc, "credentials", creds);
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_REMOVED, account.getLocale(), source.getFriendlyName()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_DOES_NOT_EXIST, account.getLocale()));
    }

    public String createClassFromDaymap(String courseId, String courseName, String room, LessonTimeSlot slot) {
        ClassManager mgr = ClassManager.getInstance();
        if (mgr.getByOwner(courseId) == null) {
            StirlingClass clazz = new StirlingClass(courseId, courseName, "", room, slot);
            UtilFile.getInstance().createClassFolder(clazz.getUuid());
            CalendarManager.getInstance().createCalendar(clazz.getUuid(), courseName, "", Lists.newArrayList());
            mgr.saveClass(clazz);

            mgr.generateCalendarLessons(clazz.getUuid(), slot, ClassLength.SEMESTER);

            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CREATED, StirlingLocale.ENGLISH, courseName));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ALREADY_EXISTS, StirlingLocale.ENGLISH, courseName));
    }

    public void addNotesToDaymapClass(String courseId, List<StirlingPostable> notes) {
        ClassManager mgr = ClassManager.getInstance();
        if (mgr.getByOwner(courseId) != null) {
            StirlingClass clazz = mgr.getByOwner(courseId);
            List<StirlingPostable> classNotes = Lists.newArrayList();

            try {
                classNotes.addAll(clazz.getClassNotes());
            } catch (NullPointerException ignored) {
            }

            notes.forEach(note -> {
                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                classNotes.forEach(n -> {
                    if (n.getTitle().equals(note.getTitle()) &&
                      n.getPostDateTime().getDate().equals(note.getPostDateTime().getDate())) {
                        contains.complete(true);
                    }
                });

                if (!contains.getNow(false)) {
                    classNotes.add(note);
                }
            });

            UtilLog.getInstance().log("Adding notes to the daymap course: " + courseId + "!");
            mgr.updateField(clazz, "classNotes", classNotes);
        }
    }

    public void addHomeworkToDaymapClass(String courseId, List<StirlingPostable> homework) {
        ClassManager mgr = ClassManager.getInstance();
        if (mgr.getByOwner(courseId) != null) {
            StirlingClass clazz = mgr.getByOwner(courseId);
            List<StirlingPostable> hwList = Lists.newArrayList();

            try {
                hwList.addAll(clazz.getHomework());
            } catch (NullPointerException ignored) {
            }

            homework.forEach(hw -> {
                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                hwList.forEach(h -> {
                    if (h.getTitle().equals(hw.getTitle()) &&
                      h.getPostDateTime().getDate().equals(hw.getPostDateTime().getDate())) {
                        contains.complete(true);
                    }
                });

                if (!contains.getNow(false)) {
                    hwList.add(hw);
                }
            });

            UtilLog.getInstance().log("Adding homework to the daymap course: " + courseId + "!");
            mgr.updateField(clazz, "homework", hwList);
        }
    }

    public void addResourcesToDaymapClass(String courseId, List<AttachableResource> resources) {
        ClassManager mgr = ClassManager.getInstance();
        if (mgr.getByOwner(courseId) != null) {
            StirlingClass clazz = mgr.getByOwner(courseId);
            List<AttachableResource> resourceList = Lists.newArrayList();

            try {
                resourceList.addAll(clazz.getResources());
            } catch (NullPointerException ignored) {
            }

            resources.forEach(res -> {
                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                resourceList.forEach(r -> {
                    if (res.getFilePath().equals(r.getFilePath())) {
                        contains.complete(true);
                    }
                });

                if (!contains.getNow(false)) {
                    resourceList.add(res);
                }
            });

            UtilLog.getInstance().log("Adding resources to the daymap course: " + courseId + "!");
            mgr.updateField(clazz, "resources", resourceList);
        }
    }

    public void addAssignmentsToDaymapClass(String courseId, UUID studentUuid, List<StirlingAssignment> assignments) {
        ClassManager classManager = ClassManager.getInstance();
        if (classManager.getByOwner(courseId) != null) {
            StirlingClass stirlingClass = classManager.getByOwner(courseId);
            List<AssignmentAccount> assignmentList = Lists.newArrayList();

            try {
                assignmentList.addAll(stirlingClass.getStudentAssignments());
            } catch (NullPointerException ignored) {
            }

            CompletableFuture<AssignmentAccount> future = new CompletableFuture<>();
            assignmentList.forEach(a -> {
                if (a.getUuid().equals(studentUuid)) {
                    future.complete(a);
                }
            });

            // TODO: 7/12/17 create the account
            AssignmentAccount assAccount = future.getNow(null);
            if (assAccount == null) {
                assignments.forEach(a -> AssignmentManager.getInstance().addAssignment(studentUuid, a));
                assignmentList.add(AssignmentManager.getInstance().getByUuid(studentUuid));
                classManager.updateField(stirlingClass, "studentAssignments", assignmentList);
                UtilLog.getInstance().log("Adding assignments to the daymap course: " + courseId + "!");
                return;
            }

            List<StirlingAssignment> stirlingAssignments = Lists.newArrayList();
            try {
                stirlingAssignments.addAll(assAccount.getAssignments());
            } catch (NullPointerException ignored) {
            }

            assignments.forEach(a -> {
                CompletableFuture<Boolean> contains = new CompletableFuture<>();
                stirlingAssignments.forEach(as -> {
                    if (as.getTitle().equalsIgnoreCase(a.getTitle())) {
                        contains.complete(true);
                    }

                    if (!contains.getNow(false)) {
                        AssignmentManager.getInstance().addAssignment(studentUuid, a);
                        stirlingAssignments.add(a);
                    }
                });
            });

            UtilLog.getInstance().log("Adding assignments to the daymap course: " + courseId + "!");
            AssignmentManager.getInstance().updateField(studentUuid, "assignments", stirlingAssignments);
        }
    }

    public void updateField(ImportAccount account, String field, Object value) {
        importDAO.updateField(account, field, value);
    }

    public List<ImportAccount> getAllAccounts() {
        return importDAO.getAllImportAccounts();
    }

    public ImportAccount getByUuid(UUID uuid) {
        return importDAO.getByUuid(uuid);
    }

    public boolean importAccExists(UUID uuid) {
        if (getByUuid(uuid) == null) {
            return false;
        }
        return true;
    }
}
