package com.obadiahpcrowe.stirling.signin;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.modules.events.EventManager;
import com.obadiahpcrowe.stirling.modules.events.types.SchoolSignInEvent;
import com.obadiahpcrowe.stirling.modules.events.types.SchoolSignOutEvent;
import com.obadiahpcrowe.stirling.signin.enums.SignInReason;
import com.obadiahpcrowe.stirling.signin.enums.SignOutReason;
import com.obadiahpcrowe.stirling.signin.obj.PresentUser;
import com.obadiahpcrowe.stirling.util.UtilTime;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.HashMap;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 5:53 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class SignInManager {

    private static SignInManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String signIn(StirlingAccount account, SignInReason reason, String extraInfo) {
        PresentUser user = new PresentUser(account.getUuid()).signIn(new HashMap<SignInReason, String>() {{
            put(reason, extraInfo);
        }});
        StirlingMsg msg = new StirlingMsg(MsgTemplate.SCHOOL_SIGN_IN, account.getLocale(),
          reason.getFriendlyName() + ": " + extraInfo);
        if (!isSignedIn(account)) {
            databaseManager.makeCall(new StirlingCall(databaseManager.getSignInDB()).insert(user));

            EventManager.getInstance().fireEvent(new SchoolSignInEvent(msg, account.getUuid(), reason, extraInfo));

            return gson.toJson(msg);
        } else {
            databaseManager.makeCall(new StirlingCall(databaseManager.getSignInDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, user));

            EventManager.getInstance().fireEvent(new SchoolSignInEvent(msg, account.getUuid(), reason, extraInfo));

            return gson.toJson(msg);
        }
    }

    public String signOut(StirlingAccount account, SignOutReason reason, String extraInfo) {
        if (isSignedIn(account)) {
            PresentUser presentUser = getPresentUser(account);
            presentUser.setPresent(false);
            presentUser.setSignOutReason(new HashMap<SignOutReason, String>() {{
                put(reason, extraInfo);
            }});
            presentUser.setTimeSignedOut(UtilTime.getInstance().getFriendlyTime());

            databaseManager.makeCall(new StirlingCall(databaseManager.getSignInDB()).replace(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, presentUser));

            StirlingMsg output = new StirlingMsg(MsgTemplate.SCHOOL_SIGN_OUT, account.getLocale(),
              reason.getFriendlyName() + ": " + extraInfo);

            EventManager.getInstance().fireEvent(new SchoolSignOutEvent(output, account.getUuid(), reason, extraInfo));

            return gson.toJson(output);
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SCHOOL_NOT_SIGNED_IN, account.getLocale()));
    }

    public PresentUser getPresentUser(StirlingAccount account) {
        try {
            return (PresentUser) databaseManager.makeCall(new StirlingCall(databaseManager.getSignInDB()).get(
              new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, PresentUser.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean isSignedIn(StirlingAccount account) {
        try {
            PresentUser presentUser = (PresentUser) databaseManager.makeCall(new StirlingCall(databaseManager.getSignInDB())
              .get(new HashMap<String, Object>() {{
                put("uuid", account.getUuid());
            }}, PresentUser.class));

            if (presentUser != null) {
                if (presentUser.isPresent()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static SignInManager getInstance() {
        if (instance == null)
            instance = new SignInManager();
        return instance;
    }
}
