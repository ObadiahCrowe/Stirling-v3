package com.obadiahpcrowe.stirling.classes.terms;

import com.obadiahpcrowe.stirling.util.enums.AusState;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.terms
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TermLength {

    private AusState state;
    private int year;
    private int term;
    private LocalDate startDate;
    private LocalDate endDate;

    public TermLength(AusState state, int year, int term, LocalDate startDate, LocalDate endDate) {
        this.state = state;
        this.year = year;
        this.term = term;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
