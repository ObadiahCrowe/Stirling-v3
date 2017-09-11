package com.obadiahpcrowe.stirling.localisation.translation;

import com.obadiahpcrowe.stirling.localisation.StirlingLocale;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/9/17 at 1:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation
 * Copyright (c) Obadiah Crowe 2017
 */
public class TranslateManager {

    private static TranslateManager instance;
    private final String GOOGLE_API = "http://translate.google.com/translate_a/";
    private final String GOOGLE_PARAMS = "single?client=z&sl=%s&tl=%s-CN&ie=UTF-8&oe=UTF-8&dt=t&dt=rm&q=%s";
    private final String PATTERN = "\"(.*?)\"";

    public String translate(String text, StirlingLocale input, StirlingLocale output) throws IOException {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String params = String.format(GOOGLE_PARAMS, input.getLocaleCode(), output.getLocaleCode(), encodedText);

        return "";
    }

    public static TranslateManager getInstance() {
        if (instance == null)
            instance = new TranslateManager();
        return instance;
    }
}
