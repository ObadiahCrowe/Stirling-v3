package com.obadiahpcrowe.stirling.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.cloud.CloudManager;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.notes.NoteManager;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:08 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class NotesAPI implements APIController {

    private AccountManager accountManager = AccountManager.getInstance();
    private NoteManager noteManager = NoteManager.getInstance();
    private Gson gson = new Gson();

    @CallableAPI(fields = {"accountName", "password", "title", "content", "resources"})
    @RequestMapping(value = "/stirling/v3/notes/create", method = RequestMethod.GET)
    public String createNote(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "resources", required = false) MultipartFile[] resources) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        List<AttachableResource> res = Lists.newArrayList();

        for (MultipartFile file : resources) {
            CloudManager.getInstance().uploadFile(account, file);
            res.add(new AttachableResource(account.getUuid(), CloudManager.getInstance().getCloudFolder(account) + File.separator + file.getOriginalFilename()));
        }

        return noteManager.createNote(account, title, content, res);
    }

    @CallableAPI(fields = {"accountName", "password", "noteUuid"})
    @RequestMapping(value = "/stirling/v3/notes/delete", method = RequestMethod.GET)
    public String deleteNote(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("noteUuid") String noteUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(noteUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), noteUuid, "noteUuid"));
        }

        return noteManager.deleteNote(account, uuid);
    }

    @CallableAPI(fields = {"accountName", "password", "noteUuid"})
    @RequestMapping(value = "/stirling/v3/notes/get", method = RequestMethod.GET)
    public String getNote(@RequestParam("accountName") String accountName,
                          @RequestParam("password") String password,
                          @RequestParam("noteUuid") String noteUuid) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(noteUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), noteUuid, "noteUuid"));
        }

        return gson.toJson(noteManager.getNote(uuid));
    }

    @CallableAPI(fields = {"accountName", "password"})
    @RequestMapping(value = "/stirling/v3/notes/getAll", method = RequestMethod.GET)
    public String getAllNotes(@RequestParam("accountName") String accountName,
                              @RequestParam("password") String password) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        return gson.toJson(noteManager.getNotes(account));
    }

    @CallableAPI(fields = {"accountName", "password", "noteUuid", "resources"})
    @RequestMapping(value = "/stirling/v3/notes/resources/add", method = RequestMethod.GET)
    public String addResources(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("noteUuid") String noteUuid,
                               @RequestParam("resources") MultipartFile[] resources) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        UUID uuid = null;
        try {
            uuid = UUID.fromString(noteUuid);
        } catch (IllegalArgumentException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.INCOMPATIBLE_VALUE, account.getLocale(), noteUuid, "noteUuid"));
        }

        List<AttachableResource> res = Lists.newArrayList();

        for (MultipartFile file : resources) {
            CloudManager.getInstance().uploadFile(account, file);
            res.add(new AttachableResource(account.getUuid(), CloudManager.getInstance().getCloudFolder(account) + File.separator + file.getOriginalFilename()));
        }

        return noteManager.attachFiles(account, uuid, res);
    }

    @CallableAPI(fields = {"accountName", "password"})
}
