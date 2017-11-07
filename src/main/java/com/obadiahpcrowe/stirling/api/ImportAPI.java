package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/11/17 at 6:36 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class ImportAPI implements APIController {

    private Gson gson = new Gson();
    private AccountManager accountManager = AccountManager.getInstance();
    private ImportManager importManager = ImportManager.getInstance();

    @CallableAPI(fields = {"accountName", "password", "credUsername", "credPassword", "authCode", "credType"})
    @RequestMapping(value = "/stirling/v3/import/addCredentials", method = RequestMethod.GET)
    public String addImportCredentials(@RequestParam(value = "accountName") String accountName,
                                       @RequestParam(value = "password") String password,
                                       @RequestParam(value = "credUsername", required = false) String credUsername,
                                       @RequestParam(value = "credPassword", required = false) String credPassword,
                                       @RequestParam(value = "authCode", required = false) String authCode,
                                       @RequestParam(value = "credType") String rawCredType) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        ImportSource source;
        try {
            source = ImportSource.valueOf(rawCredType);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawCredType, "credType"));
        }

        boolean useCreds = false;
        if (authCode == null) {
            useCreds = true;
        }

        if (useCreds) {
            if (source == ImportSource.DAYMAP) {
                return importManager.addImportCredential(account, ImportSource.DAYMAP, new ImportCredential(credUsername, credPassword.toCharArray()));
            } else if (source == ImportSource.MOODLE) {
                return importManager.addImportCredential(account, ImportSource.MOODLE, new ImportCredential(credUsername, credPassword.toCharArray()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawCredType, "credType"));
            }
        } else {
            return importManager.addGoogleImportCode(account, authCode);
        }
    }

    @CallableAPI(fields = {"accountName", "password", "credType"})
    @RequestMapping(value = "/stirling/v3/import/isValid", method = RequestMethod.GET)
    public String areCredentialsValid(@RequestParam("accountName") String accountName,
                                      @RequestParam("password") String password,
                                      @RequestParam("credType") String rawCredType) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        ImportSource source;
        try {
            source = ImportSource.valueOf(rawCredType);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawCredType, "credType"));
        }

        return gson.toJson(importManager.areCredentialsValid(account, source));
    }

    //@RequestMapping(value = "/stirling/v3/")
}
