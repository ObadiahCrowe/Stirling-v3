package com.obadiahpcrowe.stirling.pod.tutor.obj;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 3:53 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.tutor.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TutorAssignment {

    private UUID assignee;
    private UUID tutor;
    private UUID uuid;
    private String date;
    private String time;

    public TutorAssignment(UUID assignee, UUID tutor, String date, String time) {
        this.assignee = assignee;
        this.tutor = tutor;
        this.uuid = UUID.randomUUID();
        this.date = date;
        this.time = time;
    }
}
