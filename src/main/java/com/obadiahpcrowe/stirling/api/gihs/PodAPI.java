package com.obadiahpcrowe.stirling.api.gihs;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.pod.laptop.LaptopManager;
import com.obadiahpcrowe.stirling.pod.signin.PodSignInManager;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:08 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class PodAPI implements APIController {

    private Gson gson = new Gson();
    private AccountManager accountManager = AccountManager.getInstance();
    private PodSignInManager podSignInManager = PodSignInManager.getInstance();
    private LaptopManager laptopManager = LaptopManager.getInstance();

    @CallableAPI(fields = {"accountName", "password", "studentId"})
    @RequestMapping(value = "/stirling/v3/pod/set/studentId", method = RequestMethod.GET)
    public String setStudentId(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("studentId") String studentId) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        Integer id;
        try {
            id = Integer.valueOf(studentId);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), "studentId", studentId));
        }

        return podSignInManager.setStudentId(account, id);
    }

    @CallableAPI(fields = {"accountName", "password", "podLine", "teacher", "reason"})
    @RequestMapping(value = "/stirling/v3/pod/signin", method = RequestMethod.GET)
    public String podSignIn(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            @RequestParam("podLine") String rawLine,
                            @RequestParam("teacher") String teacher,
                            @RequestParam("reason") String rawReason) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        PodLine line;
        try {
            line = PodLine.valueOf(rawLine.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), "podLine", rawLine));
        }

        PodReason reason;
        try {
            reason = PodReason.valueOf(rawReason.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), "reason", rawReason));
        }

        return podSignInManager.signInToPod(account, line, teacher, reason);
    }

    @CallableAPI(fields = {"accountName", "password", "laptopName"})
    @RequestMapping(value = "/stirling/v3/pod/set/laptopName", method = RequestMethod.GET)
    public String setLaptopName(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password,
                                @RequestParam("laptopName") String laptopName) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return laptopManager.setLaptop(account, laptopName);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/pod/laptopStatus", method = RequestMethod.GET)
    public String getLaptopStatus(@RequestParam("accountName") String accountName,
                                  @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return LocalisationManager.getInstance().translate(laptopManager.getLaptopStatus(account), account.getLocale());
    }
}
