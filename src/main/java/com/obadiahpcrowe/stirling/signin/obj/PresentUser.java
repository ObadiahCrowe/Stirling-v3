package com.obadiahpcrowe.stirling.signin.obj;

import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.signin.enums.SignOutReason;
import com.obadiahpcrowe.stirling.util.UtilTime;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 5:59 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.signin.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
public class PresentUser {

    private UUID uuid;
    private boolean present;
    private Map<SignInReason, String> signInReason;
    private Map<SignOutReason, String> signOutReason;
    private String timeSignedIn;
    private String timeSignedOut;

    public PresentUser(UUID uuid) {
        this.uuid = uuid;
    }

    public PresentUser signIn(Map<SignInReason, String> signInReason) {
        this.present = true;
        this.signInReason = signInReason;
        this.timeSignedIn = UtilTime.getInstance().getFriendlyTime();
        return this;
    }

    public PresentUser signOut(Map<SignOutReason, String> signOutReason) {
        this.present = false;
        this.signOutReason = signOutReason;
        this.timeSignedOut = UtilTime.getInstance().getFriendlyTime();
        return this;
    }
}
