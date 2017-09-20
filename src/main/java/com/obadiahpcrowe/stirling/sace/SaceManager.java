package com.obadiahpcrowe.stirling.sace;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.SaceDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.SaceDAO;
import com.obadiahpcrowe.stirling.sace.obj.SaceCompletion;
import com.obadiahpcrowe.stirling.sace.obj.SaceResult;
import com.obadiahpcrowe.stirling.sace.obj.SaceUser;
import com.obadiahpcrowe.stirling.sace.scrapers.SaceScraper;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 6:26 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace
 * Copyright (c) Obadiah Crowe 2017
 */
public class SaceManager {

    private MorphiaService morphiaService;
    private SaceDAO saceDAO;
    private Gson gson = new Gson();

    public SaceManager() {
        this.morphiaService = new MorphiaService();
        this.saceDAO = new SaceDAOImpl(SaceUser.class, morphiaService.getDatastore());
    }

    public String setSaceCreds(StirlingAccount account, String saceId, String sacePassword) {
        SaceUser user = new SaceUser(account.getUuid(), saceId, sacePassword);
        if (isSaceUserPresent(account)) {
            saceDAO.delete(getSaceUser(account));
            saceDAO.save(user);
        } else {
            saceDAO.save(user);
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SACE_CREDS_SET, account.getLocale(), account.getDisplayName()));
    }

    public String getSaceResults(StirlingAccount account) {
        try {
            List<SaceResult> results = SaceScraper.getInstance().getResults(getSaceUser(account));
            updateResultsCache(account, results);
            return gson.toJson(results);
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "getting your SACE data"));
        }
    }

    public String getSaceCompletion(StirlingAccount account) {
        try {
            List<SaceCompletion> completions = SaceScraper.getInstance().getCompletion(getSaceUser(account));
            updateCompletionCache(account, completions);
            return gson.toJson(completions);
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "getting your SACE data"));
        }
    }

    private void updateResultsCache(StirlingAccount account, List<SaceResult> results) {
        // TODO: 13/9/17  
        return;
    }

    private void updateCompletionCache(StirlingAccount account, List<SaceCompletion> completions) {
        // TODO: 13/9/17
        return;
    }

    private boolean isSaceUserPresent(StirlingAccount account) {
        if (getSaceUser(account) == null) {
            return false;
        }
        return true;
    }

    public SaceUser getSaceUser(StirlingAccount account) {
        return saceDAO.getByUuid(account.getUuid());
    }

    public String getSaceId(StirlingAccount account) {
        if (isSaceUserPresent(account)) {
            SaceUser user = getSaceUser(account);
            return user.getSaceId();
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SACE_CREDS_NOT_FOUND, account.getLocale()));
    }
}
