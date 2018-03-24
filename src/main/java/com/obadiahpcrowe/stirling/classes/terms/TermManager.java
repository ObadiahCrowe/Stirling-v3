package com.obadiahpcrowe.stirling.classes.terms;

import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.util.UtilLog;
import com.obadiahpcrowe.stirling.util.enums.AusState;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 16/10/17 at 9:20 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.terms
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TermManager {

    private static TermManager instance;

    private List<TermLength> saTerms;

    public TermManager() {
        saTerms = Lists.newArrayList();
        initSA();
        // TODO: 16/10/17 Other states
    }

    public static TermManager getInstance() {
        if (instance == null)
            instance = new TermManager();
        return instance;
    }

    public int getCurrentTerm(List<TermLength> lengths) {
        LocalDate now = LocalDate.now();
        CompletableFuture<Integer> future = new CompletableFuture<>();

        lengths.forEach(l -> {
            if (now.compareTo(l.getStartDate()) >= 0 && now.compareTo(l.getEndDate()) <= 0) {
                future.complete(l.getTerm());
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            UtilLog.getInstance().log(e.getMessage());
            return 0;
        }
    }

    private void initSA() {
        saTerms.add(new TermLength(AusState.SA, 2018, 1, LocalDate.of(2018, 1, 30), LocalDate.of(2018, 4, 13)));
        saTerms.add(new TermLength(AusState.SA, 2018, 2, LocalDate.of(2018, 5, 1), LocalDate.of(2018, 7, 7)));
        saTerms.add(new TermLength(AusState.SA, 2018, 3, LocalDate.of(2018, 7, 24), LocalDate.of(2018, 9, 29)));
        saTerms.add(new TermLength(AusState.SA, 2018, 4, LocalDate.of(2018, 10, 16), LocalDate.of(2018, 12, 15)));
    }

    private void initVIC() {

    }
}
