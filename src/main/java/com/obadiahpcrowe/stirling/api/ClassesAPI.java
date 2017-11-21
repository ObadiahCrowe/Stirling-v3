package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.classes.ClassIdentifier;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
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

    // TODO: 26/10/17 Create edit api calls
    // TODO: 26/10/17 Resources in sections

    private AccountManager accountManager = AccountManager.getInstance();
    private ClassManager classManager = ClassManager.getInstance();
    private final String CALL_DISABLED = "This API call is undergoing rigorous testing before becoming a beta API. Please wait a while.";
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
    @RequestMapping(value = "/stirling/v3/classes/get/title", method = RequestMethod.GET)
    public String getTitle(@RequestParam("accountName") String accountName,
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

        return stirlingClass.getName();
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

        return stirlingClass.getRoom();
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

        return LocalisationManager.getInstance().translate(gson.toJson(stirlingClass.getSections()), account.getLocale());
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

        return LocalisationManager.getInstance().translate(gson.toJson(stirlingClass.getStudents()), account.getLocale());
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

        return LocalisationManager.getInstance().translate(gson.toJson(stirlingClass.getTeachers()), account.getLocale());
    }
}
