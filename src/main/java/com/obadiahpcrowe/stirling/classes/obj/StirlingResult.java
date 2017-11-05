package com.obadiahpcrowe.stirling.classes.obj;

import lombok.Getter;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 26/9/17 at 9:24 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingResult {

    private int receivedMarks;
    private int maxMarks;
    private String grade;
    private double weighting;
    private String comments;

    @Deprecated
    public StirlingResult() {}

    public StirlingResult(int receivedMarks, int maxMarks, String grade, double weighting, String comments) {
        this.receivedMarks = receivedMarks;
        this.maxMarks = maxMarks;
        this.grade = grade;
        this.weighting = weighting;
        this.comments = comments;
    }
}
