package com.obadiahpcrowe.stirling.sace.atar;

import lombok.Getter;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/11/17 at 1:29 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.atar
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AggregateGrade {

    A_PLUS("A+", 19),
    A("A", 18),
    A_MINUS("A-", 17),

    B_PLUS("B+", 16),
    B("B", 15),
    B_MINUS("B-", 14),

    C_PLUS("C+", 13),
    C("C", 12),
    C_MINUS("C-", 11),

    D_PLUS("D+", 10),
    D("D", 9),
    D_MINUS("D-", 8),

    E_PLUS("E+", 7),
    E("E", 6),
    E_MINUS("E-", 5);

    private String friendlyGrade;
    private int value;

    AggregateGrade(String friendlyGrade, int value) {
        this.friendlyGrade = friendlyGrade;
        this.value = value;
    }

    public static AggregateGrade getGradeFromValue(int value) {
        try {
            return Arrays.asList(AggregateGrade.values()).get(value);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static AggregateGrade getGradeFromText(String grade) {
        CompletableFuture<AggregateGrade> future = new CompletableFuture<>();
        Arrays.asList(AggregateGrade.values()).forEach(g -> {
            if (g.getFriendlyGrade().equalsIgnoreCase(grade)) {
                future.complete(g);
            }
        });

        return future.getNow(null);
    }
}
