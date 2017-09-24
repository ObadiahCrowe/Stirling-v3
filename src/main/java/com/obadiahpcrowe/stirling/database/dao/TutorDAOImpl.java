package com.obadiahpcrowe.stirling.database.dao;

import com.obadiahpcrowe.stirling.database.dao.interfaces.TutorDAO;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.enums.TutorSpeciality;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.obj.StirlingTutor;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 22/9/17 at 4:18 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao
 * Copyright (c) Obadiah Crowe 2017
 */
public class TutorDAOImpl extends BasicDAO<StirlingTutor, ObjectId> implements TutorDAO {

    public TutorDAOImpl(Class<StirlingTutor> tutorClass, Datastore datastore) {
        super(tutorClass, datastore);
    }

    @Override
    public StirlingTutor getByUuid(UUID uuid) {
        Query<StirlingTutor> query = createQuery()
          .field("uuid").equal(uuid);

        return query.get();
    }

    @Override
    public List<StirlingTutor> getBySpeciality(List<TutorSpeciality> specialities) {
        Query<StirlingTutor> query = createQuery()
          .field("specialities").equal(specialities);

        return query.asList();
    }
}
