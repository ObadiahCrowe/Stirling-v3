package com.obadiahpcrowe.stirling.sace.atar;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/11/17 at 5:34 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.atar
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum Grade {

    A_PLUS("A+", 15),
    A("A", 14),
    A_MINUS("A-", 13),

    B_PLUS("B+", 12),
    B("B", 11),
    B_MINUS("B-", 10),

    C_PLUS("C+", 9),
    C("C", 8),
    C_MINUS("C-", 7),

    D_PLUS("D+", 6),
    D("D", 5),
    D_MINUS("D-", 4),

    E_PLUS("E+", 3),
    E("E", 2),
    E_MINUS("E-", 1);

    private String friendlyGrade;
    private int value;

    Grade(String friendlyGrade, int value) {
        this.friendlyGrade = friendlyGrade;
        this.value = value;
    }
}
