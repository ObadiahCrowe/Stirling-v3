package com.obadiahpcrowe.stirling.localisation;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:13 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum StirlingLocale {

    ENGLISH("en"),
    FRENCH("fr"),
    SPANISH("es"),
    CHINESE("zh-tw"),
    KOREAN("ko"),
    JAPANESE("ja"),
    ITALIAN("it"),
    RUSSIAN("ru"),
    GREEK("el");

    private String localeCode;

    StirlingLocale(String localeCode) {
        this.localeCode = localeCode;
    }
}
