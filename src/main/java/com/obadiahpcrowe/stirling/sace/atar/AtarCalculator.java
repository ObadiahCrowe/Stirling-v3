package com.obadiahpcrowe.stirling.sace.atar;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 11/11/17 at 5:32 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.atar
 * Copyright (c) Obadiah Crowe 2017
 */
public class AtarCalculator {

    private static AtarCalculator instance;

    private AtarCalculator() {
        //
    }

    public static AtarCalculator getInstance() {
        if (instance == null)
            instance = new AtarCalculator();
        return instance;
    }

    public AtarResponse calculateAtar(List<Grade> grades, Grade rpGrade) {
        if (grades.size() > 4) {
            return null;
        }

        double rp = (double) AggregateGrade.getGradeFromText(rpGrade.getFriendlyGrade()).getValue() / 2;

        List<Grade> gradeList = Lists.newArrayList();
        List<Integer> gradeInts = Lists.newArrayList();

        gradeList.addAll(grades);

        if (gradeList.size() <= 4) {
            gradeList.forEach(g -> {
                gradeInts.add(AggregateGrade.getGradeFromText(g.getFriendlyGrade()).getValue());
            });
        }

        double finalGrade = 0;
        for (int i = 0; i < gradeInts.size(); i++) {
            finalGrade = finalGrade + gradeInts.get(i);
        }

        finalGrade = finalGrade + rp;

        final String responseMsg = "Calculate your ATAR from your aggregate here (remember to factor in bonus points): " +
          "http://www.satac.edu.au/documents/2016_sace-ntcet_uni_agg_atar_conversion_table.pdf";

        return new AtarResponse(0, finalGrade, responseMsg);
    }
}
