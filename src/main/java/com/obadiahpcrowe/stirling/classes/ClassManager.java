package com.obadiahpcrowe.stirling.classes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.calendar.CalendarManager;
import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.obj.*;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ClassesDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ClassesDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilLog;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

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

    private static ClassManager instance;

    private MorphiaService morphiaService;
    private ClassesDAO classesDAO;
    private Gson gson = new Gson();

    public ClassManager() {
        this.morphiaService = new MorphiaService();
        this.classesDAO = new ClassesDAOImpl(StirlingClass.class, morphiaService.getDatastore());
    }

    public String createClass(StirlingAccount account, String name, String desc, String room, LessonTimeSlot timeSlot) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (!classExists(name)) {
                StirlingClass clazz = new StirlingClass(account, name, desc, room);
                UtilFile.getInstance().createClassFolder(clazz.getUuid());
                CalendarManager.getInstance().createCalendar(clazz.getUuid(), name, desc, Lists.newArrayList());
                generateCalendarLessons(clazz.getUuid(), timeSlot);

                classesDAO.save(clazz);
                return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_CREATED, account.getLocale(), name));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_ALREADY_EXISTS, account.getLocale(), name));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "create classes", "TEACHER"));
    }

    public StirlingCalendar getClassCalendar(StirlingAccount account, UUID classUuid) {
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
                    section.getChildren().add(postable);

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

                    section.getChildren().forEach(s -> {
                        if (s instanceof StirlingPostable) {
                            StirlingPostable postable = (StirlingPostable) s;
                            if (postable.getUuid().equals(postableUuid)) {
                                postableFuture.complete(s);
                            }
                        }
                    });

                    List<Object> objects = Lists.newArrayList(section.getChildren());
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

    public String createAssignment(StirlingAccount account, UUID classUuid, String title, String desc, StirlingDate dueDate, int maxMarks, double weighting) {
        if (isAccountHighEnough(account, AccountType.TEACHER)) {
            if (classExists(classUuid)) {
                StirlingClass clazz = getByUuid(classUuid);

                StirlingAssignment assignment = new StirlingAssignment(title, desc,
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
                for (UUID uuid : studentUuids) {
                    if (students.contains(uuid)) {
                        students.remove(uuid);
                    }
                }

                classesDAO.updateField(clazz, "students", students);
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
        return "";
    }

    public String deleteHomeworkTask(StirlingAccount account, UUID classUuid, UUID homeworkUuid) {
        return "";
    }

    public String createClassNote(StirlingAccount account, UUID classUuid, String title, String content,
                                  List<AttachableResource> resources) {
        return "";
    }

    public String deleteClassNote(StirlingAccount account, UUID classUuid, UUID homeworkUuid) {
        return "";
    }

    public String createResource(StirlingAccount account, UUID classUuid, String title, String content,
                                 List<AttachableResource> resources) {
        return "";
    }

    public String deleteResource(StirlingAccount account, UUID classUuid, UUID homeworkUuid) {
        return "";
    }

    public String uploadAssignment(StirlingAccount account, UUID classUuid, UUID assignmentUuid,
                                   List<AttachableResource> resources) {
        return "";
    }

    public String markAssignment(StirlingAccount account, UUID classUuid, UUID assignmentUuid, UUID studentUuid,
                                 int receivedMarks, String grade, double weighting, String comments) {
        return "";
    }

    public String assignProgressMarker(StirlingAccount account, UUID classUuid, UUID studentUuid, String name,
                                       String desc, StirlingDate dueDateTime) {
        return "";
    }

    public String removeProgressMarker(StirlingAccount account, UUID classUuid, UUID studentUuid, UUID markerUuid) {
        return "";
    }

    public StirlingClass getByOwner(String owner) {
        return classesDAO.getByOwner(owner);
    }

    private String generateCalendarLessons(UUID classUuid, LessonTimeSlot lessonTimeslot) {
        // TODO: 13/10/17 Very important
        return "";
    }

    // We're all thinking it ;)
    private boolean isAccountHighEnough(StirlingAccount account, AccountType targetType) {
        return account.getAccountType().getAccessLevel() >= targetType.getAccessLevel();
    }

    public static ClassManager getInstance() {
        if (instance == null)
            instance = new ClassManager();
        return instance;
    }
}
