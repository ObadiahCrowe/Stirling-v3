package com.obadiahpcrowe.stirling.pod.signin.obj;

import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 10:59 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class PodUser {

    private UUID uuid;
    private int studentId;
    private @Setter boolean signedIn;
    private String assigningTeacher;
    private PodLine line;
    private PodReason reason;

    public PodUser(UUID uuid, int studentId, boolean signedIn) {
        this.uuid = uuid;
        this.studentId = studentId;
        this.signedIn = signedIn;
    }

    public PodUser setSignInOptions(PodLine line, String assigningTeacher, PodReason reason) {
        if (signedIn) {
            this.line = line;
            this.reason = reason;
            this.assigningTeacher = assigningTeacher;
        } else {
            this.signedIn = true;
            return setSignInOptions(line, assigningTeacher, reason);
        }
        return this;
    }
}
