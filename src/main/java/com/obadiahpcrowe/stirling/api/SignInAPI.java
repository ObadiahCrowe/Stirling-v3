package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.signin.SignInManager;
import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.signin.enums.SignOutReason;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:09 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class SignInAPI implements APIController {

    private AccountManager accountManager = AccountManager.getInstance();
    private SignInManager signInManager = SignInManager.getInstance();
    private Gson gson = new Gson();

    @CallableAPI(fields = {"accountName", "password", "reason", "extraInfo"})
    @RequestMapping(value = "/stirling/v3/signin", method = RequestMethod.GET)
    public String signIn(@RequestParam("accountName") String accountName,
                         @RequestParam("password") String password,
                         @RequestParam("reason") String rawReason,
                         @RequestParam("extraInfo") String extraInfo) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        SignInReason reason;
        try {
            reason = SignInReason.valueOf(rawReason.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawReason, "reason"));
        }

        return signInManager.signIn(account, reason, extraInfo);
    }

    @CallableAPI(fields = {"accountName", "password", "reason", "extraInfo"})
    @RequestMapping(value = "/stirling/v3/signout", method = RequestMethod.GET)
    public String signOut(@RequestParam("accountName") String accountName,
                          @RequestParam("password") String password,
                          @RequestParam("reason") String rawReason,
                          @RequestParam("extraInfo") String extraInfo) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        SignOutReason reason;
        try {
            reason = SignOutReason.valueOf(rawReason.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawReason, "reason"));
        }

        return signInManager.signOut(account, reason, extraInfo);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/isSignedIn", method = RequestMethod.GET)
    public String isSignedIn(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return gson.toJson(signInManager.isSignedIn(account));
    }
}
