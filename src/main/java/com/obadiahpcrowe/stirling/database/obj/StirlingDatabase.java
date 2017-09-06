package com.obadiahpcrowe.stirling.database.obj;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;

import java.util.Arrays;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 5:04 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingDatabase {

    private DB db;
    private String collection;

    public StirlingDatabase(ServerAddress address, String dbName, String collection, MongoCredential credential) {
        MongoClient client;
        if (credential != null) {
            client = new MongoClient(address, Arrays.asList(credential));
        } else {
            client = new MongoClient(address);
        }
        this.db = client.getDB(dbName);
        this.collection = collection;
    }
}
