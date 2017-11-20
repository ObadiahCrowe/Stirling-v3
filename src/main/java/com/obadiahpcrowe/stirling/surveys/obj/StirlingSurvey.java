package com.obadiahpcrowe.stirling.surveys.obj;

import com.google.common.collect.Maps;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private UUID uuid;
    private String title;
    private String desc;
    private List<AccountType> targetAudiences;

    private List<SurveyQuestion> surveyQuestions;
    private Map<UUID, List<SurveyQuestion>> completedResponses;

    @Deprecated
    public StirlingSurvey() {}

    public StirlingSurvey(StirlingAccount account, String title, String desc, List<AccountType> targetAudience,
                          List<SurveyQuestion> questions) {
        this.owner = account.getAccountName();
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.desc = desc;
        this.targetAudiences = targetAudience;
        this.surveyQuestions = questions;
        this.completedResponses = Maps.newHashMap();
    }
}
