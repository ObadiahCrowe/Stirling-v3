package com.obadiahpcrowe.stirling.signin;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.SignInDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.SignInDAO;
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

    private MorphiaService morphiaService;
    private SignInDAO signInDAO;
    private Gson gson = new Gson();

    public SignInManager() {
        this.morphiaService = new MorphiaService();
        this.signInDAO = new SignInDAOImpl(PresentUser.class, morphiaService.getDatastore());
    }

    public String signIn(StirlingAccount account, SignInReason reason, String extraInfo) {
        PresentUser user = new PresentUser(account.getUuid()).signIn(new HashMap<SignInReason, String>() {{
            put(reason, extraInfo);
        }});
        StirlingMsg msg = new StirlingMsg(MsgTemplate.SCHOOL_SIGN_IN, account.getLocale(),
          reason.getFriendlyName() + ": " + extraInfo);

        if (getPresentUser(account) == null) {
            signInDAO.save(user);

            EventManager.getInstance().fireEvent(new SchoolSignInEvent(msg, account.getUuid(), reason, extraInfo));

            return gson.toJson(msg);
        } else {
            user.setUuid(getPresentUser(account).getUuid());
            signInDAO.delete(getPresentUser(account));
            signInDAO.save(user);

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

            signInDAO.delete(getPresentUser(account));
            signInDAO.save(presentUser);

            StirlingMsg output = new StirlingMsg(MsgTemplate.SCHOOL_SIGN_OUT, account.getLocale(),
              reason.getFriendlyName() + ": " + extraInfo);

            EventManager.getInstance().fireEvent(new SchoolSignOutEvent(output, account.getUuid(), reason, extraInfo));

            return gson.toJson(output);
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.SCHOOL_NOT_SIGNED_IN, account.getLocale()));
    }

    public PresentUser getPresentUser(StirlingAccount account) {
        return signInDAO.getByUuid(account.getUuid());
    }

    public boolean isSignedIn(StirlingAccount account) {
        PresentUser presentUser = getPresentUser(account);
        return presentUser.isPresent();
    }
}
