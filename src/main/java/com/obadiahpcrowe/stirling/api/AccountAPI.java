package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
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

    @CallableAPI(fields = {"accountName", "password", "field", "value"})
    @RequestMapping(value = "/stirling/v3/accounts/update", method = RequestMethod.GET)
    public String updateAccountField(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("field") String field,
                                     @RequestParam("value") String value) {
        StirlingAccount account = AccountManager.getInstance().getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!AccountManager.getInstance().validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        //AccountManager.getInstance().updateField(account, field);
        return "NOT FINISHED";
    }

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/accounts/getUpdateFields", method = RequestMethod.GET)
    public String getUpdateFields() {
        return "NOT FINISHED";
    }
}
