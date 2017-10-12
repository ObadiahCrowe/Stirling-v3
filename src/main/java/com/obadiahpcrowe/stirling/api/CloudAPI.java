package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.AccountManager;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import com.obadiahpcrowe.stirling.localisation.StirlingLocale;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 21/9/17 at 5:07 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class CloudAPI implements APIController {

    private AccountManager accountManager = AccountManager.getInstance();
    private Gson gson = new Gson();

    @CallableAPI(fields = { "accountName", "password", "file" })
    @RequestMapping(value = "/stirling/v3/cloud/upload", method = RequestMethod.GET)
    public String uploadFile(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("file") MultipartFile file) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        try {
            File out = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
              "Cloud" + File.separator + file.getOriginalFilename());

            if (out.exists()) {
                return gson.toJson(new StirlingMsg(MsgTemplate.FILE_ALREADY_EXISTS, account.getLocale(), file.getOriginalFilename()));
            }

            file.transferTo(out);
            return gson.toJson(new StirlingMsg(MsgTemplate.UPLOADING_FILE, account.getLocale(), file.getOriginalFilename()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "uploading the file"));
        }
    }

    @CallableAPI(fields = { "accountName", "password", "filePath" })
    @RequestMapping(value = "/stirling/v3/cloud/download", method = RequestMethod.GET)
    public String downloadFile(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("filePath") String filePath,
                               HttpServletResponse response) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + filePath);

        if (!file.exists()) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLOUD_FILE_DOES_NOT_EXIST, account.getLocale(), filePath));
        }

        try {
            InputStream in = new FileInputStream(file);
            response.setContentType(Files.probeContentType(file.toPath()));

            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            return gson.toJson(new StirlingMsg(MsgTemplate.DOWNLOADING_FILE, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "downloading the file"));
        }
    }

    @CallableAPI(fields = { "accountName", "password", "filePath", "newName" })
    @RequestMapping(value = "/stirling/v3/cloud/rename", method = RequestMethod.GET)
    public String renameFile(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("filePath") String filePath,
                             @RequestParam("newName") String newName) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + filePath);

        File newFile = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) +
          File.separator + "Cloud" + File.separator + newName + file.getName().split(".")[1]);

        if (!file.exists()) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CLOUD_FILE_DOES_NOT_EXIST, account.getLocale(), filePath));
        }

        if (newFile.exists()) {
            return gson.toJson(new StirlingMsg(MsgTemplate.CANNOT_RENAME_FILE_EXISTS, account.getLocale(), file.getName(), newName));
        }

        file.renameTo(newFile);
        return gson.toJson(new StirlingMsg(MsgTemplate.FILE_RENAMED, account.getLocale(), file.getName(), newName + file.getName().split(".")[1]));
    }

    @CallableAPI(fields = { "accountName", "password", "filePath" })
    @RequestMapping(value = "/stirling/v3/cloud/delete/file", method = RequestMethod.GET)
    public String deleteFile(@RequestParam("accountName") String accountName,
                             @RequestParam("password") String password,
                             @RequestParam("filePath") String filePath) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + filePath);

        file.delete();

        return gson.toJson(new StirlingMsg(MsgTemplate.FILE_DELETED, account.getLocale(), file.getName()));
    }

    @CallableAPI(fields = { "accountName", "password", "folderName" })
    @RequestMapping(value = "/stirling/v3/cloud/create/folder", method = RequestMethod.GET)
    public String createFolder(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("folderName") String folderName) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + folderName);

        if (!file.exists()) {
            file.mkdir();
            return gson.toJson(new StirlingMsg(MsgTemplate.FOLDER_CREATED, account.getLocale(), folderName));
        }
        return gson.toJson(new StirlingMsg(MsgTemplate.FOLDER_ALREADY_EXISTS, account.getLocale(), folderName));
    }

    @CallableAPI(fields = { "accountName", "password", "folderPath" })
    @RequestMapping(value = "/stirling/v3/cloud/delete/folder", method = RequestMethod.GET)
    public String deleteFolder(@RequestParam("accountName") String accountName,
                               @RequestParam("password") String password,
                               @RequestParam("folderPath") String folderPath) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + folderPath);

        UtilFile.getInstance().deleteDirectory(file);

        return gson.toJson(new StirlingMsg(MsgTemplate.FOLDER_DELETED, account.getLocale(), file.getName()));
    }

    @CallableAPI(fields = { "accountName", "password", "filePath", "newPath" })
    @RequestMapping(value = "/stirling/v3/cloud/move", method = RequestMethod.GET)
    public String moveFile(@RequestParam("accountName") String accountName,
                           @RequestParam("password") String password,
                           @RequestParam("filePath") String filePath,
                           @RequestParam("newPath") String newPath) {
        StirlingAccount account = accountManager.getAccount(accountName);
        if (account == null) {
            return gson.toJson(new StirlingMsg(MsgTemplate.ACCOUNT_DOES_NOT_EXIST, StirlingLocale.ENGLISH, accountName));
        }

        if (!accountManager.validCredentials(accountName, password)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.PASSWORD_INCORRECT, StirlingLocale.ENGLISH, accountName));
        }

        File file = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + filePath);

        File dest = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) + File.separator +
          "Cloud" + File.separator + newPath);

        if (dest.exists()) {
            return gson.toJson(new StirlingMsg(MsgTemplate.FILE_ALREADY_EXISTS, account.getLocale(), dest.getName()));
        }

        try {
            FileUtils.moveFile(file, dest);
            return gson.toJson(new StirlingMsg(MsgTemplate.FILE_MOVED, account.getLocale(), file.getName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "while moving the file"));
        }
    }
}
