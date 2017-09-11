package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 4:58 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class AccountAPI implements APIController {

    private Gson gson = new Gson();

    @CallableAPI(fields = {"accountName", "emailAddress", "password"})
    @RequestMapping(value = "/stirling/v3/accounts/create", method = RequestMethod.GET)
    public String createAccount(@RequestParam("accountName") String accountName,
                                @RequestParam("emailAddress") String emailAddress,
                                @RequestParam("password") String password) {
        return AccountManager.getInstance().createAccount(accountName, emailAddress, password);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/accounts/delete", method = RequestMethod.GET)
    public String deleteAccount(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password) {
        return AccountManager.getInstance().deleteAccount(accountName, password);
    }

    @CallableAPI(fields = {"accountName", "password", "displayName"})
    @RequestMapping(value = "/stirling/v3/accounts/update/displayName", method = RequestMethod.GET)
    public String updateDisplayName(@RequestParam("accountName") String accountName,
                                    @RequestParam("password") String password,
                                    @RequestParam("displayName") String displayName) {
        StirlingAccount account = AccountManager.getInstance().getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!AccountManager.getInstance().validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        AccountManager.getInstance().updateField(account, "displayName", displayName);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "displayName", account.getAccountName()));
    }

    @CallableAPI(fields = {"accountName", "password", "emailAddress"})
    @RequestMapping(value = "/stirling/v3/accounts/update/emailAddress", method = RequestMethod.GET)
    public String updateEmailAddress(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("emailAddress") String emailAddress) {
        StirlingAccount account = AccountManager.getInstance().getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!AccountManager.getInstance().validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        AccountManager.getInstance().updateField(account, "emailAddress", emailAddress);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "emailAddress", account.getAccountName()));
    }

    @CallableAPI(fields = {"accountName", "password", "locale"})
    @RequestMapping(value = "/stirling/v3/accounts/update/locale", method = RequestMethod.GET)
    public String updateLocale(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("locale") String locale) {
        StirlingAccount account = AccountManager.getInstance().getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!AccountManager.getInstance().validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        AccountManager.getInstance().updateField(account, "locale", StirlingLocale.valueOf(locale));
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "locale", account.getAccountName()));
    }

    @CallableAPI(fields = {"accountName", "password", "newPassword"})
    @RequestMapping(value = "/stirling/v3/accounts/update/password", method = RequestMethod.GET)
    public String updatePassword(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("newPassword") String newPassword) {
        StirlingAccount account = AccountManager.getInstance().getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!AccountManager.getInstance().validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        String salt = BCrypt.gensalt();
        String ePassword = BCrypt.hashpw(newPassword, salt);

        AccountManager.getInstance().updateField(account, "salt", salt);
        AccountManager.getInstance().updateField(account, "password", ePassword);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "password", account.getAccountName()));
    }

    @CallableAPI(fields = {"accountName", "password", "avatar"})
    @RequestMapping(value = "/stirling/v3/accounts/update/avatar", method = RequestMethod.GET)
    public String updateAvatar() {
        // TODO: 11/9/17 this
        return "";
    }
}
