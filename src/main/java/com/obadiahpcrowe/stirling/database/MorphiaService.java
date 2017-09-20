package com.obadiahpcrowe.stirling.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 12:26 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database
 * Copyright (c) Obadiah Crowe 2017
 */
public class MorphiaService {

    private final @Getter MongoClient mongoClient;
    private @Getter @Setter Morphia morphia;
    private @Getter @Setter Datastore datastore;

    public MorphiaService() {
        MongoClientOptions options = MongoClientOptions.builder()
          .codecRegistry(MongoClient.getDefaultCodecRegistry()).build();

        mongoClient = new MongoClient(new ServerAddress("localhost", 27017), options);
        this.morphia = new Morphia();
        this.datastore = morphia.createDatastore(mongoClient, "stirling");
    }
}
