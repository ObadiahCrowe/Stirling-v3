package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.ClassManager;
import com.obadiahpcrowe.stirling.classes.StirlingClass;
import com.obadiahpcrowe.stirling.classes.enums.AssignmentType;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.ImportManager;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingPostable;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.exceptions.FuckDaymapException;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
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

    public DaymapClass getFullCourse(String username, String password, ImportableClass clazz, boolean importing) {
        // Completable holders
        CompletableFuture<String> dataId = new CompletableFuture<>();

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

                HtmlDivision dayDiv = (HtmlDivision) page.getByXPath("//*[@id=\"divEvents\"]/div/div[2]").get(0);
                String day = dayDiv.getTextContent().split(",")[0];

                String roomStr = "";

                for (DomElement element : div.getChildElements()) {
                    if (element.getAttribute("class").equalsIgnoreCase("L itm")) {
                        for (DomElement e : element.getChildElements()) {
                            if (e.getTextContent().contains(clazz.getClassName())) {
                                dataId.complete(element.getAttribute("data-id"));
                                roomStr = e.getTextContent().replace(clazz.getClassName(), "").replace(" ", "");
                                future.complete(element.getFirstElementChild().getTextContent());
                            }
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
                throw new FuckDaymapException("Could not retrieve timeslot from DayMap! Cannot complete import!");
            }
        } catch (InterruptedException | ExecutionException e) {
            return null;
        } catch (FuckDaymapException e) {
            e.printStackTrace();
            return null;
        }

        String dId = dataId.getNow("NO_ID");
        if (dId.equalsIgnoreCase("NO_ID")) {
            try {
                throw new FuckDaymapException("Cloud not retrieve data-id from DayMap! Cannot complete import!");
            } catch (FuckDaymapException e) {
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
                HtmlTableBody table = (HtmlTableBody) page.getByXPath("//*[@id=\"ctl00_cp_divTasks\"]/div/table/tbody").get(0);

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
                                        System.out.println("STACK TRACE!!!");
                                        e1.printStackTrace();
                                        System.exit(0);
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

        ImportManager mgr = ImportManager.getInstance();
        try {
            mgr.createClassFromDaymap(clazz.getId(), clazz.getClassName(), room.get(), timeSlot.get());
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
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/plans/class.aspx?id=" + dId);

                HtmlTableBody body = (HtmlTableBody) page.getByXPath("//*[@id=\"divFeed\"]/table[2]/tbody").get(0);

                body.getChildElements().forEach(e -> {
                    if (e.getFirstElementChild().getAttribute("class").equalsIgnoreCase("capb")) {
                        if (!e.getLastElementChild().getAttribute("onclick").contains("_taskClass")) {
                            Thread sectionThread = new Thread(() -> {
                                DomElement element = e;
                                CompletableFuture<String> title = new CompletableFuture<>();

                                element.getLastElementChild().getChildElements().forEach(el -> {
                                    if (el.getAttribute("class").equals("lpTitle")) {
                                        title.complete(el.getTextContent());
                                    }
                                });

                                String rawPostDate = element.getLastElementChild().getFirstElementChild().getTextContent();
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

                                String typeRaw = element.getFirstElementChild().getFirstElementChild().getTextContent();
                                String type = "";
                                if (typeRaw.contains("File")) {
                                    type = "Resource";
                                } else if (type.contains("Home Work")) {
                                    type = "Homework";
                                } else if (type.contains("Class Note")) {
                                    type = "Class Note";
                                } else if (type.contains("Class Post")) {
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
                                    String onClick = element.getLastElementChild().getAttribute("onclick")
                                      .replace("DMU.ViewPlan(", "").replace(");;", "");

                                    try {
                                        HtmlPage resPage = intClient.getPage("https://daymap.gihs.sa.edu.au/DayMap/curriculum/plan.aspx?id=" + onClick);
                                        HtmlDivision div = (HtmlDivision) resPage.getByXPath("//*[@class=\"lpAll\"]").get(0);

                                        StirlingPostable postable = new StirlingPostable(t,
                                          div.getTextContent().replace("\\u00a0", ""), Lists.newArrayList());

                                        if (type.equals("Class Note")) {
                                            noteList.add(postable);
                                        } else {
                                            homeList.add(postable);
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (type.equals("CLASSPOST")) {
                                    String onClick = element.getLastElementChild().getAttribute("onclick")
                                      .replace("return openMsg(", "").replace(", true);;", "");

                                    try {
                                        HtmlPage resPage = intClient.getPage("https://daymap.gihs.sa.edu.au/daymap/coms/Message.aspx?ID=" + onClick);
                                        HtmlDivision div = (HtmlDivision) resPage.getByXPath("//*[@id=\"msgBody\"]");

                                        noteList.add(new StirlingPostable(t, div.getTextContent().replace("\\u00a0", ""),
                                          Lists.newArrayList()));
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (type.equals("Resource")) {
                                    String onClick = element.getLastElementChild().getLastElementChild()
                                      .getLastElementChild().getLastElementChild().getAttribute("onclick")
                                      .replace("DMU.OpenAttachment(", "").replace(");", "");

                                    String name = element.getLastElementChild().getLastElementChild().getLastElementChild()
                                      .getLastElementChild().getTextContent();

                                    ClassManager classManager = ClassManager.getInstance();
                                    StirlingClass stirlingClass = classManager.getByOwner(clazz.getId());
                                    AttachableResource resource = new AttachableResource(stirlingClass.getUuid(), name);
                                    resList.add(resource);

                                    Thread res = new Thread(() -> {
                                        //
                                    });
                                    res.start();
                                }
                            });
                            sectionThread.start();
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

        try {
            System.out.println("SLOT: " + timeSlot.get().getSlotNumber());
            System.out.println("ROOM: " + room.get());
            System.out.println("TEACHER: " + teacher.get());

            //
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            return new DaymapClass(clazz.getId(), clazz.getClassName(), timeSlot.get(), room.get(), teacher.get(),
              classNotes.get(), homework.get(), resources.get(), assignments.get());
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }
}
