package com.obadiahpcrowe.stirling.pod.signin;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodLine;
import com.obadiahpcrowe.stirling.pod.signin.enums.PodReason;
import com.obadiahpcrowe.stirling.util.StirlingWebClient;

import java.io.IOException;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 12/9/17 at 10:21 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.pod.signin
 * Copyright (c) Obadiah Crowe 2017
 */
public class PodScraper {

    private static PodScraper instance;

    public void signIn(int studentId, PodLine line, String assigningTeacher, PodReason reason) throws IOException {
        final WebClient client = new StirlingWebClient(BrowserVersion.CHROME).getClient(null,
          new NicelyResynchronizingAjaxController());

        HtmlPage page = client.getPage("http://service03.curric.gihs.sa.edu.au/staff/pod-signin");
        HtmlTextInput studentIdInput = (HtmlTextInput) page.getByXPath("//*[@id=\"studentid\"]").get(0);
        HtmlTextInput teacherInput = (HtmlTextInput) page.getByXPath("//*[@id=\"teacher\"]").get(0);
        HtmlSelect lineSelect = (HtmlSelect) page.getByXPath("//*[@id=\"lesson\"]").get(0);
        HtmlSelect reasonSelect = (HtmlSelect) page.getByXPath("//*[@id=\"reason\"]").get(0);
        HtmlButton button = (HtmlButton) page.getByXPath("//*[@id=\"submit\"]").get(0);

        studentIdInput.setText(String.valueOf(studentId));
        teacherInput.setText(assigningTeacher);
        lineSelect.setSelectedIndex(line.ordinal());
        reasonSelect.setSelectedIndex(reason.ordinal());

        button.click();
    }

    public static PodScraper getInstance() {
        if (instance == null)
            instance = new PodScraper();
        return instance;
    }
}
