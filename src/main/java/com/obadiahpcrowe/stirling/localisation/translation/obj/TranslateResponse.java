package com.obadiahpcrowe.stirling.localisation.translation.obj;

import lombok.Getter;

import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/11/17 at 5:03 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.localisation.translation.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class TranslateResponse {

    private List<TranslateSentence> sentences;
    private String src;
    private int confidence;
    private IdResult id_result;

    public TranslateResponse(List<TranslateSentence> sentences, String src, int confidence, IdResult id_result) {
        this.sentences = sentences;
        this.src = src;
        this.confidence = confidence;
        this.id_result = id_result;
    }
}
