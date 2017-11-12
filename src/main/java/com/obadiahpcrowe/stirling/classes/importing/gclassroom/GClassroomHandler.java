package com.obadiahpcrowe.stirling.classes.importing.gclassroom;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
import com.google.api.services.drive.Drive;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        String accessToken;
        String refreshToken;

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
            return gson.toJson(new StirlingMsg(MsgTemplate.UNEXPECTED_ERROR, account.getLocale(),
              "retrieving a Google refresh token. Make sure the auth code is valid"));
        }

        ImportCredential credential = new ImportCredential(accessToken, refreshToken);

        ImportAccount importAccount = importManager.getByUuid(account.getUuid());
        Map<ImportSource, ImportCredential> credentialMap = Maps.newHashMap();
        try {
            credentialMap.putAll(importAccount.getCredentials());
        } catch (NullPointerException ignored) {
        }

        if (credentialMap.containsKey(ImportSource.GOOGLE_CLASSROOM)) {
            credentialMap.replace(ImportSource.GOOGLE_CLASSROOM, credential);
        } else {
            credentialMap.put(ImportSource.GOOGLE_CLASSROOM, credential);
        }

        importManager.updateField(importAccount, "credentials", credentialMap);
        return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), ImportSource.GOOGLE_CLASSROOM.getFriendlyName()));
    }

    public String refreshAccessToken(StirlingAccount account) {
        if (!areCredentialsPresent(account)) {
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_CREDS_INVALID, account.getLocale(), ImportSource.GOOGLE_CLASSROOM.getFriendlyName()));
        }

        ImportCredential cred = importManager.getCreds(account, ImportSource.GOOGLE_CLASSROOM);
        String clientSecret = UtilFile.getInstance().getStorageLoc() + File.separator + "client_secret.json";

        try {
            GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(clientSecret));
            TokenResponse response = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
              cred.getRefreshToken(), secrets.getDetails().getClientId(), secrets.getDetails().getClientSecret()).execute();

            String token = response.getAccessToken();
            Map<ImportSource, ImportCredential> credentialMap = Maps.newHashMap();
            try {
                credentialMap.putAll(importManager.getByUuid(account.getUuid()).getCredentials());
            } catch (NullPointerException ignored) {
            }

            ImportCredential credential = new ImportCredential(token, cred.getRefreshToken());
            credentialMap.replace(ImportSource.GOOGLE_CLASSROOM, credential);
            importManager.updateField(importManager.getByUuid(account.getUuid()), "credentials", credentialMap);

            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_ACCOUNT_CREDS_SET, account.getLocale(), ImportSource.GOOGLE_CLASSROOM.getFriendlyName()));
        } catch (IOException e) {
            return gson.toJson(new StirlingMsg(MsgTemplate.IMPORT_CANNOT_REFRESH_TOKEN, account.getLocale()));
        }
    }

    public List<ImportableClass> getCourses(StirlingAccount account) {
        if (!areCredentialsPresent(account)) {
            return null;
        }

        ImportCredential cred = importManager.getCreds(account, ImportSource.GOOGLE_CLASSROOM);

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredential credential = new GoogleCredential().setAccessToken(cred.getAccessToken());

            Classroom classroom = new Classroom.Builder(transport, JacksonFactory.getDefaultInstance(), credential)
              .setApplicationName("Stirling").build();

            ListCoursesResponse response;

            try {
                response = classroom.courses().list().setPageSize(classroom.courses().list().size()).execute();
            } catch (GoogleJsonResponseException e) {
                if (e.getDetails().getCode() == 401) {
                    refreshAccessToken(account);
                    return getCourses(account);
                }
                return null;
            }

            List<Course> courses = response.getCourses();

            List<ImportableClass> classes = Lists.newArrayList();
            courses.forEach(c -> {
                if (c.getCourseState().equalsIgnoreCase("ACTIVE")) {
                    classes.add(new ImportableClass(c.getName(), c.getId()));
                }
            });

            return classes;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GoogleClass importCourse(StirlingAccount account, ImportableClass c) {
        if (!areCredentialsPresent(account)) {
            return null;
        }

        GoogleCredential credential = new GoogleCredential().setAccessToken(importManager.getByUuid(account.getUuid())
          .getCredentials().get(ImportSource.GOOGLE_CLASSROOM).getAccessToken());

        Classroom classroom;
        try {
            classroom = new Classroom.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
              credential).setApplicationName("Stirling").build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }

        Drive drive = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Stirling").build();
        ListCourseWorkResponse response = null;
        try {
            response = classroom.courses().courseWork().list(c.getId()).execute();
        } catch (GoogleJsonResponseException e) {
            if (e.getDetails().getCode() == 401) {
                refreshAccessToken(account);
                return importCourse(account, c);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        File classFile = new File(UtilFile.getInstance().getStorageLoc() + File.separator + "UserData" +
          File.separator + account.getUuid() + File.separator + "Classes" + File.separator + c.getId());

        if (!classFile.exists()) {
            classFile.mkdir();
        }

        CompletableFuture<String> courseId = new CompletableFuture<>();

        final ListCourseWorkResponse lcwr = response;
        CompletableFuture<List<AttachableResource>> resources = new CompletableFuture<>();

        Thread resourceThread = new Thread(() -> {
            List<AttachableResource> resList = Lists.newArrayList();
            for (CourseWork courseWork : lcwr.getCourseWork()) {
                if (courseWork.getMaterials() != null && courseWork.getMaterials().size() > 0) {
                    try {
                        for (Material material : courseWork.getMaterials()) {
                            DriveFile file = material.getDriveFile().getDriveFile();

                            File dlFile = new File(classFile, file.getTitle());
                            if (dlFile.exists()) {
                                continue;
                            } else {
                                try {
                                    dlFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            courseId.complete(courseWork.getCourseId());

                            CompletableFuture<AttachableResource> resource = new CompletableFuture<>();
                            Thread dlThread = new Thread(() -> {
                                OutputStream outputStream = null;

                                try {
                                    outputStream = new FileOutputStream(new File(classFile, file.getTitle()));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                MediaHttpDownloader downloader = new MediaHttpDownloader(new NetHttpTransport(),
                                  drive.getRequestFactory().getInitializer());

                                downloader.setDirectDownloadEnabled(true);
                                try {
                                    drive.files().get(file.getId()).executeMediaAndDownloadTo(outputStream);

                                    resource.complete(new AttachableResource(account.getUuid(), c.getId() + File.separator +
                                      file.getTitle(), ARType.CLASS_SINGLE));
                                } catch (HttpResponseException e) {
                                    if (e.getStatusCode() == 404) {
                                        dlFile.delete();
                                    }
                                    resource.complete(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    resource.complete(null);
                                }
                            });
                            dlThread.start();

                            try {
                                if (resource.get() != null) {
                                    resList.add(resource.get());
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            }
            resources.complete(resList);
        });
        resourceThread.start();

        CompletableFuture<List<StirlingPostable>> postables = new CompletableFuture<>();

        Thread postThread = new Thread(() -> {
            List<StirlingPostable> pList = Lists.newArrayList();
            for (CourseWork courseWork : lcwr.getCourseWork()) {
                pList.add(new StirlingPostable(courseWork.getTitle(), courseWork.getDescription(), Lists.newArrayList()));
            }
            postables.complete(pList);
        });
        postThread.start();

        GoogleClass googleClass;
        try {
            googleClass = new GoogleClass(courseId.get(), c.getClassName(), resources.get(), postables.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        for (File f : classFile.listFiles()) {
            if (f.length() == 0) {
                f.delete();
            }
        }

        return googleClass;
    }

    public boolean areCredentialsPresent(StirlingAccount account) {
        ImportCredential credential = importManager.getCreds(account, ImportSource.GOOGLE_CLASSROOM);

        try {
            if (credential.getRefreshToken() != null && credential.getAccessToken() != null) {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}
