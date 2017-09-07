package com.obadiahpcrowe.stirling.api;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.api.obj.APIController;
import com.obadiahpcrowe.stirling.api.obj.APIManager;
import com.obadiahpcrowe.stirling.api.obj.CallableAPI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 7/9/17 at 11:37 AM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.api
 * Copyright (c) Obadiah Crowe 2017
 */
@RestController
public class InfoAPI implements APIController {

    private Gson gson = new Gson();

    @CallableAPI(fields = "")
    @RequestMapping(value = "/stirling/v3/supportedCalls", method = RequestMethod.GET)
    public String getSupportedCalls() {
        return gson.toJson(APIManager.getInstance().getApis());
    }
}
