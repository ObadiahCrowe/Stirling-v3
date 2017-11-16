package com.obadiahpcrowe.stirling.classes.importing.moodle;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingSection;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;
import com.obadiahpcrowe.stirling.util.UtilFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        final String classUrl = "http://dlb.sa.edu.au/gihsmoodle/course/view.php?id=" + clazz.getId();

        CompletableFuture<List<AttachableResource>> resources = new CompletableFuture<>();
        CompletableFuture<List<StirlingPostable>> posts = new CompletableFuture<>();
        CompletableFuture<List<StirlingAssignment>> assignments = new CompletableFuture<>();
        CompletableFuture<List<StirlingSection>> sections = new CompletableFuture<>();

        Thread everythingThread = new Thread(() -> {
            WebClient client = webClient;
            HtmlPage p = null;
            HtmlUnorderedList topics;
            HtmlUnorderedList gtopics = null;

            try {
                p = client.getPage(classUrl);
                topics = (HtmlUnorderedList) p.getByXPath("//*[@id=\"region-main\"]/div/div/ul").get(0);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (IndexOutOfBoundsException e) {
                try {
                    p = client.getPage(classUrl);
                    topics = (HtmlUnorderedList) p.getByXPath("//*[@id=\"region-main\"]/div/div/div/ul").get(0);
                } catch (IndexOutOfBoundsException e1) {
                    topics = null;
                } catch (IOException e1) {
                    return;
                }
            }

            try {
                gtopics = (HtmlUnorderedList) p.getByXPath("//*[@id=\"gtopics\"]").get(0);
            } catch (IndexOutOfBoundsException ignored) {
            }

            List<Thread> threads = Lists.newArrayList();
            List<AttachableResource> resourceList = Lists.newArrayList();
            if (topics != null) {
                topics.getChildElements().forEach(section -> {
                    if (section.getTagName().equalsIgnoreCase("li")) {
                        section.getChildElements().forEach(div -> {
                            if (div.getAttribute("class").equalsIgnoreCase("content")) {
                                try {
                                    div.getLastElementChild().getChildElements().forEach(node -> {
                                        Thread t = new Thread(() -> {
                                            if (node.getAttribute("class").contains("modtype_resource")) {
                                                node.getLastElementChild().getFirstElementChild().getLastElementChild()
                                                  .getChildElements().forEach(e -> {
                                                    if (e.getAttribute("class").contains("activityinstance")) {
                                                        String url = e.getFirstElementChild().getAttribute("href");
                                                        CompletableFuture<String> name = new CompletableFuture<>();
                                                        e.getFirstElementChild().getChildElements().forEach(c -> {
                                                            if (c.getAttribute("class").equalsIgnoreCase("instancename")) {
                                                                if (e.getLastElementChild().getAttribute("class")
                                                                  .equalsIgnoreCase("resourcelinkdetails")) {
                                                                    String fileType = e.getLastElementChild().getTextContent();

                                                                    String type = "";
                                                                    if (fileType.contains("Word document")) {
                                                                        type = ".doc";
                                                                    } else if (fileType.contains("Powerpoint")) {
                                                                        type = ".ppt";
                                                                    } else if (fileType.contains("PDF")) {
                                                                        type = ".pdf";
                                                                    } else if (fileType.contains("Folder")) {
                                                                        name.complete(null);
                                                                    } else if (fileType.contains("URL")) {
                                                                        return;
                                                                    } else if (fileType.contains("Book")) {
                                                                        return;
                                                                    } else if (fileType.contains("Game")) {
                                                                        return;
                                                                    } else if (fileType.contains("Glossary")) {
                                                                        return;
                                                                    } else if (fileType.contains("Turnitin Assignment")) {
                                                                        return;
                                                                    }

                                                                    name.complete(c.getTextContent() + type);
                                                                } else {
                                                                    name.complete(null);
                                                                }
                                                            }
                                                        });

                                                        if (name.getNow(null) == null) {
                                                            return;
                                                        }

                                                        resourceList.add(new AttachableResource(account.getUuid(),
                                                          clazz.getId() + File.separator + name.getNow(null).replace(" File", ""),
                                                          ARType.CLASS_SINGLE));

                                                        Thread dlThread = new Thread(() -> {
                                                            File classFile = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) +
                                                              File.separator + "Classes" + File.separator + clazz.getId());

                                                            File dlFile = new File(classFile, name.getNow(null).replace(" File", ""));

                                                            try {
                                                                if (!classFile.exists()) {
                                                                    classFile.mkdir();
                                                                }

                                                                if (!dlFile.exists()) {
                                                                    dlFile.createNewFile();
                                                                }

                                                                InputStream in = client.getPage(url).getWebResponse().getContentAsStream();
                                                                FileOutputStream out = new FileOutputStream(dlFile);

                                                                ReadableByteChannel rbc = Channels.newChannel(in);
                                                                out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                                            } catch (IOException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        });
                                                        dlThread.start();
                                                    }
                                                });
                                            } else if (node.getAttribute("class").contains("modtype_folder")) {
                                                HtmlPage folderPage = null;

                                                // What the living shit. How in fucks name does this work??
                                                // I don't know how the fuck this works, but it does. What the fuck.
                                                // *cue emo / metal music* WHAT THE FUCK IS GOING ONNNN
                                                try {
                                                    folderPage = node.getLastElementChild().getLastElementChild().getLastElementChild()
                                                      .getFirstElementChild().getLastElementChild().click();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    return;
                                                } catch (ClassCastException e) {
                                                    try {
                                                        folderPage = node.getLastElementChild().getLastElementChild()
                                                          .getLastElementChild().getFirstElementChild().getFirstElementChild().click();
                                                    } catch (IOException e1) {
                                                        e1.printStackTrace();
                                                    } catch (ClassCastException e1) {
                                                        try {
                                                            folderPage = node.getLastElementChild().getLastElementChild()
                                                              .getLastElementChild().getFirstElementChild().getFirstElementChild().click();
                                                        } catch (IOException e2) {
                                                            e2.printStackTrace();
                                                        } catch (ClassCastException e2) {
                                                            try {
                                                                folderPage = node.getLastElementChild().getLastElementChild()
                                                                  .getLastElementChild().getFirstElementChild().getFirstElementChild().click();
                                                            } catch (IOException e3) {
                                                                e3.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }

                                                if (folderPage == null) {
                                                    return;
                                                }

                                                HtmlDivision folderDiv = (HtmlDivision) folderPage.getByXPath("//*[@id=\"folder_tree0\"]").get(0);

                                                folderDiv.getFirstElementChild().getFirstElementChild().getChildElements().forEach(e -> {
                                                    if (e.getTagName().equalsIgnoreCase("ul")) {
                                                        e.getChildElements().forEach(e1 -> {
                                                            HtmlAnchor link = (HtmlAnchor) e1.getFirstElementChild().getFirstElementChild();

                                                            String dlUrl = link.getAttribute("href");
                                                            String name = link.getLastElementChild().getTextContent();

                                                            resourceList.add(new AttachableResource(account.getUuid(),
                                                              clazz.getId() + File.separator + name,
                                                              ARType.CLASS_SINGLE));

                                                            Thread dlThread = new Thread(() -> {
                                                                File classFile = new File(UtilFile.getInstance().getUserFolder(account.getUuid()) +
                                                                  File.separator + "Classes" + File.separator + clazz.getId());

                                                                File dlFile = new File(classFile, name);

                                                                try {
                                                                    if (!classFile.exists()) {
                                                                        classFile.mkdir();
                                                                    }

                                                                    if (!dlFile.exists()) {
                                                                        dlFile.createNewFile();
                                                                    } else {
                                                                        return;
                                                                    }

                                                                    InputStream in = client.getPage(dlUrl).getWebResponse().getContentAsStream();
                                                                    FileOutputStream out = new FileOutputStream(dlFile);

                                                                    ReadableByteChannel rbc = Channels.newChannel(in);
                                                                    out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                                                } catch (IOException e5) {
                                                                    e5.printStackTrace();
                                                                }
                                                            });
                                                            dlThread.start();
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                        t.start();
                                        threads.add(t);
                                    });
                                } catch (NullPointerException ignored) {
                                }
                            }
                        });
                    }
                });
            }

            if (gtopics != null) {
                gtopics.getChildElements().forEach(section -> {
                    CompletableFuture<String> sectionName = new CompletableFuture<>();
                    CompletableFuture<String> sectionDesc = new CompletableFuture<>();
                    section.getFirstElementChild().getChildElements().forEach(child -> {
                        if (child.getAttribute("class").equalsIgnoreCase("sectionname")) {
                            sectionName.complete(child.getTextContent());
                        } else if (child.getAttribute("class").equalsIgnoreCase("summary")) {
                            StringBuilder builder = new StringBuilder();
                            try {
                                child.getFirstElementChild().getChildElements().forEach(c -> {
                                    builder.append(c.getFirstElementChild().getTextContent());
                                });
                            } catch (NullPointerException ignored) {
                            }

                            if (builder.toString().length() == 0) {
                                builder.append("");
                            }

                            sectionDesc.complete(builder.toString());
                        } else if (child.getAttribute("class").contains("section") && child.getTagName().equalsIgnoreCase("ul")) {
                            //
                        }
                    });

                    if (sectionName.getNow(null) == null || sectionDesc.getNow(null) == null) {
                        return;
                    }

                    System.out.println("START");
                    System.out.println(sectionName.getNow(null));
                    System.out.println(sectionDesc.getNow(null));
                    System.out.println("END");
                });
            }

            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            resources.complete(resourceList);
        });
        everythingThread.start();

        try {
            System.out.println(new Gson().toJson(resources.get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        MoodleClass moodleClass;
        try {
            moodleClass = new MoodleClass(clazz.getId(), clazz.getClassName(), sections.get(), posts.get(),
              resources.get(), assignments.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println(new Gson().toJson(moodleClass));

        return moodleClass;
    }
}
