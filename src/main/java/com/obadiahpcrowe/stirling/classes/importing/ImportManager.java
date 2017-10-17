package com.obadiahpcrowe.stirling.classes.importing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.ImportDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.ImportDAO;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

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
        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), source.getFriendlyName()));
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

    public String importExternalCourseForAccount(StirlingAccount account, UUID classUuid, ImportSource source, String id) {
        AccountManager accManager = AccountManager.getInstance();
        ClassManager classManager = ClassManager.getInstance();
        if (accManager.accountExists(account.getUuid())) {
            if (classManager.classExists(classUuid)) {
                ImportableClass clazz = getCourse(account, source, id);
                //
            }
        }
        return "";
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
