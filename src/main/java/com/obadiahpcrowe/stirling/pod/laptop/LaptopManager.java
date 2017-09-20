package com.obadiahpcrowe.stirling.pod.laptop;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.LaptopDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.LaptopDAO;
import com.obadiahpcrowe.stirling.pod.laptop.obj.LaptopUser;
import com.obadiahpcrowe.stirling.pod.laptop.scrapers.ReimageScraper;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop
 * Copyright (c) Obadiah Crowe 2017
 */
public class LaptopManager {

    private MorphiaService morphiaService;
    private LaptopDAO laptopDAO;
    private Gson gson = new Gson();

    public LaptopManager() {
        this.morphiaService = new MorphiaService();
        this.laptopDAO = new LaptopDAOImpl(LaptopUser.class, morphiaService.getDatastore());
    }

    public String setLaptop(StirlingAccount account, String name) {
        if (name.startsWith("HTS") || name.startsWith("STL")) {
            return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_IS_HOTSWAP, account.getLocale()));
        }

        LaptopUser newUser = new LaptopUser(account.getUuid(), name);
        if (laptopUserExists(account)) {
            LaptopUser laptopUser = getLaptopUser(account);
            laptopDAO.delete(laptopUser);
            laptopDAO.save(newUser);
        } else {
            laptopDAO.save(newUser);
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_NAME_SET, account.getLocale(), name));
    }

    public String getLaptopStatus(StirlingAccount account) {
        LaptopUser user = getLaptopUser(account);

        if (user == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_NAME_NOT_FOUND, account.getLocale()));
        }

        try {
            return ReimageScraper.getInstance().getLaptopData(user.getLaptopName());
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "getting laptop status"));
        }
    }

    private boolean laptopUserExists(StirlingAccount account) {
        if (getLaptopUser(account) == null) {
            return false;
        }
        return true;
    }

    private LaptopUser getLaptopUser(StirlingAccount account) {
        return laptopDAO.getByUuid(account.getUuid());
    }
}
