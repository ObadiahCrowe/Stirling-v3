package com.obadiahpcrowe.stirling.session;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.redis.RedisCall;
import com.obadiahpcrowe.stirling.redis.RedisManager;
import com.obadiahpcrowe.stirling.redis.enums.RedisHeader;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private Map<String, UUID> sessioners;

    private SessionManager() {
        this.sessioners = Maps.newHashMap();
    }

    public String createSession(StirlingAccount account) {
        if (!sessionExists(account)) {
            StirlingSession session = new StirlingSession(account.getUuid());
            redisManager.makeCall(new RedisCall(redisManager.getJedis(), RedisHeader.SESSION)
              .insert(session.getUuid().toString(), gson.toJson(session)));
            sessioners.put(session.getAccessToken(), account.getUuid());

            return gson.toJson(new StirlingMsg(MsgTemplate.SESSION_CREATED, account.getLocale(), account.getDisplayName()));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SESSION_EXISTS, account.getLocale(), account.getDisplayName()));
    }

    public boolean sessionExists(StirlingAccount account) {
        try {
            StirlingSession session = gson.fromJson(redisManager.makeCall(new RedisCall(redisManager.getJedis(),
              RedisHeader.SESSION).get(account.getUuid().toString())), StirlingSession.class);

            if ((session.getInitTime() + TimeUnit.HOURS.toMillis(2)) < System.currentTimeMillis()) {
                redisManager.makeCall(new RedisCall(redisManager.getJedis(), RedisHeader.SESSION)
                  .delete(account.getUuid().toString()));
                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean sessionValid(String accessToken) {
        if (sessioners.containsKey(accessToken)) {
            UUID uuid = getUUIDFromSession(accessToken);
            StirlingSession session = gson.fromJson(redisManager.makeCall(new RedisCall(redisManager.getJedis(),
              RedisHeader.SESSION).get(uuid.toString())), StirlingSession.class);

            if (session.getInitTime() > System.currentTimeMillis()) {
                return true;
            } else {
                redisManager.makeCall(new RedisCall(redisManager.getJedis(), RedisHeader.SESSION)
                  .delete(uuid.toString()));
            }
        }
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
