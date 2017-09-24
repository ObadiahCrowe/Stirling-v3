package com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.obj;

import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.calendar.obj.CalendarEntry;
import com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.enums.TutorSpeciality;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.*;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:40 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutorsINACTIVE.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
@Entity("tutorsINACTIVE")
public class StirlingTutor {

    @Id
    private ObjectId id;

    private String displayName;
    private UUID uuid;
    private List<TutorSpeciality> specialities;
    private List<CalendarEntry> tutorAssignments;

    @Reference
    private Map<String, TutorRequest> tutorRequests;

    public StirlingTutor() {}

    public StirlingTutor(UUID uuid, List<TutorSpeciality> tutorSpecialities) {
        this.displayName = new AccountManager().getAccount(uuid).getDisplayName();
        this.uuid = uuid;
        this.specialities = tutorSpecialities;
        this.tutorAssignments = new ArrayList<>();
        this.tutorRequests = new HashMap<>();
    }
}
