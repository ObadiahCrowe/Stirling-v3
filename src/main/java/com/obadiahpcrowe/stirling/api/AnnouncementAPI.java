package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.announcements.AnnouncementManager;
import com.obadiahpcrowe.stirling.announcements.StirlingAnnouncement;
import com.obadiahpcrowe.stirling.announcements.enums.AnnouncementType;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:07 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class AnnouncementAPI implements APIController {

    private Gson gson = new Gson();
    private AnnouncementManager manager = AnnouncementManager.getInstance();
    private AccountManager accountManager = AccountManager.getInstance();

    @CallableAPI(fields = { "accountName", "password", "type", "image", "title", "desc", "content", "resourceNames", "targetAudiences", "tags" })
    @RequestMapping(value = "/stirling/v3/announcements/create", method = RequestMethod.POST)
    public String createAnnouncement(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("type") String type,
                                     @RequestParam("image") MultipartFile file,
                                     @RequestParam("title") String title,
                                     @RequestParam("desc") String desc,
                                     @RequestParam("content") String content,
                                     @RequestParam(value = "resources", required = false) MultipartFile[] files,
                                     @RequestParam("targetAudiences") String targetAudiences,
                                     @RequestParam(value = "tags", required = false) String tags) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        AnnouncementType announcementType;
        try {
            announcementType = AnnouncementType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), type, "type"));
        }

        if (!(file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".jpeg"))) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INVALID_TYPE_FORMAT, account.getLocale(), file.getOriginalFilename(), ".jpg"));
        }

        UUID uuid = UUID.randomUUID();
        File out = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Announcements" +
          File.separator + uuid);
        File banner = new File(out + File.separator + "banner.jpg");
        try {
            if (!out.exists()) {
                out.mkdir();
            }

            file.transferTo(banner);
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "creating the announcement"));
        }

        return manager.postAnnouncement(account, uuid, announcementType, new AttachableResource(uuid, "banner.jpg", ARType.ANNOUNCEMENT),
          title, desc, content, files, targetAudiences, tags);
    }

    @CallableAPI(fields = { "accountName", "password", "uuid" })
    @RequestMapping(value = "/stirling/v3/announcements/delete", method = RequestMethod.GET)
    public String deleteAnnouncement(@RequestParam("accountName") String accountName,
                                     @RequestParam("password") String password,
                                     @RequestParam("uuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), rawUuid, "uuid"));
        }

        return manager.deleteAnnouncement(account, uuid);
    }

    @CallableAPI(fields = { "accountName", "password", "uuid" })
    @RequestMapping(value = "/stirling/v3/announcements/get/banner", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getAnnouncementBanner(@RequestParam("accountName") String accountName,
                                        @RequestParam("password") String password,
                                        @RequestParam("uuid") String rawUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return null;
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(rawUuid);
        } catch (IllegalArgumentException e) {
            return null;
        }

        try {
            StirlingAnnouncement announcement = manager.getAnnouncement(uuid);
            if (announcement.getTargetAudience().contains(account.getAccountType())) {
                System.out.println(manager.getAnnouncement(uuid).getBannerImage().getFile().getPath());
                InputStream inputStream = new FileInputStream(manager.getAnnouncement(uuid).getBannerImage().getFile());
                return IOUtils.toByteArray(inputStream);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @CallableAPI(fields = { "accountName", "password" })
    @RequestMapping(value = "/stirling/v3/announcements/getAll", method = RequestMethod.GET)
    public String getAnnouncements(@RequestParam("accountName") String accountName,
                                   @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return gson.toJson(manager.getAnnouncements(account));
    }

    // Above is all g, below is fucked

    @CallableAPI(fields = { "accountName", "password", "uuid", "field", "value" })
    @RequestMapping(value = "/stirling/v3/announcements/update/field", method = RequestMethod.GET)
    public String updateAnnouncementField(@RequestParam("accountName") String accountName,
                                          @RequestParam("password") String password,
                                          @RequestParam("uuid") String rawUuid,
                                          @RequestParam("field") String field,
                                          @RequestParam("value") String value) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        List<String> validFields = Arrays.asList(
          "type",
          "title",
          "desc",
          "content",
          "targetAudiences",
          "tags"
        );

        CompletableFuture<String> future = new CompletableFuture<>();
        validFields.forEach(f -> {
            if (field.equalsIgnoreCase(f)) {
                future.complete(f);
            }
        });

        String finalField = future.getNow("NONE");
        if (finalField.equalsIgnoreCase("NONE")) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNKNOWN_FIELD, account.getLocale(), finalField));
        }

        return manager.updateField(UUID.fromString(rawUuid), field, value);
    }

    @CallableAPI(fields = { "accountName", "password", "banner" })
    @RequestMapping(value = "/stirling/v3/announcements/update/banner", method = RequestMethod.POST)
    public String updateAnnouncementBanner(@RequestParam("accountName") String accountName,
                                           @RequestParam("password") String password,
                                           @RequestParam("uuid") String rawUuid,
                                           @RequestParam("banner") MultipartFile file) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        try {
            File out = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Announcements" +
              File.separator + rawUuid);

            if (!out.exists()) {
                out.mkdir();
            }

            File banner = new File(out + File.separator + "banner.jpg");

            file.transferTo(banner);
            return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_EDITED, StirlingLocale.ENGLISH, rawUuid));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "updating the banner image"));
        }
    }
}
