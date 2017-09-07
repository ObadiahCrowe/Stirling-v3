package com.obadiahpcrowe.stirling.redis;

import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:45 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.redis
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class RedisManager {

    private static RedisManager instance;
    private String address = "localhost";
    private int port = 6379;
    private Jedis jedis = new Jedis(address, port);

    public String makeCall(RedisCall call) {
        String output = "";
        String identifier = call.getHeader().getHeaderName() + call.getIdentifier();
        switch (call.getJedisType()) {
            case GET:
                output = get(jedis, identifier);
                break;
            case DELETE:
                delete(jedis, identifier);
                break;
            case INSERT:
                insert(jedis, identifier, call.getValue());
                break;
            case REPLACE:
                replace(jedis, identifier, call.getValue());
                break;
        }
        return output;
    }

    private String get(Jedis jedis, String identifier) {
        return jedis.get(identifier);
    }

    private void delete(Jedis jedis, String identifier) {
        jedis.del(identifier);
    }

    private void insert(Jedis jedis, String identifier, String value) {
        jedis.set(identifier, value);
    }

    private void replace(Jedis jedis, String identifier, String value) {
        delete(jedis, identifier);
        insert(jedis, identifier, value);
    }

    public void subscribeToKey(String key, Method calledMethod) {
        //
    }

    public void callSub(String key) {
        //
    }

    public static RedisManager getInstance() {
        if (instance == null)
            instance = new RedisManager();
        return instance;
    }
}
