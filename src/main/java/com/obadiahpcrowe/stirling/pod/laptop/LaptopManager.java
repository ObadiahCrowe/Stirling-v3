package com.obadiahpcrowe.stirling.pod.laptop;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.pod.laptop.obj.LaptopUser;
import com.obadiahpcrowe.stirling.pod.laptop.scrapers.ReimageScraper;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop
 * Copyright (c) Obadiah Crowe 2017
 */
public class LaptopManager {

    private static LaptopManager instance;
    private MorphiaService morphiaService = MorphiaService.getInstance();
    private Gson gson = new Gson();

    public String setLaptop(StirlingAccount account, String name) {
        boolean userExists = true;
        try {
            morphiaService.makeCall(new StirlingCall(morphiaService.getLaptopDB()).get(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, LaptopUser.class));
        } catch (NullPointerException e) {
            userExists = false;
        }

        if (name.startsWith("HTS") || name.startsWith("STL")) {
            return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_IS_HOTSWAP, account.getLocale()));
        }

        if (userExists) {
            morphiaService.makeCall(new StirlingCall(morphiaService.getLaptopDB()).replaceField(new HashMap<String, Object>() {{
                put("uuid", account.getUuid().toString());
            }}, name, "laptopName"));
        } else {
            morphiaService.makeCall(new StirlingCall(morphiaService.getLaptopDB()).insert(new LaptopUser(account.getUuid(), name)));
        }

        return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_NAME_SET, account.getLocale(), name));
    }

    public String getLaptopStatus(StirlingAccount account) {
        LaptopUser user = null;
        try {
            user = (LaptopUser) morphiaService.makeCall(new StirlingCall(morphiaService.getLaptopDB())
              .get(new HashMap<String, Object>() {{
                  put("uuid", account.getUuid().toString());
              }}, LaptopUser.class));
        } catch (NullPointerException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.LAPTOP_NAME_NOT_FOUND, account.getLocale()));
        }

        try {
            return ReimageScraper.getInstance().getLaptopData(user.getLaptopName());
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "getting laptop status"));
        }
    }

    public static LaptopManager getInstance() {
        if (instance == null)
            instance = new LaptopManager();
        return instance;
    }
}
