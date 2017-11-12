package com.obadiahpcrowe.stirling.classes.importing.moodle;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingSection;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/10/17 at 2:55 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.moodle
 * Copyright (c) Obadiah Crowe 2017
 */
public class MoodleScraper {

    private static MoodleScraper instance;
    private ImportManager importManager;

    private MoodleScraper() {
        this.importManager = ImportManager.getInstance();
    }

    public static MoodleScraper getInstance() {
        if (instance == null)
            instance = new MoodleScraper();
        return instance;
    }

    public boolean areCredentialsValid(ImportCredential credential) {
        final WebClient webClient = new StirlingWebClient(BrowserVersion.CHROME).getClient(null,
          new NicelyResynchronizingAjaxController());
        try {
            String url = "http://dlb.sa.edu.au/gihsmoodle/login/index.php";

            HtmlPage loginPage = webClient.getPage(url);

            HtmlForm loginForm = loginPage.getForms().get(0);
            loginForm.getInputByName("username").type(credential.getUsername());
            loginForm.getInputByName("password").type(credential.getPassword());

            loginPage = loginForm.getInputByValue("Log in").click();

            return !loginPage.getUrl().toString().equalsIgnoreCase(url);
        } catch (IOException e) {
            return false;
        }
    }

    public List<ImportableClass> getCourses(StirlingAccount account) {
        if (importManager.areCredentialsValid(account, ImportSource.MOODLE)) {
            ImportAccount acc = importManager.getByUuid(account.getUuid());
            ImportCredential cred = acc.getCredentials().get(ImportSource.MOODLE);

            List<ImportableClass> classes = Lists.newArrayList();

            final WebClient webClient = new StirlingWebClient(BrowserVersion.CHROME).getClient(null,
              new NicelyResynchronizingAjaxController());

            try {
                HtmlPage loginPage = webClient.getPage("http://dlb.sa.edu.au/gihsmoodle/login/index.php");

                HtmlForm loginForm = loginPage.getForms().get(0);
                loginForm.getInputByName("username").type(cred.getUsername());
                loginForm.getInputByName("password").type(cred.getPassword());

                loginPage = loginForm.getInputByValue("Log in").click();

                HtmlPage coursesPage = webClient.getPage("http://dlb.sa.edu.au/gihsmoodle/my/index.php?mynumber=-2");
                List<HtmlDivision> divs = coursesPage.getByXPath("//*[@class=\"box coursebox\"]");

                divs.forEach(div -> {
                    String courseId = div.getId().replace("course-", "");
                    String courseName = div.getFirstChild().getFirstChild().getFirstChild().getTextContent();

                    classes.add(new ImportableClass(courseName, courseId));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return classes;
        }
        return null;
    }

    public MoodleClass getCourse(StirlingAccount account, ImportableClass clazz) {
        ImportCredential cred = importManager.getCreds(account, ImportSource.MOODLE);

        final WebClient webClient = new StirlingWebClient(BrowserVersion.CHROME).getClient(null,
          new NicelyResynchronizingAjaxController());

        HtmlPage page;
        try {
            String url = "http://dlb.sa.edu.au/gihsmoodle/login/index.php";

            page = webClient.getPage(url);

            HtmlForm loginForm = page.getForms().get(0);
            loginForm.getInputByName("username").type(cred.getUsername());
            loginForm.getInputByName("password").type(cred.getPassword());

            page = loginForm.getInputByValue("Log in").click();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String classUrl = "http://dlb.sa.edu.au/gihsmoodle/course/view.php?id=" + clazz.getId();
        try {
            page = webClient.getPage(classUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        CompletableFuture<List<AttachableResource>> resources = new CompletableFuture<>();
        CompletableFuture<List<StirlingPostable>> posts = new CompletableFuture<>();
        CompletableFuture<List<StirlingAssignment>> assignments = new CompletableFuture<>();
        CompletableFuture<List<StirlingSection>> sections = new CompletableFuture<>();

        Thread resourcesThread = new Thread(() -> {
            //
        });

        Thread contentThread = new Thread(() -> {
            //
        });

        Thread assignmentThread = new Thread(() -> {
            //
        });

        Thread workbookThread = new Thread(() -> {
            //
        });


        return null;
    }
}
