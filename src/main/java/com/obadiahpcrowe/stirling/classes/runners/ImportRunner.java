package com.obadiahpcrowe.stirling.classes.runners;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 4/11/17 at 4:05 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.classes.runners
 * Copyright (c) Obadiah Crowe 2017
 */
public class ImportRunner {

    private static ImportRunner instance;

    public static ImportRunner getInstance() {
        if (instance == null)
            instance = new ImportRunner();
        return instance;
    }

    public void start() {
        /*
        Thread t = new Thread(() -> {
            ImportManager.getInstance().getAllAccounts().forEach(account -> {
                Thread accounts = new Thread(() -> {
                    StirlingAccount acc = AccountManager.getInstance().getAccount(account.getAccountUuid());
                    account.getImportableClasses().forEach((source, clazz) -> {
                        Thread classes = new Thread(() -> {
                            clazz.forEach(c -> {
                                if (source == ImportSource.DAYMAP) {
                                    CompletableFuture<ImportCredential> creds = new CompletableFuture<>();
                                    account.getCredentials().forEach((src, cred) -> {
                                        if (src == ImportSource.DAYMAP) {
                                            creds.complete(cred);
                                        }
                                    });

                                    ImportCredential credential = creds.getNow(null);
                                    if (credential == null) {
                                        return;
                                    }

                                    DaymapClass daymapClass = DaymapScraper.getInstance().getFullCourse(
                                      credential.getUsername(), credential.getPassword(), c, true);

                                    //
                                } else if (source == ImportSource.MOODLE) {
                                    CompletableFuture<ImportCredential> creds = new CompletableFuture<>();
                                    account.getCredentials().forEach((src, cred) -> {
                                        if (src == ImportSource.MOODLE) {
                                            creds.complete(cred);
                                        }
                                    });

                                    ImportCredential credential = creds.getNow(null);
                                    if (credential == null) {
                                        return;
                                    }

                                    MoodleClass moodleClass = (MoodleClass) c;
                                    //
                                } else if (source == ImportSource.GOOGLE_CLASSROOM) {
                                    CompletableFuture<ImportCredential> creds = new CompletableFuture<>();
                                    account.getCredentials().forEach((src, cred) -> {
                                        if (src == ImportSource.GOOGLE_CLASSROOM) {
                                            creds.complete(cred);
                                        }
                                    });

                                    ImportCredential credential = creds.getNow(null);
                                    if (credential == null) {
                                        return;
                                    }

                                    GoogleClass googleClass = (GoogleClass) c;
                                    //
                                }
                            });
                        });
                        classes.start();
                    });
                });
                accounts.start();
            });
        });
        t.start();*/
    }
}
