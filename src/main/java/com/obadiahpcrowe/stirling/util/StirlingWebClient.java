package com.obadiahpcrowe.stirling.util;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 9/9/17 at 9:07 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.util
 * Copyright (c) Obadiah Crowe 2017
 */
public class StirlingWebClient extends WebClient {

    public StirlingWebClient(BrowserVersion browserVersion) {
        super(browserVersion);
    }

    public StirlingWebClient getClient(DefaultCredentialsProvider provider, AjaxController controller) {
        if (provider != null)
            this.setCredentialsProvider(provider);
        if (controller != null)
            this.setAjaxController(controller);

        this.getOptions().setJavaScriptEnabled(true);
        this.getOptions().setUseInsecureSSL(true);
        this.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.getOptions().setThrowExceptionOnScriptError(false);
        this.getOptions().setCssEnabled(false);

        return this;
    }
}
