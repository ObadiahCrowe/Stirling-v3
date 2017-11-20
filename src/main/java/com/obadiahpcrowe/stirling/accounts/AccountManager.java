package com.obadiahpcrowe.stirling.accounts;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.calendar.CalendarManager;
import com.obadiahpcrowe.stirling.database.MorphiaService;
import com.obadiahpcrowe.stirling.database.dao.AccountDAOImpl;
import com.obadiahpcrowe.stirling.database.dao.interfaces.AccountDAO;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.modules.events.EventManager;
import com.obadiahpcrowe.stirling.modules.events.types.AccountCreatedEvent;
import com.obadiahpcrowe.stirling.schools.SchoolManager;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 5/9/17 at 4:22 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.accounts
 * Copyright (c) Obadiah Crowe 2017
 */
public class AccountManager {

    private static AccountManager instance;

    private MorphiaService morphiaService;
    private AccountDAO accountDAO;
    private Gson gson;

    public AccountManager() {
        this.morphiaService = new MorphiaService();
        this.accountDAO = new AccountDAOImpl(StirlingAccount.class, morphiaService.getDatastore());
        this.gson = new Gson();
    }

    public String createAccount(String accountName, String emailAddress, String password) {
        if (!accountExists(accountName)) {
            if (!emailExists(emailAddress)) {
                String ext = SchoolManager.getInstance().getSchool().getEmailExtension();
                if (!emailAddress.endsWith(ext)) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.EMAIL_ADDRESS_INVALID_EXT, StirlingLocale.ENGLISH, emailAddress, ext));
                }

                if (password.length() < 8) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.FIELD_TOO_SHORT, StirlingLocale.ENGLISH, "password", "8"));
                }

                if (password.length() > 36) {
                    return gson.toJson(new StirlingMsg(MsgTemplate.FIELD_TOO_LONG, StirlingLocale.ENGLISH, "password", "36"));
                }

                StirlingAccount account = new StirlingAccount(accountName, emailAddress, password);
                accountDAO.save(account);

                // Disabled until I unfuck it.
                //CalendarManager.getInstance().createCalendar(account.getUuid(), account.getDisplayName() + "'s Calendar", "", Lists.newArrayList());

                UtilFile.getInstance().createUserFiles(account.getUuid());

                StirlingMsg msg = new StirlingMsg(MsgTemplate.ACCOUNT_CREATED, StirlingLocale.ENGLISH, accountName);
                EventManager.getInstance().fireEvent(new AccountCreatedEvent(msg, accountName, account.getUuid()));
                return gson.toJson(msg);
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.EMAIL_ADDRESS_IN_USE, StirlingLocale.ENGLISH, emailAddress));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_EXISTS, StirlingLocale.ENGLISH, accountName));
        }
    }

    public String deleteAccount(String accountName, String password) {
        if (accountExists(accountName)) {
            if (validCredentials(accountName, password)) {
                return deleteAccount(getAccount(accountName).getUuid());
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }
    }

    public String deleteAccount(UUID uuid) {
        if (accountExists(uuid)) {
            StirlingAccount account = getAccount(uuid);
            accountDAO.delete(account);

            UtilFile.getInstance().deleteUserFiles(uuid);
            CalendarManager.getInstance().deleteCalendar(uuid);

            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DELETED, account.getLocale(), uuid.toString()));
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, uuid.toString()));
        }
    }

    public void updateField(StirlingAccount account, String field, Object value) {
        accountDAO.updateField(account, field, value);
    }

    private boolean emailExists(String emailAddress) {
        return accountDAO.emailAddressExists(emailAddress);
    }

    public boolean accountExists(String accountName) {
        if (getAccount(accountName) == null) {
            return false;
        }
        return true;
    }

    public boolean accountExists(UUID uuid) {
        if (getAccount(uuid) == null) {
            return false;
        }
        return true;
    }

    public StirlingAccount getAccount(UUID uuid) {
        return accountDAO.getByUuid(uuid);
    }

    public StirlingAccount getAccount(String accountName) {
        return accountDAO.getByAccountName(accountName);
    }

    public boolean validCredentials(String accountName, String password) {
        if (accountExists(accountName)) {
            StirlingAccount account = getAccount(accountName);
            if (BCrypt.checkpw(password, account.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public List<StirlingAccount> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }
}
