package com.obadiahpcrowe.stirling.sace.scrapers;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.sace.obj.SaceCompletion;
import com.obadiahpcrowe.stirling.sace.obj.SaceResult;
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

    public List<SaceCompletion> getCompletion(SaceUser user) {
        return null;
    }

    public static SaceScraper getInstance() {
        if (instance == null)
            instance = new SaceScraper();
        return instance;
    }
}
