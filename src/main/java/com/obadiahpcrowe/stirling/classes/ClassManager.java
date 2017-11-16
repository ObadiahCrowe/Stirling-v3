package com.obadiahpcrowe.stirling.classes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.calendar.CalendarManager;
import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.classes.enums.*;
import com.obadiahpcrowe.stirling.classes.enums.fields.LessonField;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.*;
import com.obadiahpcrowe.stirling.classes.terms.TermLength;
import com.obadiahpcrowe.stirling.classes.terms.TermManager;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ClassesDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilLog;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 11:55 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes
 * Copyright (c) Obadiah Crowe 2017
 */
public class ClassManager {

    // TODO: 16/10/17 if contains for all maps. fml.

    private static ClassManager instance;

    private MorphiaService morphiaService;
    private ClassesDAO classesDAO;
    private Gson gson;

    public ClassManager() {
        this.morphiaService = new MorphiaService();
        this.classesDAO = new ClassesDAOImpl(StirlingClass.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public String createClass(StirlingAccount account, String name, String desc, String room, LessonTimeSlot timeSlot) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (!classExists(name)) {
                StirlingClass clazz = new StirlingClass(account, name, desc, room, timeSlot);
                UtilFile.getInstance().createClassFolder(clazz.getUuid());
                CalendarManager.getInstance().createCalendar(clazz.getUuid(), name, desc, Lists.newArrayList());
                classesDAO.save(clazz);

                generateCalendarLessons(clazz.getUuid(), timeSlot, ClassLength.SEMESTER);

                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CREATED, account.getLocale(), name));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ALREADY_EXISTS, account.getLocale(), name));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create classes", "TEACHER"));
    }

    public StirlingCalendar getClassTimetable(StirlingAccount account, UUID classUuid) {
        if (classExists(classUuid)) {
            StirlingClass clazz = getByUuid(classUuid);

            if (clazz.getStudents().contains(account.getUuid())) {
                return CalendarManager.getInstance().getCalendar(clazz.getUuid());
            }
            return null;
        }
        return null;
    }

    public String deleteClass(StirlingAccount account, UUID classUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
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

    public String addClassesToAccount(StirlingAccount account, UUID... classUuids) {
        List<StirlingClass> classes = Lists.newArrayList();
        for (UUID uuid : classUuids) {
            if (classExists(uuid)) {
                try {
                    classes.addAll(account.getStirlingClasses());
                } catch (NullPointerException ignored) {
                }

                CompletableFuture<Boolean> exists = new CompletableFuture<>();

                classes.forEach(c -> {
                    if (c.getUuid().equals(uuid)) {
                        exists.complete(true);
                    }
                });

                if (!exists.getNow(false)) {
                    classes.add(getByUuid(uuid));
                }
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
            }
        }

        AccountManager.getInstance().updateField(account, "stirlingClasses", classes);
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_STUDENT_ADDED, account.getLocale(), "multiple classes", account.getAccountName()));
    }

    public String addClassToAccount(StirlingAccount account, UUID classUuid) {
        if (classExists(classUuid)) {
            List<StirlingClass> classes = Lists.newArrayList();
            try {
                classes.addAll(account.getStirlingClasses());
            } catch (NullPointerException ignored) {
            }

            CompletableFuture<Boolean> exists = new CompletableFuture<>();

            classes.forEach(c -> {
                if (c.getUuid().equals(classUuid)) {
                    exists.complete(true);
                }
            });

            if (!exists.getNow(false)) {
                classes.add(getByUuid(classUuid));
            }

            AccountManager.getInstance().updateField(account, "stirlingClasses", classes);
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_STUDENT_ADDED, account.getLocale(),
              classUuid.toString(), account.getAccountName()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
    }

    public StirlingClass getByUuid(UUID classUuid) {
        return classesDAO.getByUuid(classUuid);
    }

    public StirlingClass getByName(String className) {
        return classesDAO.getByName(className);
    }

    public List<StirlingClass> getAllClasses(StirlingAccount account) {
        List<StirlingClass> classes = Lists.newArrayList();
        account.getStirlingClasses().forEach(c -> {
            classes.add(getByUuid(c.getUuid()));
        });

        return classes;
    }

    public boolean classExists(UUID classUuid) {
        if (getByUuid(classUuid) == null) {
            return false;
        }
        return true;
    }

    public boolean classExists(String className) {
        return getByName(className) != null;
    }

    public String setRoom(StirlingAccount account, UUID classUuid, String roomName) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                classesDAO.updateField(clazz, "room", roomName);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ROOM_CHANGED, account.getLocale(), clazz.getName(), roomName));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "set the room", "TEACHER"));
    }

    public String setName(StirlingAccount account, UUID classUuid, String className) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                classesDAO.updateField(clazz, "name", className);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_NAME_CHANGED, account.getLocale(), clazz.getName(), className));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "set the name", "TEACHER"));
    }

    public String setDesc(StirlingAccount account, UUID classUuid, String desc) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                classesDAO.updateField(clazz, "desc", desc);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DESC_CHANGED, account.getLocale(), clazz.getName(), desc));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "set the description", "TEACHER"));
    }

    public String addSection(StirlingAccount account, UUID classUuid, String title, String desc) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                StirlingSection section = new StirlingSection(clazz.getUuid(), title, desc);
                List<StirlingSection> sections = Lists.newArrayList(clazz.getSections());
                sections.add(section);

                classesDAO.updateField(clazz, "sections", sections);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_SECTION_ADDED, account.getLocale(), title, clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "add sections", "TEACHER"));
    }

    public String removeSection(StirlingAccount account, UUID classUuid, UUID sectionUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingSection> sections = Lists.newArrayList(clazz.getSections());
                clazz.getSections().forEach(section -> {
                    if (section.getSectionUuid().equals(sectionUuid)) {
                        sections.remove(section);
                    }
                });

                classesDAO.updateField(clazz, "sections", sections);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_SECTION_REMOVED, account.getLocale(), sectionUuid.toString(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove sections", "TEACHER"));
    }

    public String addPostable(StirlingAccount account, UUID classUuid, UUID sectionUuid, String title,
                              String content, List<AttachableResource> resources) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                CompletableFuture<StirlingSection> future = new CompletableFuture<>();

                clazz.getSections().forEach(section -> {
                    if (section.getSectionUuid().equals(sectionUuid)) {
                        future.complete(section);
                    }
                });

                try {
                    StirlingSection section = future.get();
                    StirlingPostable postable = new StirlingPostable(title, content, resources);
                    section.getClassNotes().add(postable);

                    List<StirlingSection> sections = Lists.newArrayList(clazz.getSections());
                    clazz.getSections().forEach(s -> {
                        if (s.getSectionUuid().equals(section.getSectionUuid())) {
                            sections.remove(s);
                        }
                    });
                    sections.add(section);

                    classesDAO.updateField(clazz, "sections", sections);
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_POSTABLE_ADDED, account.getLocale(), section.getTitle()));
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_SECTION_DOES_NOT_EXIST, account.getLocale(), sectionUuid.toString()));
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "add postables", "TEACHER"));
    }

    public String removePostable(StirlingAccount account, UUID classUuid, UUID sectionUuid, UUID postableUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                CompletableFuture<StirlingSection> future = new CompletableFuture<>();

                clazz.getSections().forEach(s -> {
                    if (s.getSectionUuid().equals(sectionUuid)) {
                        future.complete(s);
                    }
                });

                try {
                    StirlingSection section = future.get();
                    CompletableFuture<Object> postableFuture = new CompletableFuture<>();

                    section.getClassNotes().forEach(s -> {
                        if (s instanceof StirlingPostable) {
                            StirlingPostable postable = (StirlingPostable) s;
                            if (postable.getUuid().equals(postableUuid)) {
                                postableFuture.complete(s);
                            }
                        }
                    });

                    List<Object> objects = Lists.newArrayList(section.getClassNotes());
                    objects.remove(postableFuture.get());

                    List<StirlingSection> sections = Lists.newArrayList(clazz.getSections());
                    clazz.getSections().forEach(s -> {
                        if (s.getSectionUuid().equals(section.getSectionUuid())) {
                            sections.remove(s);
                        }
                    });
                    sections.add(section);

                    classesDAO.updateField(clazz, "sections", sections);
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_POSTABLE_REMOVED, account.getLocale(), section.getTitle()));
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_SECTION_DOES_NOT_EXIST, account.getLocale(), sectionUuid.toString()));
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove postables", "TEACHER"));
    }

    public String addCatchupModule(StirlingAccount account, UUID classUuid, UUID lessonUuid, String title,
                                   String content, List<AttachableResource> resources) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                List<StirlingCatchup> catchups = Lists.newArrayList(clazz.getCatchups());
                StirlingCatchup catchup = new StirlingCatchup(lessonUuid, title, content, resources);

                catchups.add(catchup);
                classesDAO.updateField(clazz, "catchups", catchups);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CATCHUP_ADDED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "add catchup modules", "TEACHER"));
    }

    public String removeCatchupModule(StirlingAccount account, UUID classUuid, UUID lessonUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingCatchup> catchups = Lists.newArrayList(clazz.getCatchups());
                clazz.getCatchups().forEach(c -> {
                    if (c.getLessonUuid().equals(lessonUuid)) {
                        catchups.remove(c);
                    }
                });

                classesDAO.updateField(clazz, "catchups", catchups);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CATCHUP_REMOVED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove catchup modules", "TEACHER"));
    }

    public String createAssignment(StirlingAccount account, UUID classUuid, String title, String desc, AssignmentType type, boolean formative,
                                   StirlingDate dueDate, int maxMarks, double weighting) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                StirlingAssignment assignment = new StirlingAssignment(title, desc, type, formative,
                  new StirlingResult(0, maxMarks, "", weighting, ""), dueDate);

                Map<UUID, List<StirlingAssignment>> assignments = Maps.newHashMap(clazz.getStudentAssignments());

                clazz.getStudents().forEach(student -> {
                    if (assignments.containsKey(student)) {
                        List<StirlingAssignment> sAssignments = Lists.newArrayList(assignments.get(student));
                        sAssignments.add(assignment);

                        assignments.remove(student);
                        assignments.put(student, sAssignments);
                    } else {
                        assignments.put(student, Lists.newArrayList(assignment));
                    }
                });

                classesDAO.updateField(clazz, "studentAssignments", assignments);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_ADDED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create assignments", "TEACHER"));
    }

    public String removeAssignmentForStudent(StirlingAccount account, UUID classUuid, UUID studentUuid, UUID assignmentUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                CompletableFuture<String> assignmentName = new CompletableFuture<>();

                List<StirlingAssignment> assignments = Lists.newArrayList(clazz.getStudentAssignments().get(studentUuid));
                clazz.getStudentAssignments().get(studentUuid).forEach(assignment -> {
                    if (assignment.getUuid().equals(assignmentUuid)) {
                        assignments.remove(assignment);
                        assignmentName.complete(assignment.getTitle());
                    }
                });

                classesDAO.updateField(clazz, "studentAssignments", assignments);
                try {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_REMOVED_SINGLE,
                      account.getLocale(), assignmentName.get(), AccountManager.getInstance().getAccount(studentUuid).getAccountName()));
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_DOES_NOT_EXIST, account.getLocale(), assignmentUuid.toString()));
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete assignments", "TEACHER"));
    }

    public String removeAssignmentForAll(StirlingAccount account, UUID classUuid, UUID assignmentUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                Map<UUID, List<StirlingAssignment>> assignments = Maps.newHashMap(clazz.getStudentAssignments());
                CompletableFuture<String> assignmentName = new CompletableFuture<>();

                clazz.getStudentAssignments().forEach((u, a) -> {
                    a.forEach(assignment -> {
                        if (assignment.getUuid().equals(assignmentUuid)) {
                            assignmentName.complete(assignment.getTitle());
                            assignments.forEach((c, b) -> {
                                if (b.contains(assignment)) {
                                    b.remove(assignment);
                                }
                            });
                        }
                    });
                });

                classesDAO.updateField(clazz, "studentAssignments", assignments);
                try {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_REMOVED_ALL, account.getLocale(), assignmentName.get()));
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_DOES_NOT_EXIST, account.getLocale(), assignmentUuid.toString()));
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete assignments", "TEACHER"));
    }

    public String addStudents(StirlingAccount account, UUID classUuid, UUID... studentUuids) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<UUID> students = Lists.newArrayList(clazz.getStudents());
                for (UUID uuid : studentUuids) {
                    if (!students.contains(uuid)) {
                        students.add(uuid);
                    }
                }

                classesDAO.updateField(clazz, "students", students);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_MULTIPLE_STUDENTS_ADDED, StirlingLocale.ENGLISH, clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "add students", "TEACHER"));
    }

    public String addStudentThroughID(UUID studentUuid, String id) {
        if (AccountManager.getInstance().accountExists(studentUuid)) {
            if (getByOwner(id) != null) {
                StirlingClass clazz = getByOwner(id);
                List<UUID> students = Lists.newArrayList(clazz.getStudents());
                if (!students.contains(studentUuid)) {
                    students.add(studentUuid);
                }

                classesDAO.updateField(clazz, "students", students);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_STUDENT_ADDED, StirlingLocale.ENGLISH, studentUuid.toString(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, StirlingLocale.ENGLISH, id));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, studentUuid.toString()));
    }

    public String removeStudents(StirlingAccount account, UUID classUuid, UUID... studentUuids) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<UUID> students = Lists.newArrayList(clazz.getStudents());
                Map<UUID, ClassRole> roles = Maps.newHashMap(clazz.getMembers());
                Map<UUID, List<StirlingAssignment>> assignments = Maps.newHashMap(clazz.getStudentAssignments());
                Map<UUID, List<StirlingResult>> results = Maps.newHashMap(clazz.getStudentResults());
                Map<UUID, List<ProgressMarker>> progressMarkers = Maps.newHashMap(clazz.getProgressMarkers());

                for (UUID uuid : studentUuids) {
                    if (students.contains(uuid)) {
                        students.remove(uuid);
                    }

                    if (roles.containsKey(uuid)) {
                        roles.remove(uuid);
                    }

                    if (assignments.containsKey(uuid)) {
                        assignments.remove(uuid);
                    }

                    if (results.containsKey(uuid)) {
                        results.remove(uuid);
                    }

                    if (progressMarkers.containsKey(uuid)) {
                        progressMarkers.remove(uuid);
                    }
                }

                classesDAO.updateField(clazz, "students", students);
                classesDAO.updateField(clazz, "members", roles);
                classesDAO.updateField(clazz, "studentAssignments", assignments);
                classesDAO.updateField(clazz, "studentResults", results);
                classesDAO.updateField(clazz, "progressMarkers", progressMarkers);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_MULTIPLE_STUDENTS_REMOVED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove students", "TEACHER"));
    }

    public String removeStudentThroughID(UUID studentUuid, String id) {
        if (AccountManager.getInstance().accountExists(studentUuid)) {
            if (getByOwner(id) != null) {
                StirlingClass clazz = getByOwner(id);
                List<UUID> students = Lists.newArrayList(clazz.getStudents());
                if (students.contains(studentUuid)) {
                    students.remove(studentUuid);
                }

                classesDAO.updateField(clazz, "students", students);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_STUDENT_REMOVED, StirlingLocale.ENGLISH, studentUuid.toString(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, StirlingLocale.ENGLISH, id));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, studentUuid.toString()));
    }

    public String addTeachers(StirlingAccount account, UUID classUuid, UUID... teacherUuids) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<UUID> teachers = Lists.newArrayList(teacherUuids);
                teachers.addAll(clazz.getTeachers());

                classesDAO.updateField(clazz, "teachers", teachers);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_TEACHERS_ADDED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "add teachers", "TEACHER"));
    }

    public String removeTeachers(StirlingAccount account, UUID classUuid, UUID... teacherUuids) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<UUID> finalTeachers = Lists.newArrayList(clazz.getTeachers());
                List<UUID> teachers = Lists.newArrayList(teacherUuids);
                clazz.getTeachers().forEach(teacher -> {
                    if (teachers.contains(teacher)) {
                        finalTeachers.remove(teacher);
                    }
                });

                classesDAO.updateField(clazz, "teachers", finalTeachers);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_TEACHERS_REMOVED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove teachers", "TEACHER"));
    }

    public String createHomeworkTask(StirlingAccount account, UUID classUuid, String title, String content,
                                     List<AttachableResource> resources) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                StirlingPostable hwTask = new StirlingPostable(title, content, resources);

                List<StirlingPostable> homework = Lists.newArrayList(clazz.getHomework());
                homework.add(hwTask);

                classesDAO.updateField(clazz, "homework", homework);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_HOMEWORK_ADDED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create homework tasks", "TEACHER"));
    }

    public String deleteHomeworkTask(StirlingAccount account, UUID classUuid, UUID homeworkUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingPostable> homework = Lists.newArrayList(clazz.getHomework());
                homework.forEach(hw -> {
                    if (hw.getUuid().equals(homeworkUuid)) {
                        homework.remove(hw);
                    }
                });

                if (homework.size() == clazz.getHomework().size()) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_HOMEWORK_DOES_NOT_EXIST, account.getLocale()));
                }

                classesDAO.updateField(clazz, "homework", homework);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_HOMEWORK_REMOVED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete homework tasks", "TEACHER"));
    }

    public String createClassNote(StirlingAccount account, UUID classUuid, String title, String content,
                                  List<AttachableResource> resources) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                StirlingPostable note = new StirlingPostable(title, content, resources);

                List<StirlingPostable> notes = Lists.newArrayList(clazz.getClassNotes());
                notes.add(note);

                classesDAO.updateField(clazz, "classNotes", notes);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_NOTE_ADDED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create class notes", "TEACHER"));
    }

    public String deleteClassNote(StirlingAccount account, UUID classUuid, UUID noteUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingPostable> notes = Lists.newArrayList(clazz.getClassNotes());
                notes.forEach(note -> {
                    if (note.getUuid().equals(noteUuid)) {
                        notes.remove(note);
                    }
                });

                if (notes.size() == clazz.getClassNotes().size()) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_NOTE_DOES_NOT_EXIST, account.getLocale()));
                }

                classesDAO.updateField(clazz, "classNotes", notes);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_NOTE_REMOVED, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete class notes", "TEACHER"));
    }

    public String createResource(StirlingAccount account, UUID classUuid, String title, MultipartFile multipartFile) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);
                AttachableResource resource = new AttachableResource(classUuid, multipartFile.getOriginalFilename(), ARType.CLASS);
                File file = new File(resource.getFile().getPath());

                try {
                    multipartFile.transferTo(file);
                } catch (IOException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "creating the resource"));
                }

                List<StirlingResource> resources = Lists.newArrayList(clazz.getResources());
                resources.add(new StirlingResource(resource, title));

                classesDAO.updateField(clazz, "resources", resources);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_RESOURCE_ADDED, account.getLocale(), file.getName(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create resources", "TEACHER"));
    }

    public String deleteResource(StirlingAccount account, UUID classUuid, String filePath) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingResource> resources = Lists.newArrayList(clazz.getResources());
                resources.forEach(resource -> {
                    if (resource.getFilePath().equalsIgnoreCase(filePath)) {
                        resources.remove(resource);
                    }
                });

                if (resources.size() == clazz.getResources().size()) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_RESOURCE_DOES_NOT_EXIST, account.getLocale()));
                }

                classesDAO.updateField(clazz, "resources", resources);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_RESOURCE_DELETED, account.getLocale(), filePath));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "delete resources", "TEACHER"));
    }

    public String getResource(StirlingAccount account, UUID classUuid, String filePath) {
        if (classExists(classUuid)) {
            StirlingClass clazz = getByUuid(classUuid);

            if (!clazz.getStudents().contains(account.getUuid())) {
                return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
            }

            CompletableFuture<StirlingResource> future = new CompletableFuture<>();

            clazz.getResources().forEach(resource -> {
                if (resource.getFilePath().equalsIgnoreCase(filePath)) {
                    future.complete(resource);
                }
            });

            try {
                return gson.toJson(future.get());
            } catch (InterruptedException | ExecutionException e) {
                UtilLog.getInstance().log(e.getMessage());
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_RESOURCE_DOES_NOT_EXIST, account.getLocale()));
            }
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
    }

    public String uploadAssignment(StirlingAccount account, UUID classUuid, UUID assignmentUuid,
                                   List<AttachableResource> resources) {
        if (account.getAccountType() == AccountType.STUDENT) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                if (!clazz.getStudents().contains(account.getUuid())) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
                }

                Map<UUID, List<StirlingAssignment>> assignments = Maps.newHashMap(clazz.getStudentAssignments());
                List<StirlingAssignment> userAssignments = assignments.get(account.getUuid());
                CompletableFuture<StirlingAssignment> assignment = new CompletableFuture<>();

                userAssignments.forEach(stirlingAssignment -> {
                    if (stirlingAssignment.getUuid().equals(assignmentUuid)) {
                        assignment.complete(stirlingAssignment);
                        userAssignments.remove(assignment);
                    }
                });

                try {
                    StirlingAssignment a = assignment.get();
                    a.getSubmittedFiles().addAll(resources);

                    userAssignments.add(a);

                    assignments.replace(account.getUuid(), userAssignments);
                    classesDAO.updateField(clazz, "studentAssignments", assignments);
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_SUBMITTED, account.getLocale(), assignment.get().getTitle()));
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_DOES_NOT_EXIST, account.getLocale(), assignmentUuid.toString()));
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "upload an assignment", "STUDENT"));
    }

    public String markAssignment(StirlingAccount account, UUID classUuid, UUID assignmentUuid, UUID studentUuid,
                                 int receivedMarks, String grade, double weighting, String comments) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                if (!clazz.getStudents().contains(studentUuid)) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
                }

                Map<UUID, List<StirlingAssignment>> assignments = Maps.newHashMap(clazz.getStudentAssignments());
                if (assignments.containsKey(studentUuid)) {
                    List<StirlingAssignment> list = assignments.get(studentUuid);
                    CompletableFuture<StirlingAssignment> future = new CompletableFuture<>();
                    list.forEach(ass -> {
                        if (ass.getUuid().equals(assignmentUuid)) {
                            list.remove(ass);
                            ass.setResult(new StirlingResult(receivedMarks, ass.getResult().getMaxMarks(), grade, weighting, comments));
                            future.complete(ass);
                        }
                    });

                    try {
                        list.add(future.get());
                        assignments.replace(studentUuid, list);
                    } catch (InterruptedException | ExecutionException e) {
                        UtilLog.getInstance().log(e.getMessage());
                        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_DOES_NOT_EXIST, account.getLocale(), assignmentUuid.toString()));
                    }
                }

                classesDAO.updateField(clazz, "studentAssignments", assignments);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ASSIGNMENT_MARKED, account.getLocale(),
                  AccountManager.getInstance().getAccount(studentUuid).getDisplayName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "mark an assignment", "TEACHER"));
    }

    public String assignProgressMarker(StirlingAccount account, UUID classUuid, UUID studentUuid, String name, String desc) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                if (!clazz.getStudents().contains(studentUuid)) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
                }

                Map<UUID, List<ProgressMarker>> progressMarkers = Maps.newHashMap(clazz.getProgressMarkers());

                if (!progressMarkers.containsKey(studentUuid)) {
                    progressMarkers.put(studentUuid, Lists.newArrayList());
                }

                List<ProgressMarker> markers = progressMarkers.get(studentUuid);
                markers.add(new ProgressMarker(name, desc, StirlingDate.getNow()));

                progressMarkers.replace(studentUuid, markers);

                classesDAO.updateField(clazz, "progressMarkers", progressMarkers);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_PROGRESS_MARKER_ADDED, account.getLocale(),
                  AccountManager.getInstance().getAccount(studentUuid).getDisplayName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "assign a progress marker", "TEACHER"));
    }

    public String removeProgressMarker(StirlingAccount account, UUID classUuid, UUID studentUuid, UUID markerUuid) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                if (!clazz.getStudents().contains(studentUuid)) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
                }

                Map<UUID, List<ProgressMarker>> markerMap = Maps.newHashMap();
                try {
                    markerMap.putAll(clazz.getProgressMarkers());
                } catch (NullPointerException ignored) {
                }

                if (!markerMap.containsKey(studentUuid)) {
                    markerMap.put(studentUuid, Lists.newArrayList());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_PROGRESS_MARKER_DOES_NOT_EXIST, account.getLocale()));
                }

                List<ProgressMarker> markers = Lists.newArrayList(markerMap.get(studentUuid));
                markers.forEach(marker -> {
                    if (marker.getUuid().equals(markerUuid)) {
                        markers.remove(marker);
                    }
                });

                if (markers.size() == markerMap.get(studentUuid).size()) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_PROGRESS_MARKER_DOES_NOT_EXIST, account.getLocale()));
                }

                markerMap.replace(studentUuid, markers);
                classesDAO.updateField(clazz, "progressMarkers", markerMap);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_PROGRESS_MARKED_REMOVED, account.getLocale(),
                  AccountManager.getInstance().getAccount(studentUuid).getDisplayName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "remove a progress marker", "TEACHER"));
    }

    public String setStudentAttendance(StirlingAccount account, UUID classUuid, UUID lessonUuid, UUID studentUuid, AttendanceStatus status) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingLesson> lessons = Lists.newArrayList();
                try {
                    lessons.addAll(clazz.getLessons());
                } catch (NullPointerException ignored) {
                }
                CompletableFuture<StirlingLesson> lessonFuture = new CompletableFuture<>();

                lessons.forEach(l -> {
                    if (l.getUuid().equals(lessonUuid)) {
                        lessonFuture.complete(l);
                        lessons.remove(l);
                    }
                });

                try {
                    StirlingLesson lesson = lessonFuture.get();
                    Map<UUID, AttendanceStatus> attendanceStatuses = Maps.newHashMap();
                    try {
                        attendanceStatuses.putAll(lesson.getStudentAttendance());
                    } catch (NullPointerException ignored) {
                    }

                    if (!attendanceStatuses.containsKey(studentUuid)) {
                        attendanceStatuses.put(studentUuid, status);
                    } else {
                        attendanceStatuses.replace(studentUuid, status);
                    }

                    lesson.setStudentAttendance(attendanceStatuses);
                    lessons.add(lesson);
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_LESSON_DOES_NOT_EXIST, account.getLocale(), lessonUuid.toString()));
                }

                classesDAO.updateField(clazz, "lessons", lessons);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ATTENDANCE_SET, account.getLocale(),
                  AccountManager.getInstance().getAccount(studentUuid).getDisplayName(), status.getFriendlyName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "set attendance", "TEACHER"));
    }

    public String generateCalendarLessons(UUID classUuid, LessonTimeSlot lessonTimeSlot, ClassLength classLength) {
        if (classExists(classUuid)) {
            TermManager manager = TermManager.getInstance();

            List<TermLength> lengths = manager.getSaTerms(); // TODO: 16/10/17 In future, check the config option
            int currentTerm = manager.getCurrentTerm(lengths);

            switch (currentTerm) {
                case 1:
                    generatePerTerm(lengths, 1, classUuid, lessonTimeSlot);
                    if (classLength.equals(ClassLength.SEMESTER)) {
                        generatePerTerm(lengths, 2, classUuid, lessonTimeSlot);
                    }
                    break;
                case 2:
                    generatePerTerm(lengths, 2, classUuid, lessonTimeSlot);
                    break;
                case 3:
                    generatePerTerm(lengths, 3, classUuid, lessonTimeSlot);
                    if (classLength.equals(ClassLength.SEMESTER)) {
                        generatePerTerm(lengths, 4, classUuid, lessonTimeSlot);
                    }
                    break;
                case 4:
                    generatePerTerm(lengths, 4, classUuid, lessonTimeSlot);
                    break;
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_LESSONS_GENERATED, StirlingLocale.ENGLISH, classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, StirlingLocale.ENGLISH, classUuid.toString()));
    }

    private void generatePerTerm(List<TermLength> lengths, int currentTerm, UUID classUuid, LessonTimeSlot lessonTimeSlot) {
        StirlingClass clazz = getByUuid(classUuid);

        // Arrays start at 0
        LocalDate start = lengths.get(currentTerm - 1).getStartDate();
        LocalDate end = lengths.get(currentTerm - 1).getEndDate();

        List<StirlingLesson> lessons = Lists.newArrayList();
        try {
            lessons.addAll(clazz.getLessons());
        } catch (NullPointerException e) {
            UtilLog.getInstance().log(e.getMessage());
        }

        for (SlotData data : lessonTimeSlot.getWeeklyOccurances()) {
            LocalDate day = start.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(data.getDayOfWeek().toUpperCase())));
            while (day.isBefore(end)) {
                String date = day.getDayOfMonth() + "/" + day.getMonthValue() + "/" + day.getYear();
                CompletableFuture<Boolean> generate = new CompletableFuture<>();

                lessons.forEach(lesson -> {
                    if (lesson.getStartDateTime().getDate().equalsIgnoreCase(date)) {
                        generate.complete(false);
                    }
                });

                if (generate.getNow(true)) {
                    lessons.add(new StirlingLesson(clazz.getName() + " Lesson",
                      "Lesson on " + date, clazz.getRoom(), date, data.getStartTime(), data.getEndTime(),
                      Maps.newHashMap(), null, null));
                }

                day = day.plusWeeks(1);
            }
        }

        classesDAO.updateField(clazz, "lessons", lessons);
    }

    public String editLessonField(StirlingAccount account, UUID classUuid, UUID lessonUuid, LessonField field, Object value) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                List<StirlingLesson> lessons = Lists.newArrayList();
                try {
                    lessons.addAll(clazz.getLessons());
                } catch (NullPointerException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_LESSON_DOES_NOT_EXIST, account.getLocale(), lessonUuid.toString()));
                }

                CompletableFuture<StirlingLesson> future = new CompletableFuture<>();
                lessons.forEach(l -> {
                    if (l.getUuid().equals(lessonUuid)) {
                        future.complete(l);
                        lessons.remove(l);
                    }
                });

                if (clazz.getLessons().size() == lessons.size()) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_LESSON_DOES_NOT_EXIST, account.getLocale(), lessonUuid.toString()));
                }

                try {
                    StirlingLesson lesson = future.get();

                    switch (field) {
                        case TITLE:
                            lesson.setTitle((String) value);
                            break;
                        case DESC:
                            lesson.setDesc((String) value);
                            break;
                        case ROOM:
                            lesson.setLocation((String) value);
                            break;
                        case HOMEWORK:
                            lesson.setHomework((StirlingPostable) value);
                            break;
                        case CLASSNOTE:
                            lesson.setClassNote((StirlingPostable) value);
                            break;
                    }

                    lessons.add(lesson);
                } catch (InterruptedException | ExecutionException e) {
                    UtilLog.getInstance().log(e.getMessage());
                    return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_LESSON_DOES_NOT_EXIST, account.getLocale(), lessonUuid.toString()));
                }

                classesDAO.updateField(clazz, "lessons", lessons);
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "update lessons", "TEACHER"));
    }

    public String addStudentClassHolder(StirlingAccount account, ImportSource source, ImportableClass clazz) {
        //
        return "";
    }

    public void updateField(StirlingClass clazz, String field, Object value) {
        classesDAO.updateField(clazz, field, value);
    }

    public String takeClassOwnership(StirlingAccount account, String ownerId) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            StirlingClass clazz = getByOwner(ownerId);
            if (clazz != null) {
                classesDAO.updateField(clazz, "owner", Lists.newArrayList(account.getAccountName()));
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_OWNERSHIP_TAKEN, account.getLocale(), clazz.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), ownerId));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "take class ownership", "TEACHER"));
    }

    public StirlingClass getByOwner(String owner) {
        return classesDAO.getByOwner(owner);
    }

    // We're all thinking it ;)
    private boolean isAccountHighEnough(StirlingAccount account, AccountType targetType) {
        return account.getAccountType().getAccessLevel() >= targetType.getAccessLevel();
    }

    public void saveClass(StirlingClass clazz) {
        classesDAO.save(clazz);
    }

    public static ClassManager getInstance() {
        if (instance == null)
            instance = new ClassManager();
        return instance;
    }
}
