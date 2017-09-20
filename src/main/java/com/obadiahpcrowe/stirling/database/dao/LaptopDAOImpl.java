package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.LaptopDAO;
import com.obadiahpcrowe.stirling.pod.laptop.obj.LaptopUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:52 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class LaptopDAOImpl extends BasicDAO<LaptopUser, ObjectId> implements LaptopDAO {

    public LaptopDAOImpl(Class<LaptopUser> laptopUserClass, Datastore datastore) {
        super(laptopUserClass, datastore);
    }

    @Override
    public LaptopUser getByUuid(UUID uuid) {
        Query<LaptopUser> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<LaptopUser> getByLaptopName(String laptopName) {
        Query<LaptopUser> query = createQuery()
          .field("laptopName").equal(laptopName);

        return query.asList();
    }
}
