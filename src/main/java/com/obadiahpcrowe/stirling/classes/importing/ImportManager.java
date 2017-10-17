package com.obadiahpcrowe.stirling.classes.importing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapHandler;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.gclassroom.GClassroomHandler;
import com.obadiahpcrowe.stirling.classes.importing.moodle.MoodleHandler;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ImportDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.Arrays;
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

    // Add course to user methods, import for class (teacher only). Write methods and scrapers in each platform handler.

    private MorphiaService morphiaService;
    private ImportDAO importDAO;
    private Gson gson;

    public ImportManager() {
        this.morphiaService = new MorphiaService();
        this.importDAO = new ImportDAOImpl(ImportAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public static ImportManager getInstance() {
        if (instance == null)
            instance = new ImportManager();
        return instance;
    }

    public void performImportRefreshSingle(UUID accountUuid) {
        // TODO: 17/10/17 this
    }

    public void performImportRefreshAll() {
        // TODO: 17/10/17 Yeah, nah
    }

    public String addImportCredential(StirlingAccount account, ImportSource source, ImportCredential credential) {
        Map<ImportSource, ImportCredential> credentialMap;
        if (importAccExists(account.getUuid())) {
            credentialMap = Maps.newHashMap();
            credentialMap.put(source, credential);

            importDAO.save(new ImportAccount(account.getUuid(), credentialMap));
            return addImportCredential(account, source, credential);
        }

        // Add account then import
        ImportAccount importAccount = getByUuid(account.getUuid());
        if (importAccount.getCredentials().containsKey(source)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CONTAINS_CRED, account.getLocale(), source.getFriendlyName()));
        }

        credentialMap = Maps.newHashMap(importAccount.getCredentials());
        credentialMap.put(source, credential);

        importDAO.updateField(importAccount, "credentials", credentialMap);
        performImport(account);

        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
    }

    public void performImport(StirlingAccount account) {
        List<ImportableClass> daymapClasses = Lists.newArrayList();
        List<ImportableClass> moodleClasses = Lists.newArrayList();
        List<ImportableClass> googleClasses = Lists.newArrayList();

        List<Thread> threads = Lists.newArrayList();
        ImportAccount importAccount = getByUuid(account.getUuid());

        Thread daymap = new Thread(() -> {
            daymapClasses.addAll(DaymapHandler.getInstance().getAllCourses(importAccount));
        });
        daymap.start();

        Thread moodle = new Thread(() -> {
            moodleClasses.addAll(MoodleHandler.getInstance().getAllCourses(importAccount));
        });
        moodle.start();

        Thread google = new Thread(() -> {
            googleClasses.addAll(GClassroomHandler.getInstance().getAllCourses(importAccount));
        });
        google.start();

        threads.addAll(Arrays.asList(daymap, moodle, google));

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Map<ImportSource, List<ImportableClass>> classes = Maps.newHashMap();
        try {
            classes.putAll(importAccount.getCourseHolders());

            for (ImportSource source : ImportSource.values()) {
                List<ImportableClass> clazzes = Lists.newArrayList();
                clazzes.addAll(classes.get(source));

                switch (source) {
                    case DAYMAP:
                        for (ImportableClass c : daymapClasses) {
                            if (!clazzes.contains(c)) {
                                clazzes.add(c);
                            }
                        }
                        break;
                    case MOODLE:
                        for (ImportableClass c : moodleClasses) {
                            if (!clazzes.contains(c)) {
                                clazzes.add(c);
                            }
                        }
                        break;
                    case GOOGLE_CLASSROOM:
                        for (ImportableClass c : googleClasses) {
                            if (!clazzes.contains(c)) {
                                clazzes.add(c);
                            }
                        }
                        break;
                }

                classes.replace(source, clazzes);
            }
        } catch (NullPointerException ignored) {
        }

        importDAO.updateField(importAccount, "courseHolders", classes);
    }

    public String downloadCourse(StirlingAccount account, ImportSource source, String id) {
        return "";
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

    public List<ImportableClass> getAllCourses(StirlingAccount account, ImportSource source) {
        if (importAccExists(account.getUuid())) {
            ImportAccount acc = getByUuid(account.getUuid());
            List<ImportableClass> holders = Lists.newArrayList();
            holders.addAll(acc.getCourseHolders().get(source));

            return holders;
        }
        return null;
    }

    public ImportableClass getCourse(StirlingAccount account, ImportSource source, String id) {
        CompletableFuture<ImportableClass> future = new CompletableFuture<>();
        getAllCourses(account, source).forEach(course -> {
            if (course.getId().equalsIgnoreCase(id)) {
                future.complete(course);
            }
        });

        return future.getNow(null);
    }

    public String importExternalCourseForAccount(UUID uuid, UUID classUuid, ImportSource source, String id) {
        AccountManager accManager = AccountManager.getInstance();
        StirlingAccount account = null;

        try {
            account = accManager.getAccount(uuid);
        } catch (NullPointerException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, account.getUuid().toString()));
        }

        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, account.getUuid().toString()));
        }

        ClassManager classManager = ClassManager.getInstance();
        if (accManager.accountExists(account.getUuid())) {
            if (classManager.classExists(classUuid)) {
                ImportAccount acc = getByUuid(account.getUuid());
                StirlingClass clazz = classManager.getByUuid(classUuid);

                Map<ImportSource, List<ImportableClass>> currentClasses = Maps.newHashMap();
                try {
                    currentClasses.putAll(clazz.getStudentImportHolders().get(account.getUuid()));
                } catch (NullPointerException ignored) {
                }

                ImportableClass importableClass = getCourse(account, source, id);
                if (!currentClasses.get(source).contains(importableClass)) {
                    List<ImportableClass> classes = Lists.newArrayList();
                    try {
                        classes.addAll(currentClasses.get(source));
                    } catch (NullPointerException ignored) {
                    }

                    classes.add(importableClass);
                    currentClasses.replace(source, classes);
                }

                return classManager.updateStudentHolders(account, currentClasses);
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.CLASS_DOES_NOT_EXIST, account.getLocale(), classUuid.toString()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, account.getUuid().toString()));
    }

    public String importExternalCourseForClass(StirlingAccount account, UUID classUuid, ImportSource source, String id) {
        // TEACHER ONLY
        return "";
    }

    public void updateField(ImportAccount account, String field, Object value) {
        importDAO.updateField(account, field, value);
    }

    public ImportAccount getByUuid(UUID uuid) {
        return importDAO.getByUuid(uuid);
    }

    public boolean importAccExists(UUID uuid) {
        return importDAO.getByUuid(uuid) != null;
    }
}
