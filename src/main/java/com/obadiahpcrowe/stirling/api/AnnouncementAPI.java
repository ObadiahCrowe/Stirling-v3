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
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.IOUtils;
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

        UUID uuid = UUID.randomUUID();
        File out = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "Announcements" +
          File.separator + uuid);

        String ext = "";
        if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".jpeg")) {
            ext = ".jpg";
        } else if (file.getOriginalFilename().endsWith(".png")) {
            ext = ".png";
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INVALID_TYPE_FORMAT, account.getLocale(), file.getOriginalFilename(), ".jpg or .png"));
        }

        File banner = new File(out + File.separator + "banner" + ext);
        try {
            if (!out.exists()) {
                out.mkdir();
            }

            file.transferTo(banner);
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "creating the announcement"));
        }

        return manager.postAnnouncement(account, uuid, announcementType, new AttachableResource(uuid, "banner" + ext, ARType.ANNOUNCEMENT),
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
    @RequestMapping(value = "/stirling/v3/announcements/get/banner", method = RequestMethod.GET)
    public String getAnnouncementBanner(@RequestParam("accountName") String accountName,
                                        @RequestParam("password") String password,
                                        @RequestParam("uuid") String rawUuid,
                                        HttpServletResponse response) {
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

        try {
            StirlingAnnouncement announcement = manager.getAnnouncement(uuid);
            if (announcement == null) {
                return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
            }

            if (announcement.getTargetAudience().contains(account.getAccountType())) {
                File file = announcement.getBannerImage().getFile();
                InputStream in = new FileInputStream(file);
                response.setContentType(Files.probeContentType(file.toPath()));

                IOUtils.copy(in, response.getOutputStream());
                response.flushBuffer();
                return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
            }
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(), "view this announcement"));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "downloading the banner image"));
        }
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "resourceUuid"})
    @RequestMapping(value = "/stirling/v3/announcements/get/resource", method = RequestMethod.GET)
    public String getAnnouncementResource(@RequestParam("accountName") String accountName,
                                          @RequestParam("password") String password,
                                          @RequestParam("uuid") String rawUuid,
                                          @RequestParam("resourceUuid") String resourceUuid,
                                          HttpServletResponse response) {
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

        UUID resUuid;
        try {
            resUuid = UUID.fromString(resourceUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), resourceUuid, "resourceUuid"));
        }

        StirlingAnnouncement announcement = manager.getAnnouncement(uuid);
        if (announcement == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_DOES_NOT_EXIST, account.getLocale(), uuid.toString()));
        }

        CompletableFuture<AttachableResource> resource = new CompletableFuture<>();
        announcement.getResources().forEach(res -> {
            if (res.getResUuid().equals(resUuid)) {
                resource.complete(res);
            }
        });

        AttachableResource res = resource.getNow(null);
        if (res == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.RESOURCE_DOES_NOT_EXIST, account.getLocale(), resUuid.toString()));
        }

        File file = res.getFile();
        try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(file.toPath()));

            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.RESOURCE_DOES_NOT_EXIST, account.getLocale(), resUuid.toString()));
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

    // Always updates edit field

    @CallableAPI(fields = {"accountName", "password", "uuid", "title"})
    @RequestMapping(value = "/stirling/v3/announcements/update/title", method = RequestMethod.GET)
    public String updateTitle(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password,
                              @RequestParam("uuid") String rawUuid,
                              @RequestParam("title") String title) {
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

        manager.updateField(uuid, "title", title);
        manager.updateField(uuid, "editDateTime", StirlingDate.getNow());
        return gson.toJson(new StirlingMsg(MsgTemplate.ANNOUNCEMENT_UPDATED, account.getLocale(), "title"));
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "desc"})
    @RequestMapping(value = "/stirling/v3/announcements/update/desc", method = RequestMethod.GET)
    public String updateDesc() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "type"})
    @RequestMapping(value = "/stirling/v3/announcements/update/type", method = RequestMethod.GET)
    public String updateType() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "banner"})
    @RequestMapping(value = "/stirling/v3/announcements/update/banner", method = RequestMethod.GET)
    public String updateBanner() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "content"})
    @RequestMapping(value = "/stirling/v3/announcements/update/content", method = RequestMethod.GET)
    public String updateContent() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "resources"})
    @RequestMapping(value = "/stirling/v3/announcements/update/resources", method = RequestMethod.GET)
    public String updateResources() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "targetAudiences"})
    @RequestMapping(value = "/stirling/v3/announcements/update/targetAudience", method = RequestMethod.GET)
    public String updateAudience() {
        return "";
    }

    @CallableAPI(fields = {"accountName", "password", "uuid", "tags"})
    @RequestMapping(value = "/stirling/v3/announcements/update/tags", method = RequestMethod.GET)
    public String updateTags() {
        return "";
    }
}
