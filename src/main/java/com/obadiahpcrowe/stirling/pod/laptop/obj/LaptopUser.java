package com.obadiahpcrowe.stirling.pod.laptop.obj;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:11 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Entity("laptops")
public class LaptopUser {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String laptopName;

    @Deprecated
    public LaptopUser() {}

    public LaptopUser(UUID uuid, String laptopName) {
        this.uuid = uuid;
        this.laptopName = laptopName;
    }
}
