package com.obadiahpcrowe.stirling.api.debug;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.Stirling;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.enums.VersionType;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 24/9/17 at 9:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class DebugAPI implements APIController {

    private Gson gson = new Gson();

    @CallableAPI(fields = { "accountName", "accountType" })
    @RequestMapping(value = "/stirling/v3/dundee/setAccType", method = RequestMethod.GET)
    public String setAccType(@RequestParam("accountName") String accountName,
                             @RequestParam("accountType") String accountType) {
        if (Stirling.getInstance().getVersion().getType() == VersionType.DEVELOPMENT_BUILD) {
            AccountManager manager = new AccountManager();
            if (manager.accountExists(accountName)) {
                StirlingAccount account = manager.getAccount(accountName);
                try {
                    AccountType type = AccountType.valueOf(accountType);
                    manager.updateField(account, "accountType", type);
                    return gson.toJson(new StirlingMsg(MsgTemplate.SET_FIELD_TO_FIELD, account.getLocale(), "accountType", type.toString()));
                } catch (IllegalArgumentException e) {
                    return gson.toJson(AccountType.values());
                }
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }
        return gson.toJson("This API is only available on development builds.");
    }
}
