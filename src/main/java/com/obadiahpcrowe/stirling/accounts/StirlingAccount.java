package com.obadiahpcrowe.stirling.accounts;

import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.calendar.obj.StirlingCalendar;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

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
@Entity("accounts")
public class StirlingAccount {

    @Id
    private ObjectId objectId;

    private String displayName;
    private String accountName;
    private String emailAddress;

    private UUID uuid;
    private StirlingLocale locale;
    private AccountType accountType;

    private String password;
    private String salt;

    @Reference
    private StirlingCalendar calendar;

    @Reference
    private List<StirlingClass> stirlingClasses;

    private File avatarImage;
    private File bannerImage;

    public StirlingAccount() {}

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
        this.calendar = null;
        this.stirlingClasses = new ArrayList<>();
        this.avatarImage = null;
        this.bannerImage = null;
    }
}
