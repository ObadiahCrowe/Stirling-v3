package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.classes.ClassIdentifier;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.assignments.AssignmentAccount;
import com.obadiahpcrowe.stirling.classes.assignments.AssignmentManager;
import com.obadiahpcrowe.stirling.classes.assignments.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.obj.ProgressMarker;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingSection;
import com.obadiahpcrowe.stirling.classes.progress.MarkerManager;
import com.obadiahpcrowe.stirling.classes.progress.ProgressAccount;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class ClassesAPI implements APIController {

    private AccountManager accountManager = AccountManager.getInstance();
    private ClassManager classManager = ClassManager.getInstance();
    private Gson gson = new Gson();
    private ImportManager importManager = ImportManager.getInstance();

    @CallableAPI(fields = {"accountName", "password", "name", "desc", "room", "timeSlot"})
    @RequestMapping(value = "/stirling/v3/classes/create", method = RequestMethod.GET)
    public String createClass(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("name") String name,
                              @RequestParam("desc") String desc,
                              @RequestParam("room") String room,
                              @RequestParam("timeSlot") String timeSlot) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        LessonTimeSlot slot;
        try {
            slot = LessonTimeSlot.valueOf(timeSlot.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), timeSlot, "timeSlot"));
        }

        return classManager.createClass(account, name, desc, room, slot);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/classes/get/classList", method = RequestMethod.GET)
    public String getClassList(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        Thread dmThread = new Thread(() -> importManager.importAllDaymap(account));
        dmThread.start();

        List<ClassIdentifier> identifiers = Lists.newArrayList();
        classManager.getAllClasses(account).forEach(c -> identifiers.add(new ClassIdentifier(c.getName(), c.getUuid())));

        return LocalisationManager.getInstance().translate(gson.toJson(identifiers), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "date"})
    @RequestMapping(value = "/stirling/v3/classes/get/daily", method = RequestMethod.GET)
    public String getDailyClasses(@RequestParam("accountName") String accountName,
                                  @RequestParam("password") String password,
                                  @RequestParam(value = "date", required = false) String rawDate) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        boolean useDate = false;

        if (rawDate != null) {
            useDate = true;
        }

        StirlingDate date;
        if (useDate) {
            date = StirlingDate.parse(rawDate);
        } else {
            date = StirlingDate.getNow();
        }

        return LocalisationManager.getInstance().translate(gson.toJson(classManager.getDailyClasses(account, date)), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/delete", method = RequestMethod.GET)
    public String deleteClass(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        return classManager.deleteClass(account, uuid);
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/name", method = RequestMethod.GET)
    public String getName(@RequestParam("accountName") String accountName,
                          @RequestParam("password") String password,
                          @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        return LocalisationManager.getInstance().translate(stirlingClass.getName(), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/desc", method = RequestMethod.GET)
    public String getDesc(@RequestParam("accountName") String accountName,
                          @RequestParam("password") String password,
                          @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        return LocalisationManager.getInstance().translate(stirlingClass.getRoom(), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/room", method = RequestMethod.GET)
    public String getRoom(@RequestParam("accountName") String accountName,
                          @RequestParam("password") String password,
                          @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        return stirlingClass.getRoom();
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/banner", method = RequestMethod.GET)
    public String getBanner(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            @RequestParam("classUuid") String rawUuid,
                            HttpServletResponse response) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        File file = stirlingClass.getClassBanner().getFile();
        try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(file.toPath()));

            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "fetching the class banner"));
        }
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/notes", method = RequestMethod.GET)
    public String getClassNotes(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password,
                                @RequestParam("classUuid") String classUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(classUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "parsing the class uuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid));
        }

        if (stirlingClass.getStudents().contains(account.getUuid()) ||
          stirlingClass.getMembers().containsKey(account.getUuid())) {
            List<StirlingPostable> classNotes = Lists.newArrayList();

            try {
                classNotes.addAll(stirlingClass.getClassNotes());
            } catch (NullPointerException ignored) {
            }

            Map<UUID, List<String>> importHolders = Maps.newHashMap();
            try {
                importHolders.putAll(stirlingClass.getStudentImportHolders());
            } catch (NullPointerException ignored) {
            }

            if (importHolders.containsKey(account.getUuid())) {
                List<String> holders = stirlingClass.getStudentImportHolders().get(account.getUuid());
                ImportAccount acc = importManager.getByUuid(account.getUuid());

                holders.forEach(holder -> {
                    acc.getMoodleClasses().forEach(c -> {
                        if (c.getId().equals(holder)) {
                            classNotes.addAll(c.getPostables());
                        }
                    });

                    acc.getGoogleClasses().forEach(c -> {
                        if (c.getId().equals(holder)) {
                            classNotes.addAll(c.getPostables());
                        }
                    });
                });
            }

            return LocalisationManager.getInstance().translate(gson.toJson(classNotes), account.getLocale());
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/homework", method = RequestMethod.GET)
    public String getHomework(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "parsing the class uuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        if (stirlingClass.getStudents().contains(account.getUuid()) ||
          stirlingClass.getMembers().containsKey(account.getUuid())) {
            List<StirlingPostable> homework = Lists.newArrayList();

            try {
                homework.addAll(stirlingClass.getHomework());
            } catch (NullPointerException ignored) {
            }

            return LocalisationManager.getInstance().translate(gson.toJson(homework), account.getLocale());
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/resources", method = RequestMethod.GET)
    public String getResources(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "parsing the class uuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        if (stirlingClass.getStudents().contains(account.getUuid()) ||
          stirlingClass.getMembers().containsKey(account.getUuid())) {
            List<AttachableResource> resources = Lists.newArrayList();

            try {
                resources.addAll(stirlingClass.getResources());
            } catch (NullPointerException ignored) {
            }

            Map<UUID, List<String>> importHolders = Maps.newHashMap();
            try {
                importHolders.putAll(stirlingClass.getStudentImportHolders());
            } catch (NullPointerException ignored) {
            }

            if (importHolders.containsKey(account.getUuid())) {
                List<String> holders = stirlingClass.getStudentImportHolders().get(account.getUuid());
                ImportAccount acc = importManager.getByUuid(account.getUuid());

                holders.forEach(holder -> {
                    acc.getMoodleClasses().forEach(c -> {
                        if (c.getId().equals(holder)) {
                            resources.addAll(c.getResources());
                        }
                    });

                    acc.getGoogleClasses().forEach(c -> {
                        if (c.getId().equals(holder)) {
                            resources.addAll(c.getResources());
                        }
                    });
                });
            }

            return LocalisationManager.getInstance().translate(gson.toJson(resources), account.getLocale());
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.STUDENT_NOT_IN_CLASS, account.getLocale()));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/download/resource", method = RequestMethod.GET)
    public String downloadResource(@RequestParam("accountName") String accountName,
                                   @RequestParam("password") String password,
                                   @RequestParam("classUuid") String rawUuid,
                                   @RequestParam("resourceUuid") String resUuid,
                                   HttpServletResponse response) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        UUID resourceUuid;
        try {
            resourceUuid = UUID.fromString(resUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), resUuid, "resourceUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        CompletableFuture<AttachableResource> resource = new CompletableFuture<>();
        try {
            for (AttachableResource r : stirlingClass.getResources()) {
                if (r.getResUuid().equals(resourceUuid)) {
                    resource.complete(r);
                }
            }
        } catch (NullPointerException ignored) {
        }

        if (resource.getNow(null) != null) {
            File file = resource.getNow(null).getFile();

            try {
                InputStream in = new FileInputStream(file);
                response.setContentType(Files.probeContentType(file.toPath()));

                IOUtils.copy(in, response.getOutputStream());
                response.flushBuffer();
                return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
            } catch (IOException e) {
                return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "downloading the file"));
            }
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.CLOUD_FILE_DOES_NOT_EXIST, account.getLocale(), resUuid));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/sections", method = RequestMethod.GET)
    public String getSections(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        List<StirlingSection> sections = Lists.newArrayList();
        try {
            sections.addAll(stirlingClass.getSections());
        } catch (NullPointerException ignored) {
        }

        return LocalisationManager.getInstance().translate(gson.toJson(sections), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/catchups", method = RequestMethod.GET)
    public String getCatchups(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        return LocalisationManager.getInstance().translate(gson.toJson(stirlingClass.getCatchups()), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/students", method = RequestMethod.GET)
    public String getStudents(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        List<UUID> students = Lists.newArrayList();
        try {
            students.addAll(stirlingClass.getStudents());
        } catch (NullPointerException ignored) {
        }

        List<String> studentNames = Lists.newArrayList();
        students.forEach(u -> studentNames.add(AccountManager.getInstance().getAccount(u).getDisplayName()));

        return LocalisationManager.getInstance().translate(gson.toJson(studentNames), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/teachers", method = RequestMethod.GET)
    public String getTeachers(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        StirlingClass stirlingClass = classManager.getByUuid(uuid);

        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        List<UUID> teachers = Lists.newArrayList();
        try {
            teachers.addAll(stirlingClass.getTeachers());
        } catch (NullPointerException ignored) {
        }

        List<String> teacherNames = Lists.newArrayList();
        teachers.forEach(u -> teacherNames.add(AccountManager.getInstance().getAccount(u).getDisplayName()));

        return LocalisationManager.getInstance().translate(gson.toJson(teacherNames), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/assignments", method = RequestMethod.GET)
    public String getAssignments(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("classUuid") String rawUuid,
                                 @RequestParam(value = "studentUuid", required = false) String studentId) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        AssignmentAccount assignmentAccount = AssignmentManager.getInstance().getByUuid(account.getUuid());
        List<StirlingAssignment> assignments = Lists.newArrayList();

        try {
            assignments.addAll(assignmentAccount.getAssignments());
        } catch (NullPointerException ignored) {
        }

        List<StirlingAssignment> finalAsses = Lists.newArrayList();
        assignments.forEach(a -> {
            if (a.getClassUuid().equals(uuid)) {
                finalAsses.add(a);
            }
        });

        return LocalisationManager.getInstance().translate(gson.toJson(finalAsses), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuid"})
    @RequestMapping(value = "/stirling/v3/classes/get/progressMarkers", method = RequestMethod.GET)
    public String getProgressMarkers(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("classUuid") String rawUuid,
                                     @RequestParam(value = "studentUuid", required = false) String studentId) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        ProgressAccount progressAccount = MarkerManager.getInstance().getByUuid(account.getUuid());
        List<ProgressMarker> progressMarkers = Lists.newArrayList();

        try {
            progressMarkers.addAll(progressAccount.getProgressMarkers());
        } catch (NullPointerException ignored) {
        }

        List<ProgressMarker> finalMarkers = Lists.newArrayList();
        progressMarkers.forEach(m -> {
            if (m.getClassUuid().equals(uuid)) {
                finalMarkers.add(m);
            }
        });

        return LocalisationManager.getInstance().translate(gson.toJson(finalMarkers), account.getLocale());
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "name"})
    @RequestMapping(value = "/stirling/v3/classes/update/name", method = RequestMethod.GET)
    public String updateName(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("classUuid") String rawUuid,
                             @RequestParam("name") String name) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        return ClassManager.getInstance().setName(account, uuid, name);
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "desc"})
    @RequestMapping(value = "/stirling/v3/classes/update/desc", method = RequestMethod.GET)
    public String updateDesc(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("classUuid") String rawUuid,
                             @RequestParam("desc") String desc) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        return ClassManager.getInstance().setDesc(account, uuid, desc);
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "room"})
    @RequestMapping(value = "/stirling/v3/classes/update/room", method = RequestMethod.GET)
    public String updateRoom(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("classUuid") String rawUuid,
                             @RequestParam("room") String room) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        return ClassManager.getInstance().setRoom(account, uuid, room);
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuids"})
    @RequestMapping(value = "/stirling/v3/classes/add/students", method = RequestMethod.GET)
    public String addStudents(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid,
                              @RequestParam("studentUuids") String rawUuids) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        List<UUID> uuids = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(rawUuids, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                uuids.add(UUID.fromString(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ClassManager.getInstance().addStudents(account, uuid, uuids.toArray(new UUID[uuids.size()]));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "teacherUuids"})
    @RequestMapping(value = "/stirling/v3/classes/add/teachers", method = RequestMethod.GET)
    public String addTeachers(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("classUuid") String rawUuid,
                              @RequestParam("teacherUuids") String rawUuids) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        List<UUID> uuids = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(rawUuids, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                uuids.add(UUID.fromString(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ClassManager.getInstance().addTeachers(account, uuid, uuids.toArray(new UUID[uuids.size()]));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "studentUuids"})
    @RequestMapping(value = "/stirling/v3/classes/remove/students", method = RequestMethod.GET)
    public String removeStudents(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("classUuid") String rawUuid,
                                 @RequestParam("studentUuids") String rawUuids) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        List<UUID> uuids = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(rawUuids, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                uuids.add(UUID.fromString(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ClassManager.getInstance().removeStudents(account, uuid, uuids.toArray(new UUID[uuids.size()]));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "teacherUuids"})
    @RequestMapping(value = "/stirling/v3/classes/remove/teachers", method = RequestMethod.GET)
    public String removeTeachers(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("classUuid") String rawUuid,
                                 @RequestParam("teacherUuids") String rawUuids) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "classUuid"));
        }

        List<UUID> uuids = Lists.newArrayList();
        StringTokenizer tokenizer = new StringTokenizer(rawUuids, ",");
        while (tokenizer.hasMoreElements()) {
            try {
                uuids.add(UUID.fromString(tokenizer.nextElement().toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ClassManager.getInstance().removeTeachers(account, uuid, uuids.toArray(new UUID[uuids.size()]));
    }

    @CallableAPI(fields = {"accountName", "password", "classUuid", "banner"})
    @RequestMapping(value = "/stirling/v3/classes/update/banner", method = RequestMethod.POST)
    public String updateBanner(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("classUuid") String rawUuid,
                               @RequestParam("banner") MultipartFile file) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "uuid"));
        }

        if (account.getAccountType().getAccessLevel() < AccountType.TEACHER.getAccessLevel()) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "update the class banner", "TEACHER"));
        }

        String ext;
        if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".jpeg")) {
            ext = ".jpg";
        } else if (file.getOriginalFilename().endsWith(".png")) {
            ext = ".png";
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INVALID_TYPE_FORMAT, account.getLocale(), file.getOriginalFilename(), ".jpg or .png"));
        }

        StirlingClass stirlingClass = ClassManager.getInstance().getByUuid(uuid);
        if (stirlingClass == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), rawUuid));
        }

        File out = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Classes" + File.separator + uuid);
        File banner = new File(out + File.separator + "banner" + ext);
        try {
            if (!out.exists()) {
                out.mkdir();
            }

            if (banner.exists()) {
                banner.delete();
            }

            file.transferTo(banner);
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "uploading the new banner image"));
        }

        ClassManager.getInstance().updateField(stirlingClass, "classBanner", new AttachableResource(stirlingClass.getUuid(), "banner" + ext, ARType.CLASS));
        return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_BANNER_UPDATED, account.getLocale(), stirlingClass.getName()));
    }
}
