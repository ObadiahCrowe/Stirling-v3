package com.obadiahpcrowe.stirling.classes.obj;

import com.obadiahpcrowe.stirling.util.enums.AusState;
import lombok.Getter;

import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TermLength {

    private AusState state;
    private int year;
    private Map<Integer, Map<String, String>> terms;

    public TermLength(AusState state, int year, Map<Integer, Map<String, String>> terms) {
        this.state = state;
        this.year = year;
        this.terms = terms;
    }
}
