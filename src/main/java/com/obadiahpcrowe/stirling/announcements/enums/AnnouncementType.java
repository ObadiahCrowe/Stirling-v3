package com.obadiahpcrowe.stirling.announcements.enums;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 8/9/17 at 9:32 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.announcements.enums
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public enum AnnouncementType {

    CLUB_NOTICE("Club Notice", AccountType.CLUB_CAPTAIN),
    PREFECT("Prefects", AccountType.PREFECT),
    LIBRARY("Learning Hub", AccountType.SERVICES),
    IT("IT", AccountType.SERVICES, AccountType.ADMINISTRATOR),
    POD("POD", AccountType.SERVICES),
    SUB_SCHOOL("Sub-School", AccountType.SUB_SCHOOL_LEADER),
    YEAR_LEVEL("Year Level", AccountType.YEAR_LEVEL_LEADER),
    PRINCIPAL("Principal Notice", AccountType.DEPUTY_PRINCIPAL, AccountType.PRINCIPAL),
    BLOG("Blog", AccountType.SERVICES),
    IMPORTANT("Important", AccountType.DEPUTY_PRINCIPAL, AccountType.PRINCIPAL, AccountType.DEVELOPER),
    STIRLING("Stirling", AccountType.DEVELOPER);

    private String friendlyName;
    private List<AccountType> accountTypes;

    AnnouncementType(String friendlyName, AccountType... accountTypes) {
        this.friendlyName = friendlyName;
        this.accountTypes = Arrays.asList(accountTypes);
    }
}
