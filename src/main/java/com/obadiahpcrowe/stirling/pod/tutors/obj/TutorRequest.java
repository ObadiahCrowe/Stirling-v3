package com.obadiahpcrowe.stirling.pod.tutors.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.pod.tutors.enums.TutorSpeciality;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 22/9/17 at 4:06 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutors.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class TutorRequest {

    @Id
    private ObjectId id;

    private UUID uuid;
    private String desc;
    private List<TutorSpeciality> subjects;

    public TutorRequest() {}

    public TutorRequest(StirlingAccount account, String desc, String date, String time, List<TutorSpeciality> subjects) {
        this.uuid = account.getUuid();
        this.desc = desc;
        this.subjects = subjects;
    }
}
