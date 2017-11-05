package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.enums.AssignmentType;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
import com.obadiahpcrowe.stirling.classes.obj.StirlingAssignment;
import com.obadiahpcrowe.stirling.classes.obj.StirlingResult;
import com.obadiahpcrowe.stirling.exceptions.FuckDaymapException;
import com.obadiahpcrowe.stirling.util.StirlingDate;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
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

    public DaymapClass getFullCourse(String username, String password, ImportableClass clazz) {
        // Completable holders
        CompletableFuture<String> dataId = new CompletableFuture<>();

        // Completable info
        CompletableFuture<LessonTimeSlot> timeSlot = new CompletableFuture<>();
        CompletableFuture<String> room = new CompletableFuture<>();
        CompletableFuture<String> teacher = new CompletableFuture<>();
        CompletableFuture<List<StirlingAssignment>> assignments = new CompletableFuture<>();

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

        Thread resourcesThread = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/plans/class.aspx?id=" + dId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            System.out.println("SLOT: " + timeSlot.get().getSlotNumber());
            System.out.println("ROOM: " + room.get());
            System.out.println("TEACHER: " + teacher.get());

            List<StirlingAssignment> assignmentsList = assignments.get();
            System.out.println("SIZE: " + assignmentsList.size());

            assignmentsList.forEach(a -> {
                System.out.println("ASSIGNMENT: " + a.getTitle());
                System.out.println("DESC: " + a.getDesc());
                System.out.println("TYPE: " + a.getType());
                System.out.println("FORMATIVE: " + a.getType());
                System.out.println("DATE: " + a.getDueDateTime().getDate());
                System.out.println("TIME: " + a.getDueDateTime().getTime());
                StirlingResult result = a.getResult();
                System.out.println("GRADE: " + result.getGrade());
                System.out.println("MAX: " + result.getMaxMarks());
                System.out.println("REC: " + result.getReceivedMarks());
                System.out.println("WEIGHTING: " + result.getWeighting());
                System.out.println("COMMENTS: " + result.getComments());
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
