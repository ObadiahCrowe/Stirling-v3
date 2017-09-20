package com.obadiahpcrowe.stirling.surveys.obj;

import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 20/9/17 at 3:30 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.surveys.obj
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
@Setter
@Entity("surveys")
public class StirlingSurvey {

    @Id
    private ObjectId id;

    private String owner;
    private List<String> usersCompleted;
    private List<AccountType> targetAudiences;

    private List<SurveyQuestion> surveyQuestions;

    public StirlingSurvey() {}

    public StirlingSurvey(StirlingAccount account, List<AccountType> targetAudience, List<SurveyQuestion> questions) {
        this.owner = account.getAccountName();
        this.usersCompleted = new ArrayList<>();
        this.targetAudiences = targetAudience;
        this.surveyQuestions = questions;
    }
}
