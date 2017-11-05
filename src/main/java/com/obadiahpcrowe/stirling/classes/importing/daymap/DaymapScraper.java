package com.obadiahpcrowe.stirling.classes.importing.daymap;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.obadiahpcrowe.stirling.classes.enums.LessonTimeSlot;
import com.obadiahpcrowe.stirling.classes.importing.obj.ImportableClass;
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
        // Completables
        CompletableFuture<LessonTimeSlot> timeSlot = new CompletableFuture<>();
        CompletableFuture<String> room = new CompletableFuture<>();
        CompletableFuture<String> teacher = new CompletableFuture<>();

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

        Thread teacherThread = new Thread(() -> {
            final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(provider, new NicelyResynchronizingAjaxController());
            CompletableFuture<String> future = new CompletableFuture<>();

            try {
                HtmlPage page = client.getPage("https://daymap.gihs.sa.edu.au/daymap/student/dayplan.aspx");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        teacherThread.start();

        try {
            System.out.println("SLOT: " + timeSlot.get().getSlotNumber());
            System.out.println("ROOM: " + room.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
