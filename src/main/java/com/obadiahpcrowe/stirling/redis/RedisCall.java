package com.obadiahpcrowe.stirling.redis;

import com.obadiahpcrowe.stirling.redis.enums.JedisType;
import com.obadiahpcrowe.stirling.redis.enums.RedisHeader;
import lombok.Getter;
import redis.clients.jedis.Jedis;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:57 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.redis
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class RedisCall {

    private Jedis jedis;
    private RedisHeader header;
    private JedisType jedisType;
    private String identifier;
    private String value;

    public RedisCall(Jedis jedis, RedisHeader header) {
        this.jedis = jedis;
        this.header = header;
    }

    public RedisCall insert(String identifier, String value) {
        this.jedisType = JedisType.INSERT;
        this.identifier = identifier;
        this.value = value;
        return this;
    }

    public RedisCall delete(String identifier) {
        this.jedisType = JedisType.DELETE;
        this.identifier = identifier;
        return this;
    }

    public RedisCall get(String identifier) {
        this.jedisType = JedisType.GET;
        this.identifier = identifier;
        return this;
    }

    public RedisCall replace(String identifier, String value) {
        this.jedisType = JedisType.REPLACE;
        this.identifier = identifier;
        this.value = value;
        return this;
    }

}
