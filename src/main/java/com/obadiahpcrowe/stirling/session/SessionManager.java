package com.obadiahpcrowe.stirling.session;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.redis.RedisManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 6:14 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.session
 * Copyright (c) Obadiah Crowe 2017
 */
public class SessionManager {

    private static SessionManager instance;
    private RedisManager redisManager = RedisManager.getInstance();
    private Gson gson = new Gson();
    private Map<String, UUID> sessioners = new HashMap<>();

    public String createSession(StirlingAccount account) {
        return "";
    }

    public boolean sessionExists(StirlingAccount account) {
        return false;
    }

    public boolean sessionValid(String accessToken) {
        return false;
    }

    public UUID getUUIDFromSession(String accessToken) {
        if (sessioners.containsKey(accessToken)) {
            return sessioners.get(accessToken);
        }
        return null;
    }

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }
}
