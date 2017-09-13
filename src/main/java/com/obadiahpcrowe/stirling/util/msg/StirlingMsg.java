package com.obadiahpcrowe.stirling.util.msg;

import com.obadiahpcrowe.stirling.localisation.LocalisationManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:23 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingMsg {

    private MsgResponse response;
    private String message;

    public StirlingMsg(MsgTemplate template, StirlingLocale locale, String... args) {
        this.response = template.getResponse();

        String rawMessage = template.getMessage();
        for (int i = 0; i < template.getArgs(); i++) {
            rawMessage = rawMessage.replace("{" + i + "}", args[i]);
        }

        this.message = LocalisationManager.getInstance().translate(rawMessage, locale);
    }
}
