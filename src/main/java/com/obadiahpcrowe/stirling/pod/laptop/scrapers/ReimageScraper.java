package com.obadiahpcrowe.stirling.pod.laptop.scrapers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.pod.laptop.obj.ReimageLaptop;
import com.obadiahpcrowe.stirling.pod.laptop.enums.LaptopStatus;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:06 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.laptop.scrapers
 * Copyright (c) Obadiah Crowe 2017
 */
public class ReimageScraper {

    private static ReimageScraper instance;

    public String getLaptopData(String laptopName) throws IOException {
        Gson gson = new Gson();
        ReimageLaptop laptop = null;
        WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(null, null);

        HtmlPage page = client.getPage("http://service01.curric.gihs.sa.edu.au/deployment.html");

        HtmlTableBody body = (HtmlTableBody) page.getByXPath("/html/body/div/table/tbody").get(0);

        for (DomElement e : body.getChildElements()) {
            if (e.getTagName().equals("tr")) {
                String name = e.getChildNodes().get(0).getTextContent();
                String rawStatus = e.getChildNodes().get(1).getTextContent();
                String stage = e.getChildNodes().get(2).getTextContent();
                int percentage = Integer.valueOf(e.getChildNodes().get(3).getTextContent());
                LaptopStatus status = null;

                if (rawStatus.contains("Completed")) {
                    status = LaptopStatus.COMPLETED;
                } else if (rawStatus.contains("Active")) {
                    status = LaptopStatus.ACTIVE;
                } else if (rawStatus.contains("Failed")) {
                    status = LaptopStatus.FAILED;
                }

                if (name.contains(laptopName)) {
                    laptop = new ReimageLaptop(name, status, stage, percentage);
                }
            }
        }

        return gson.toJson(laptop);
    }

    public static ReimageScraper getInstance() {
        if (instance == null)
            instance = new ReimageScraper();
        return instance;
    }
}
