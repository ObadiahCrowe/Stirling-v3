package com.obadiahpcrowe.stirling.accounts;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.messaging.contacts.ContactableAccount;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 2:44 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.accounts
 * Copyright (c) Obadiah Crowe 2017
 */
@Getter
public class StirlingAccount {

    private String displayName;
    private String accountName;
    private String emailAddress;
    private UUID uuid;
    private StirlingLocale locale;
    private AccountType accountType;

    private String password;
    private String salt;

    private List<StirlingClass> stirlingClasses;

    private File avatarImage;
    private File bannerImage;

    public StirlingAccount(String accountName, String emailAddress, String password) {
        String salt = BCrypt.gensalt();

        this.displayName = accountName;
        this.accountName = accountName;
        this.emailAddress = emailAddress;
        this.uuid = UUID.randomUUID();
        this.locale = StirlingLocale.ENGLISH;
        this.accountType = AccountType.STUDENT;
        this.password = BCrypt.hashpw(password, salt);
        this.salt = salt;
        this.stirlingClasses = new ArrayList<>();
        this.avatarImage = null;
        this.bannerImage = null;
    }
}
