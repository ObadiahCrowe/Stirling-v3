package com.obadiahpcrowe.stirling.classes.importing.gclassroom;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:56 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.gclassroom
 * Copyright (c) Obadiah Crowe 2017
 */
public class GClassroomHandler {

    private static GClassroomHandler instance;
    private Gson gson;
    private ImportManager importManager;

    private GClassroomHandler() {
        this.gson = new Gson();
        this.importManager = ImportManager.getInstance();
    }

    public static GClassroomHandler getInstance() {
        if (instance == null)
            instance = new GClassroomHandler();
        return instance;
    }

    public String addGoogleClassroomCreds(StirlingAccount account, String authCode) {
        String clientSecret = UtilFile.getInstance().getStorageLoc() + File.separator + "client_secret.json";
        String accessToken, refreshToken;

        try {
            GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
              new FileReader(clientSecret));

            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(),
              new JacksonFactory(), "https://www.googleapis.com/oauth2/v4/token", secrets.getDetails().getClientId(),
              secrets.getDetails().getClientSecret(), authCode, "https://da.gihs.sa.edu.au")
              .setGrantType("authorization_code").execute();

            accessToken = response.getAccessToken();
            refreshToken = response.getRefreshToken();
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(), "retrieving a Google refresh token"));
        }

        ImportCredential credential = new ImportCredential(accessToken, refreshToken);

        ImportAccount importAccount = importManager.getByUuid(account.getUuid());
        Map<ImportSource, ImportCredential> credentialMap = Maps.newHashMap();
        try {
            credentialMap.putAll(importAccount.getCredentials());
        } catch (NullPointerException ignored) {
        }

        credentialMap.replace(ImportSource.GOOGLE_CLASSROOM, credential);
        importManager.updateField(importAccount, "credentials", credentialMap);
        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), ImportSource.GOOGLE_CLASSROOM.getFriendlyName()));
    }
}
