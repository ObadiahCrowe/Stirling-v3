package com.obadiahpcrowe.stirling.database.dao.interfaces;

import com.obadiahpcrowe.stirling.pod.tutors.enums.TutorSpeciality;
import com.obadiahpcrowe.stirling.pod.tutors.obj.StirlingTutor;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 2:50 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.database.dao.interfaces
 * Copyright (c) Obadiah Crowe 2017
 */
public interface TutorDAO {

    StirlingTutor getByUuid(UUID uuid);

    List<StirlingTutor> getBySpeciality(List<TutorSpeciality> specialities);
}
