package com.obadiahpcrowe.stirling.sace.scrapers;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.calendar.obj.ExamCalEntry;
import com.obadiahpcrowe.stirling.sace.obj.SaceCompletion;
import com.obadiahpcrowe.stirling.sace.obj.SaceResult;
import com.obadiahpcrowe.stirling.sace.obj.SaceSubject;
import com.obadiahpcrowe.stirling.sace.obj.SaceUser;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 13/9/17 at 3:38 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.sace.scrapers
 * Copyright (c) Obadiah Crowe 2017
 */
public class SaceScraper {

    private static SaceScraper instance;
    private AjaxController ajaxController = new NicelyResynchronizingAjaxController();
    private Gson gson = new Gson();

    public List<SaceResult> getResults(SaceUser user) throws IOException {
        final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(null, ajaxController);

        HtmlPage login = client.getPage("https://apps.sace.sa.edu.au/students-online/login.do");

        HtmlTextInput username = (HtmlTextInput) login.getByXPath("//*[@id=\"username\"]").get(0);
        HtmlPasswordInput password = (HtmlPasswordInput) login.getByXPath("//*[@id=\"password\"]").get(0);
        HtmlButton button = (HtmlButton) login.getByXPath("//*[@id=\"loginCommand\"]/div[2]/button").get(0);

        username.setText(user.getSaceId());
        password.setText(user.getSacePassword());

        login = button.click();

        HtmlPage results = client.getPage("https://apps.sace.sa.edu.au/students-online/results.do");
        HtmlDivision div = (HtmlDivision) results.getByXPath("//*[@id=\"main\"]/div[2]/div[7]").get(0);

        List<SaceResult> resultList = new ArrayList<>();
        for (DomElement element : div.getChildElements()) {
            if (element.getAttribute("class").equalsIgnoreCase("saceResult")) {
                for (DomElement e : element.getChildElements()) {
                    if (e.getTagName().equals("tbody")) {
                        for (DomElement tr : e.getChildElements()) {
                            String enrolYear = tr.getChildNodes().get(1).getTextContent();
                            String subject = tr.getChildNodes().get(3).getTextContent();
                            int credits = Integer.valueOf(tr.getChildNodes().get(5).getTextContent().replace(" ", ""));
                            String grade = tr.getChildNodes().get(7).getTextContent();

                            subject = subject.replace("\n", "").replaceAll("\\s+$", "").replaceAll("^\\s+", "");
                            grade = grade.replace("\n", "").replaceAll("\\s+$", "").replaceAll("^\\s+", "");

                            resultList.add(new SaceResult(enrolYear, subject, credits, grade));
                        }
                    }
                }
            }
        }

        return resultList;
    }

    public List<SaceCompletion> getCompletion(SaceUser user) throws IOException {
        final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(null, ajaxController);

        HtmlPage login = client.getPage("https://apps.sace.sa.edu.au/students-online/login.do");

        HtmlTextInput username = (HtmlTextInput) login.getByXPath("//*[@id=\"username\"]").get(0);
        HtmlPasswordInput password = (HtmlPasswordInput) login.getByXPath("//*[@id=\"password\"]").get(0);
        HtmlButton button = (HtmlButton) login.getByXPath("//*[@id=\"loginCommand\"]/div[2]/button").get(0);

        username.setText(user.getSaceId());
        password.setText(user.getSacePassword());

        login = button.click();

        List<SaceCompletion> completions = new ArrayList<>();
        HtmlPage results = client.getPage("https://apps.sace.sa.edu.au/students-online/checker.do");
        HtmlTable table = (HtmlTable) results.getByXPath("//*[@id=\"printWrapper\"]/div[1]/table").get(0);
        for (DomElement element : table.getChildElements()) {
            if (element.getTagName().equals("tbody")) {
                if (element.getAttribute("id") != null) {
                    if (element.getAttribute("id").length() > 3) {
                        for (DomElement e : element.getChildElements()) {
                            if (e.getTagName().equals("tr")) {
                                String name = e.getChildNodes().get(1).getTextContent();
                                int potentialCredits = Integer.valueOf(e.getChildNodes().get(3).getTextContent().replace(" ", ""));
                                int awardedCredits = Integer.valueOf(e.getChildNodes().get(5).getTextContent().replace(" ", ""));

                                String rawTarget = e.getChildNodes().get(7).getTextContent();
                                String[] parts = rawTarget.split("/");
                                int target = Integer.valueOf(parts[1].substring(1));

                                completions.add(new SaceCompletion(name, potentialCredits, awardedCredits, target));
                            }
                        }
                    }
                }
            }
        }

        return completions;
    }

    public List<ExamCalEntry> getSaceExams(SaceUser user) {
        //https://www.sace.sa.edu.au/web/sace-operations/calendars/south-australia?p_p_id=8&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-2&p_p_col_pos=1&p_p_col_count=2&_8_struts_action=%2Fcalendar%2Fview&_8_tabs1=month&_8_month=10&_8_day=13&_8_year=2017&_8_eventType=
        return null;
    }

    public List<SaceSubject> getSaceSubjects() {
        ////https://www.sace.sa.edu.au/learning/subjects
        return null;
    }

    public static SaceScraper getInstance() {
        if (instance == null)
            instance = new SaceScraper();
        return instance;
    }
}
