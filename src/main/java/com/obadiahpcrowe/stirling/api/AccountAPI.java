package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.IOUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 6/9/17 at 4:58 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class AccountAPI implements APIController {

    private Gson gson = new Gson();
    private AccountManager accountManager = AccountManager.getInstance();

    @CallableAPI(fields = { "accountName", "emailAddress", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/create", method = RequestMethod.GET)
    public String createAccount(@RequestParam("accountName") String accountName,
                                @RequestParam("emailAddress") String emailAddress,
                                @RequestParam("password") String password) {
        return accountManager.createAccount(accountName, emailAddress, password);
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/delete", method = RequestMethod.GET)
    public String deleteAccount(@RequestParam("accountName") String accountName,
                                @RequestParam("password") String password) {
        return accountManager.deleteAccount(accountName, password);
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/accounts/validCredentials", method = RequestMethod.GET)
    public boolean validateCredentials(@RequestParam("accountName") String accountName,
                                       @RequestParam("password") String password) {
        return accountManager.validCredentials(accountName, password);
    }

    @CallableAPI(fields = { "accountName", "password", "displayName" })
    @RequestMapping(value = "/stirling/v3/accounts/update/displayName", method = RequestMethod.GET)
    public String updateDisplayName(@RequestParam("accountName") String accountName,
                                    @RequestParam("password") String password,
                                    @RequestParam("displayName") String displayName) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        accountManager.updateField(account, "displayName", displayName);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "displayName", account.getAccountName()));
    }

    @CallableAPI(fields = { "accountName", "password", "emailAddress" })
    @RequestMapping(value = "/stirling/v3/accounts/update/emailAddress", method = RequestMethod.GET)
    public String updateEmailAddress(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("emailAddress") String emailAddress) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        accountManager.updateField(account, "emailAddress", emailAddress);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "emailAddress", account.getAccountName()));
    }

    @CallableAPI(fields = { "accountName", "password", "locale" })
    @RequestMapping(value = "/stirling/v3/accounts/update/locale", method = RequestMethod.GET)
    public String updateLocale(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("locale") String rawLocale) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        StirlingLocale locale;
        try {
            locale = StirlingLocale.valueOf(rawLocale.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawLocale, "locale"));
        }

        accountManager.updateField(account, "locale", locale);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, accountManager.getAccount(accountName).getLocale(),
          "locale", account.getAccountName()));
    }

    @CallableAPI(fields = { "accountName", "password", "newPassword" })
    @RequestMapping(value = "/stirling/v3/accounts/update/password", method = RequestMethod.GET)
    public String updatePassword(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password,
                                 @RequestParam("newPassword") String newPassword) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        String salt = BCrypt.gensalt();
        String ePassword = BCrypt.hashpw(newPassword, salt);

        accountManager.updateField(account, "salt", salt);
        accountManager.updateField(account, "password", ePassword);
        return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(),
          "password", account.getAccountName()));
    }

    @CallableAPI(fields = { "accountName", "password", "avatar" })
    @RequestMapping(value = "/stirling/v3/accounts/update/avatar", method = RequestMethod.POST)
    public String updateAvatar(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("avatar") MultipartFile file) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        try {
            File out = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
              "Images" + File.separator + "avatar.png");

            if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".jpeg") || file.getOriginalFilename().endsWith(".png")) {
                file.transferTo(out);
                return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(), "avatar", file.getOriginalFilename()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.INVALID_TYPE_FORMAT, account.getLocale(), file.getOriginalFilename(), ".png, .jpg, or .jpeg"));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "updating your avatar"));
        }
    }

    @CallableAPI(fields = { "accountName", "password", "banner" })
    @RequestMapping(value = "/stirling/v3/accounts/update/banner", method = RequestMethod.POST)
    public String updateBanner(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("banner") MultipartFile file) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        try {
            File out = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
              "Images" + File.separator + "banner.jpg");

            if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".jpeg") || file.getOriginalFilename().endsWith(".png")) {
                file.transferTo(out);
                return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_FIELD_EDITED, account.getLocale(), "banner", file.getOriginalFilename()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.INVALID_TYPE_FORMAT, account.getLocale(), file.getOriginalFilename(), ".png, .jpg, or .jpeg"));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "updating your banner"));
        }
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/get/displayName", method = RequestMethod.GET)
    public String getDisplayName(@RequestParam("accountName") String accountName,
                                 @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return account.getDisplayName();
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/get/emailAddress", method = RequestMethod.GET)
    public String getEmailAddress(@RequestParam("accountName") String accountName,
                                  @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return account.getEmailAddress();
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/get/locale", method = RequestMethod.GET)
    public String getLocale(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return account.getLocale().toString();
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/get/avatar", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public String getAvatar(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            HttpServletResponse response) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = account.getAvatarImage().getFile();
        try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(file.toPath()));

            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "fetching your avatar"));
        }
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/accounts/get/banner", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public String getBanner(@RequestParam("accountName") String accountName,
                            @RequestParam("password") String password,
                            HttpServletResponse response) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return null;
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return null;
        }

        File file = account.getBannerImage().getFile();
        try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(file.toPath()));

            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "fetching your banner"));
        }
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/accounts/get/accountType", method = RequestMethod.GET)
    public AccountType getAccountType(@RequestParam("accountName") String accountName,
                                      @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return null;
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return null;
        }

        return account.getAccountType();
    }
}
