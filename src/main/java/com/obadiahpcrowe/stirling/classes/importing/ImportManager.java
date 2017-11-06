package com.obadiahpcrowe.stirling.classes.importing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.calendar.CalendarManager;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.enums.ClassLength;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapClass;
import com.obadiahpcrowe.stirling.classes.importing.daymap.DaymapScraper;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.gclassroom.GClassroomHandler;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ImportDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            ImportAccount acc = getByUuid(account.getUuid());

            if (credentialsExist(acc, ImportSource.DAYMAP)) {
                ImportCredential cred = getCreds(acc, ImportSource.DAYMAP);
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
        //
        return null;
    }

    public List<ImportableClass> getGoogleCourses(StirlingAccount account) {
        //
        return null;
    }

    public String addImportCredential(StirlingAccount account, ImportSource source, ImportCredential credential) {
        if (source == ImportSource.GOOGLE_CLASSROOM) {
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CANNOT_ADD, account.getLocale()));
        }

        Map<ImportSource, ImportCredential> credentialMap;
        if (!importAccExists(account.getUuid())) {
            credentialMap = Maps.newHashMap();
            credentialMap.put(source, credential);

            importDAO.save(new ImportAccount(account.getUuid(), credentialMap));
            if (areCredentialsValid(account, source)) {
                switch (source) {
                    case DAYMAP:
                        importAllDaymap(getByUuid(account.getUuid()), getDaymapCourses(account));
                        break;
                    case MOODLE:
                        importAllMoodle();
                        break;
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
        }

        // Add account then import
        ImportAccount importAccount = getByUuid(account.getUuid());
        credentialMap = Maps.newHashMap();
        try {
            credentialMap.putAll(importAccount.getCredentials());
        } catch (NullPointerException ignored) {
        }

        credentialMap.replace(source, credential);
        importDAO.updateField(importAccount, "credentials", credentialMap);

        if (areCredentialsValid(account, source)) {
            switch (source) {
                case DAYMAP:
                    importAllDaymap(importAccount, getDaymapCourses(account));
                    break;
                case MOODLE:
                    importAllMoodle();
                    break;
            }
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
    }

    public String addGoogleImportCode(StirlingAccount account, String authCode) {
        return GClassroomHandler.getInstance().addGoogleClassroomCreds(account, authCode);
    }

    private void importAllDaymap(ImportAccount account, List<ImportableClass> classes) {
        classes.forEach(c -> {
            Thread t = new Thread(() -> {
                importDaymapCourse(accountManager.getAccount(account.getAccountUuid()), c);
            });
            t.start();
        });
    }

    public void importDaymapCourse(StirlingAccount account, ImportableClass clazz) {
        ImportAccount acc = getByUuid(account.getUuid());
        ImportCredential cred = getCreds(acc, ImportSource.DAYMAP);
        DaymapClass daymapClass = DaymapScraper.getInstance().getFullCourse(cred.getUsername(), cred.getPassword(),
          clazz, false);

        StirlingClass stirlingClass = classManager.getByOwner(daymapClass.getId());
        classManager.addClassToAccount(account, stirlingClass.getUuid());
    }

    private void importAllMoodle() {
        //
    }

    private void importAllGoogle() {
        //
    }

    public ImportCredential getCreds(ImportAccount account, ImportSource source) {
        if (credentialsExist(account, source)) {
            return account.getCredentials().get(source);
        }
        return null;
    }

    public boolean areCredentialsValid(StirlingAccount account, ImportSource source) {
        ImportAccount acc = getByUuid(account.getUuid());
        switch (source) {
            case DAYMAP:
                return DaymapScraper.getInstance().areCredentialsValid(acc.getCredentials().get(source));
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
