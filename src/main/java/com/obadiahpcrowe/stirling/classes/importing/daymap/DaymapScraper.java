package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.enums.AssignmentType;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.ImportAccount;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.enums.ImportSource;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportCredential;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.exceptions.DaymapException;
import com.obadiahpcrowe.stirling.resources.ARType;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;
import com.obadiahpcrowe.stirling.util.UtilFile;
import com.obadiahpcrowe.stirling.util.UtilTime;
import com.obadiahpcrowe.stirling.util.formatting.DateFormatter;
import com.obadiahpcrowe.stirling.util.formatting.NameFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 17/10/17 at 11:16 PM
 * Project: Stirling-v3
 * Package: com.obadiahpcrowe.stirling.classes.importing.daymap
 * Copyright (c) Obadiah Crowe 2017
 */
public class DaymapScraper {

    private static DaymapScraper instance;

    public static DaymapScraper getInstance() {
        if (instance == null)
            instance = new DaymapScraper();
        return instance;
    }

    public List<ImportableClass> getCourses(String username, String password) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("curric\\" + username, password.toCharArray());
            }
        });

        DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
        provider.addNTLMCredentials(username, password, null, -1, "localhost", "curric");

        final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

        HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/dayplan.aspx");
        HtmlDivision linkList = (HtmlDivision) page.getByXPath("//*[@id=\"ctl00_cp_divStudent\"]/div/table/tbody/tr[1]/td[3]/div").get(0);
        List<ImportableClass> classes = Lists.newArrayList();

        for (DomNode node : linkList.getChildElements()) {
            classes.add(new ImportableClass(node.getTextContent(), node.getAttributes().getNamedItem("href").getNodeValue().replace("plans/class.aspx?id=", "")));
        }

        return classes;
    }

    public boolean areCredentialsValid(ImportCredential credential) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("curric\\" + credential.getUsername(), credential.getPassword().toCharArray());
            }
        });

        DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
        provider.addNTLMCredentials(credential.getUsername(), credential.getPassword(), null, -1, "localhost", "curric");

        final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

        try {
            HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/dayplan.aspx");
            return true;
        } catch (IOException | ClassCastException e) {
            return false;
        }
    }

    public DaymapClass getFullCourse(ImportAccount account, ImportableClass clazz, boolean importing) {
        // Completable holders
        CompletableFuture<String> dataId = new CompletableFuture<>();

        String username = account.getCredentials().get(ImportSource.DAYMAP).getUsername();
        String password = account.getCredentials().get(ImportSource.DAYMAP).getPassword();

        // Completable info
        CompletableFuture<LessonTimeSlot> timeSlot = new CompletableFuture<>();
        CompletableFuture<String> room = new CompletableFuture<>();
        CompletableFuture<String> teacher = new CompletableFuture<>();
        CompletableFuture<List<StirlingAssignment>> assignments = new CompletableFuture<>();
        CompletableFuture<List<StirlingPostable>> classNotes = new CompletableFuture<>();
        CompletableFuture<List<StirlingPostable>> homework = new CompletableFuture<>();
        CompletableFuture<List<AttachableResource>> resources = new CompletableFuture<>();

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("curric\\" + username, password.toCharArray());
            }
        });

        DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
        provider.addNTLMCredentials(username, password, null, -1, "localhost", "curric");

        // Room and Class Slot
        Thread slot = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());
            CompletableFuture<String> future = new CompletableFuture<>();

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/dayplan.aspx");
                HtmlDivision div = (HtmlDivision) page.getByXPath("//*[@id=\"divEvents\"]/div").get(0);

                String day = "";
                boolean found = false;
                String roomStr = "";

                for (DomElement element : div.getChildElements()) {
                    if (element.getAttribute("class").equalsIgnoreCase("L itm")) {
                        for (DomElement e : element.getChildElements()) {
                            if (e.getTextContent().contains(clazz.getClassName())) {
                                dataId.complete(element.getAttribute("data-id"));
                                roomStr = e.getTextContent().replace(clazz.getClassName(), "").replace(" ", "");
                                future.complete(element.getFirstElementChild().getTextContent());
                                found = true;
                            }
                        }
                    } else if (element.getAttribute("class").equalsIgnoreCase("diaryDay")) {
                        if (!found) {
                            day = element.getTextContent().split(",")[0];
                        }
                    }
                }

                String slotStr = future.getNow("NO_SLOT");

                if (slotStr.equalsIgnoreCase("NO_SLOT")) {
                    System.out.println("no slot found");
                    return;
                }

                timeSlot.complete(LessonTimeSlot.getFromString(slotStr, day));
                room.complete(roomStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        slot.start();

        try {
            if (timeSlot.get() == null) {
                throw new DaymapException("Could not retrieve timeslot from DayMap! Cannot complete import!");
            }
        } catch (InterruptedException | ExecutionException e) {
            return null;
        } catch (DaymapException e) {
            e.printStackTrace();
            return null;
        }

        String dId = dataId.getNow("NO_ID");
        if (dId.equalsIgnoreCase("NO_ID")) {
            try {
                throw new DaymapException("Cloud not retrieve data-id from DayMap! Cannot complete import!");
            } catch (DaymapException e) {
                e.printStackTrace();
            }
        }

        Thread teacherThread = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/plans/lesson.aspx?ID=" + dId);
                HtmlTableCell teacherCell = (HtmlTableCell) page.getByXPath("//*[@id=\"ctl00_cp_tdTeacher\"]").get(0);
                teacher.complete(teacherCell.getTextContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        teacherThread.start();

        Thread assessmentThread = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());
            List<StirlingAssignment> asses = Lists.newArrayList();
            List<Thread> threads = Lists.newArrayList();

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/plans/lesson.aspx?ID=" + dId);

                HtmlTableBody table;
                try {
                    table = (HtmlTableBody) page.getByXPath("//*[@id=\"ctl00_cp_divTasks\"]/div/table/tbody").get(0);
                } catch (IndexOutOfBoundsException e) {
                    assignments.complete(asses);
                    return;
                }

                table.getChildElements().forEach(e -> {
                    if (e.getTagName().equalsIgnoreCase("tr")) {
                        if (e.getFirstElementChild().getTagName().equalsIgnoreCase("td")) {
                            DomElement aEl = e.getFirstElementChild().getFirstElementChild();
                            if (aEl.getTagName().equalsIgnoreCase("a")) {
                                String id = aEl.getTextContent();
                                Thread assThread = new Thread(() -> {
                                    final WebClient aClient = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

                                    try {
                                        HtmlPage assPage = aClient.getPage("https://daymap.gihs.sa.edu.au/daymap/student/assignment.aspx?TaskID=" + id);

                                        HtmlTableCell name = (HtmlTableCell) assPage
                                          .getByXPath("//*[@id=\"ctl00_ctl00_cp_cp_divResults\"]/table[1]/tbody/tr[1]/td[2]").get(0);

                                        HtmlTableCell dueDate = (HtmlTableCell) assPage
                                          .getByXPath("//*[@id=\"ctl00_ctl00_cp_cp_divResults\"]/table/tbody/tr[4]/td[4]").get(0);

                                        HtmlTableCell assType = (HtmlTableCell) assPage
                                          .getByXPath("//*[@id=\"ctl00_ctl00_cp_cp_divResults\"]/table[1]/tbody/tr[2]/td[4]").get(0);

                                        String rawType = assType.getTextContent();
                                        AssignmentType type;
                                        boolean formative = false;

                                        if (rawType.contains("Assignment")) {
                                            type = AssignmentType.ASSIGNMENT;
                                        } else if (rawType.contains("Test")) {
                                            type = AssignmentType.TEST;
                                        } else if (rawType.contains("Essay")) {
                                            type = AssignmentType.ESSAY;
                                        } else if (rawType.contains("Formative")) {
                                            type = AssignmentType.OTHER;
                                            formative = true;
                                        } else {
                                            type = AssignmentType.OTHER;
                                        }

                                        String rawDate = dueDate.getTextContent();
                                        String date;
                                        String time;
                                        if (rawDate.contains(":")) {
                                            date = rawDate.split(" ")[0];
                                            time = rawDate.split(" ")[1];
                                        } else {
                                            date = rawDate;
                                            time = "";
                                        }

                                        String grade = "";
                                        String mark = "";
                                        String comments = "";

                                        try {
                                            HtmlTable results = (HtmlTable) assPage.getByXPath("//*[@id=\"ctl00_ctl00_cp_cp_divResults\"]/table[2]").get(0);
                                            HtmlTableBody body = results.getBodies().get(0);
                                            for (HtmlTableRow row : body.getRows()) {
                                                String first = row.getFirstChild().getTextContent();
                                                String content = row.getChildNodes().get(1).getFirstChild().getTextContent();
                                                if (first.contains("Grade:")) {
                                                    grade = content;
                                                } else if (first.contains("Mark:")) {
                                                    mark = content;
                                                } else if (first.contains("Comments:")) {
                                                    comments = content;
                                                }
                                            }
                                        } catch (IndexOutOfBoundsException | NullPointerException ignored) {
                                        }

                                        int received;
                                        int max;

                                        if (!mark.equalsIgnoreCase("")) {
                                            received = Integer.valueOf(mark.split("/")[0].replace(" ", ""));
                                            max = Integer.valueOf(mark.split("/")[1].replace(" ", ""));
                                        } else {
                                            received = 0;
                                            max = 0;
                                        }

                                        StirlingResult result = new StirlingResult(received, max, grade, 0, comments);
                                        StirlingAssignment assignment = new StirlingAssignment(name.getTextContent(), "", type, formative, result,
                                          new StirlingDate(date, time));
                                        asses.add(assignment);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                });
                                assThread.start();
                                threads.add(assThread);
                            }
                        }
                    }
                });

                threads.forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                assignments.complete(asses);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        assessmentThread.start();

        try {
            ImportManager.getInstance().createClassFromDaymap(clazz.getId(), clazz.getClassName(), room.get(), timeSlot.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Thread resourcesThread = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

            List<StirlingPostable> noteList = Lists.newArrayList();
            List<StirlingPostable> homeList = Lists.newArrayList();
            List<AttachableResource> resList = Lists.newArrayList();

            List<Thread> threads = Lists.newArrayList();

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/plans/class.aspx?id=" + clazz.getId());

                HtmlTableBody body;
                try {
                    body = (HtmlTableBody) page.getByXPath("//*[@id=\"divFeed\"]/table[2]/tbody").get(0);
                } catch (IndexOutOfBoundsException e) {
                    classNotes.complete(noteList);
                    homework.complete(homeList);
                    resources.complete(resList);
                    return;
                }

                body.getChildElements().forEach(e -> {
                    if (e.getFirstElementChild().getAttribute("class").equalsIgnoreCase("capb")) {
                        if (!e.getLastElementChild().getAttribute("onclick").contains("_taskClass")) {
                            Thread sectionThread = new Thread(() -> {
                                CompletableFuture<String> title = new CompletableFuture<>();

                                e.getLastElementChild().getChildElements().forEach(el -> {
                                    if (el.getAttribute("class").equals("lpTitle")) {
                                        title.complete(el.getTextContent());
                                    }
                                });

                                String rawPostDate = e.getLastElementChild().getFirstElementChild().getTextContent();
                                if (rawPostDate.contains("<br/>")) {
                                    try {
                                        rawPostDate = rawPostDate.substring(0, 10);
                                    } catch (IndexOutOfBoundsException e1) {
                                        try {
                                            rawPostDate = rawPostDate.substring(0, rawPostDate.indexOf("<br/>"));
                                        } catch (IndexOutOfBoundsException e2) {
                                            rawPostDate = rawPostDate.substring(0, 9);
                                        }
                                    }
                                }

                                String yr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                                if (!rawPostDate.endsWith(yr)) {
                                    String[] parts = rawPostDate.split(yr);
                                    rawPostDate = parts[0] + yr;
                                }

                                if (rawPostDate.contains("comment")) {
                                    rawPostDate = rawPostDate.split(" ")[rawPostDate.split(" ").length - 1];
                                }

                                String typeRaw = e.getFirstElementChild().getFirstElementChild().getTextContent();
                                String type;
                                if (typeRaw.contains("File")) {
                                    type = "Resource";
                                } else if (typeRaw.contains("Home Work")) {
                                    type = "Homework";
                                } else if (typeRaw.contains("Class Note")) {
                                    type = "Class Note";
                                } else if (typeRaw.contains("Class Post")) {
                                    type = "CLASSPOST";
                                } else {
                                    type = "Other";
                                }

                                String t = title.getNow(null);
                                if (t == null) {
                                    if (type.equals("CLASSPOST")) {
                                        t = "Class Note from " + rawPostDate;
                                    } else {
                                        t = type + " from " + rawPostDate;
                                    }
                                }

                                final WebClient intClient = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());
                                if (type.equals("Class Note") || type.equals("Homework")) {
                                    String onClick = e.getLastElementChild().getAttribute("onclick")
                                      .replace("DMU.ViewPlan(", "").replace(");;", "");

                                    String rawDate = e.getLastElementChild().getFirstElementChild().getTextContent();
                                    rawDate = rawDate.split("2017")[0];

                                    StirlingDate date = new StirlingDate(rawDate + "2017", UtilTime.getInstance().getFriendlyTime());

                                    CompletableFuture<String> click = new CompletableFuture<>();
                                    if (onClick.equalsIgnoreCase("")) {
                                        System.out.println(e.asXml());
                                        e.getLastElementChild().getChildElements().forEach(e1 -> {
                                            if (e1.getAttribute("class").equalsIgnoreCase("lpTitle")) {
                                                click.complete(e1.getFirstElementChild().getAttribute("href")
                                                  .replace("javascript:DMU.ViewPlan(", "").replace(");", ""));
                                            }
                                        });
                                    } else {
                                        click.complete(onClick);
                                    }

                                    String fClick = click.getNow("");
                                    if (fClick.equalsIgnoreCase("")) {
                                        return;
                                    }

                                    try {
                                        HtmlPage resPage = intClient.getPage("https://daymap.gihs.sa.edu.au/DayMap/curriculum/plan.aspx?id=" + fClick);
                                        HtmlDivision div = (HtmlDivision) resPage.getByXPath("//*[@class=\"lpAll\"]").get(0);

                                        StirlingPostable postable = new StirlingPostable(t,
                                          div.getTextContent().replace("\\u00a0", ""), Lists.newArrayList(), date);

                                        if (type.equals("Class Note")) {
                                            noteList.add(postable);
                                        } else {
                                            homeList.add(postable);
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (type.equals("CLASSPOST")) {
                                    String onClick = e.getLastElementChild().getAttribute("onclick")
                                      .replace("return openMsg(", "").replace(", true);;", "");

                                    try {
                                        HtmlPage resPage = intClient.getPage("https://daymap.gihs.sa.edu.au/daymap/coms/Message.aspx?ID=" + onClick);
                                        HtmlDivision div = (HtmlDivision) resPage.getByXPath("//*[@id=\"msgBody\"]").get(0);

                                        CompletableFuture<String> rawDate = new CompletableFuture<>();
                                        HtmlDivision header = (HtmlDivision) resPage.getByXPath("//*[@id=\"ctl00_cp_divMsg\"]/div[1]/div[1]").get(0);
                                        header.getChildElements().forEach(c -> {
                                            if (c.getAttribute("class").contains("msgSentOn")) {
                                                rawDate.complete(c.getTextContent());
                                            }
                                        });

                                        String date = rawDate.getNow(null);
                                        if (date == null) {
                                            noteList.add(new StirlingPostable(t, div.getTextContent().replace("\\u00a0", ""),
                                              Lists.newArrayList()));
                                            return;
                                        } else {
                                            String[] parts = date.split("2017 ");
                                            date = parts[0];
                                            date = date.split("posted ")[1];

                                            StirlingDate d = new StirlingDate(date + "2017", DateFormatter.formatTime(parts[1]));
                                            noteList.add(new StirlingPostable(t, div.getTextContent().replace("\\u00a0", ""),
                                              Lists.newArrayList(), d));
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (type.equals("Resource")) {
                                    CompletableFuture<String> onClick = new CompletableFuture<>();
                                    CompletableFuture<String> name = new CompletableFuture<>();

                                    HtmlDivision div = (HtmlDivision) e.getLastElementChild().getLastElementChild();
                                    div.getChildElements().forEach(el -> {
                                        if (el.getAttribute("class").equals("fLinkDiv")) {
                                            onClick.complete(el.getFirstElementChild().getAttribute("onclick"));

                                            name.complete(el.getFirstElementChild().getTextContent().trim()
                                              .replace("/", "-").replace("\n", "")
                                              .replace("\t", "").replace("\\u00a0", ""));
                                        }
                                    });

                                    String n = name.getNow(null);
                                    if (n == null) {
                                        try {
                                            throw new DaymapException("Resource name is null!");
                                        } catch (DaymapException e1) {
                                            e1.printStackTrace();
                                            return;
                                        }
                                    }

                                    ClassManager classManager = ClassManager.getInstance();
                                    StirlingClass stirlingClass = classManager.getByOwner(clazz.getId());
                                    AttachableResource resource = new AttachableResource(stirlingClass.getUuid(), n.trim(), ARType.CLASS);

                                    CompletableFuture<Boolean> contains = new CompletableFuture<>();
                                    resList.forEach(res -> {
                                        if (res.getFilePath().equalsIgnoreCase(resource.getFilePath())) {
                                            contains.complete(true);
                                        }
                                    });

                                    if (!contains.getNow(false)) {
                                        resList.add(resource);
                                    }

                                    Thread res = new Thread(() -> {
                                        final WebClient dlClient = new StirlingWebClient(BrowserVersion.CHROME)
                                          .getClient(provider, new NicelyResynchronizingAjaxController());

                                        try {
                                            HtmlPage dummyPage = dlClient.getPage("https://daymap.gihs.sa.edu.au/daymap/student/dayplan.aspx");
                                            ScriptResult result = dummyPage.executeJavaScript(onClick.get());
                                            InputStream in = result.getNewPage().getWebResponse().getContentAsStream();

                                            File cFile = new File(UtilFile.getInstance().getStorageLoc() +
                                              File.separator + "Classes" + File.separator + stirlingClass.getUuid() +
                                              File.separator + "Resources" + File.separator + n.trim());

                                            if (!cFile.exists()) {
                                                cFile.createNewFile();
                                            } else {
                                                return;
                                            }

                                            FileOutputStream out = new FileOutputStream(cFile);
                                            ReadableByteChannel rbc = Channels.newChannel(in);
                                            out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                        } catch (IOException | InterruptedException | ExecutionException e1) {
                                            e1.printStackTrace();
                                        }
                                    });
                                    res.start();
                                }
                            });
                            sectionThread.start();
                            threads.add(sectionThread);
                        }
                    }
                });
            } catch (IOException | IndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
            }

            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            classNotes.complete(noteList);
            homework.complete(homeList);
            resources.complete(resList);
        });
        resourcesThread.start();

        DaymapClass daymapClass = null;
        try {
            daymapClass = new DaymapClass(clazz, timeSlot.get(), room.get(), NameFormatter.formatName(teacher.get().trim()),
              classNotes.get(), homework.get(), resources.get(), assignments.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ImportManager importManager = ImportManager.getInstance();

        Thread addThread = new Thread(() -> {
            try {
                StirlingClass stirlingClass = ClassManager.getInstance().getByOwner(clazz.getId());
                List<String> owners = Lists.newArrayList();

                try {
                    owners.addAll(stirlingClass.getOwners());
                    if (!owners.contains(teacher.get().trim())) {
                        owners.add(teacher.get().trim());
                    }
                } catch (NullPointerException ignored) {
                }

                importManager.addNotesToDaymapClass(clazz.getId(), classNotes.get());
                importManager.addHomeworkToDaymapClass(clazz.getId(), homework.get());
                importManager.addResourcesToDaymapClass(clazz.getId(), resources.get());
                ClassManager.getInstance().updateField(stirlingClass, "room", room.get());
                ClassManager.getInstance().updateField(stirlingClass, "owners", owners);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        addThread.start();

        List<DaymapClass> classes = Lists.newArrayList();
        try {
            classes.addAll(account.getDaymapClasses());
        } catch (NullPointerException e) {
        }

        CompletableFuture<Boolean> exists = new CompletableFuture<>();
        classes.forEach(c -> {
            if (clazz.getId().equals(c.getId())) {
                exists.complete(true);
            }
        });

        if (!exists.getNow(false)) {
            classes.add(daymapClass);
        }

        importManager.updateField(account, "daymapClasses", classes);
        return daymapClass;
    }
}
