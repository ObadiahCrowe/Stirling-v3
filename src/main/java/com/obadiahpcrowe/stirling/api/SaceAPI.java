package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.sace.SaceManager;
import com.obadiahpcrowe.stirling.sace.atar.AtarCalculator;
import com.obadiahpcrowe.stirling.sace.atar.Grade;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:08 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class SaceAPI implements APIController {

    private Gson gson = new Gson();
    private AccountManager accountManager = AccountManager.getInstance();
    private SaceManager saceManager = SaceManager.getInstance();

    @CallableAPI(fields = {"accountName", "password", "saceId", "sacePassword"})
    @RequestMapping(value = "/stirling/v3/sace/setLogin", method = RequestMethod.GET)
    public String setSaceLogin(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("saceId") String saceId,
                               @RequestParam("sacePassword") String sacePassword) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return saceManager.setSaceCreds(account, saceId, sacePassword);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/sace/get/id", method = RequestMethod.GET)
    public String getSaceId(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return saceManager.getSaceId(account);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/sace/get/results", method = RequestMethod.GET)
    public String getSaceResults(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return saceManager.getSaceResults(account);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/sace/get/completion", method = RequestMethod.GET)
    public String getSaceCompletion(@RequestParam("accountName") String accountName,
                                    @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return saceManager.getSaceCompletion(account);
    }

    @CallableAPI(fields = {"grades", "rpGrade"})
    @RequestMapping(value = "/stirling/v3/sace/get/aggregate", method = RequestMethod.GET)
    public String getSaceAggregate(String rawGrades, String rawGrade) {

        List<String> gradeList = Lists.newArrayList();
        if (rawGrades != null) {
            StringTokenizer tagTokenizer = new StringTokenizer(rawGrades, ",");
            while (tagTokenizer.hasMoreElements()) {
                gradeList.add(tagTokenizer.nextElement().toString().trim().replace(" ", ""));
            }
        }

        List<Grade> grades = Lists.newArrayList();
        gradeList.forEach(g -> {
            try {
                grades.add(Grade.getGradeFromText(g));
            } catch (IllegalArgumentException e) {
                return;
            }
        });

        Grade rpGrade;
        try {
            rpGrade = Grade.getGradeFromText(rawGrade);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, StirlingLocale.ENGLISH, rawGrade, "rpGrade"));
        }

        return gson.toJson(AtarCalculator.getInstance().calculateAtar(grades, rpGrade));
    }
}
