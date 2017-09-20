package com.obadiahpcrowe.stirling.surveys.obj;

import com.obadiahpcrowe.stirling.surveys.enums.AnswerType;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 4:06 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class SurveyQuestion {

    @Id
    private ObjectId id;

    private String question;
    private boolean required;
    private AnswerType answerType;
    private String answer;

    public SurveyQuestion() {}

    public SurveyQuestion(String question, boolean required, AnswerType answerType, String answer) {
        this.question = question;
        this.required = required;
        this.answerType = answerType;
        this.answer = answer;
    }
}
